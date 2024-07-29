package au.net.causal.jgitcrypt;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Simple command line tool that can generate keys, encrypt, and decrypt files.
 */
public class JGitcryptApp
{
    public static void main(String... args)
    throws IOException, GitcryptSecurityException
    {
        if (args.length < 1)
        {
            printUsage();
            System.exit(1);
        }

        JGitcryptApp app = new JGitcryptApp();

        String command = args[0];

        switch (command)
        {
            case "decrypt" -> app.decrypt(requiredArgToFile(args, 1), argToFile(args, 2), argToFile(args, 3));
            case "encrypt" -> app.encrypt(requiredArgToFile(args, 1), argToFile(args, 2), argToFile(args, 3));
            case "generatekey" -> app.generateKey(argToFile(args, 1));
            default -> { printUsage(); System.exit(1); }
        }
    }

    private static void printUsage()
    {
        System.err.println("Usage:");
        System.err.println("jgitcrypt decrypt <keyfile> [<inputfile>] [<outputfile>]");
        System.err.println("jgitcrypt encrypt <keyfile> [<inputfile>] [<outputfile>]");
        System.err.println("    (if inputfile not specified, uses stdin, if outputfile not specified, uses stdout)");
        System.err.println("jgitcrypt generatekey [<keyfile>]");
        System.err.println("    (if keyfile not specified, uses stdout)");
    }

    /**
     * Converts arg at the specified index to a file, or exits with a usage message if the index is out of range.
     *
     * @param args args array.
     * @param argIndex index of arg to use.
     *
     * @return the file at the specified arg index.
     */
    private static Path requiredArgToFile(String[] args, int argIndex)
    {
        if (argIndex >= args.length)
        {
            printUsage();
            System.exit(1);
        }
        return argToFile(args, argIndex);
    }

    /**
     * Converts arg at the specified index to a file, or null if the index is out of range.
     *
     * @param args args array.
     * @param argIndex index of arg to use.
     *
     * @return the file at the specified arg index or null.
     */
    private static Path argToFile(String[] args, int argIndex)
    {
        if (argIndex >= args.length)
            return null;

        return Path.of(args[argIndex]);
    }

    /**
     * Decrypts a file.
     *
     * @param keyFile gitcrypt keyfile to use.
     * @param inputFile encrypted file to decrypt.  If null, will use System.in.
     * @param outputFile decrypted file to write.  If null, will use System.out.
     *
     * @throws IOException if an I/O error occurs.
     * @throws GitcryptSecurityException if Java security does not have providers set up for Gitcrypt.
     */
    public void decrypt(Path keyFile, Path inputFile, Path outputFile)
    throws IOException, GitcryptSecurityException
    {
        GitcryptKey key;
        try (InputStream in = inputStreamForArg(keyFile))
        {
            key = GitcryptKey.read(in);
        }

        try (VerifiableInputStream decryptedIn = new GitcryptDecoder(key).decode(inputStreamForArg(inputFile));
             OutputStream out = outputStreamForArg(outputFile))
        {
            decryptedIn.transferTo(out);
            decryptedIn.verify();
        }
    }

    /**
     * Encrypts a file.
     *
     * @param keyFile gitcrypt keyfile to use.
     * @param inputFile file to encrypt.  If null, will use System.in.
     * @param outputFile encrypted file to write.  If null, will use System.out.
     *
     * @throws IOException if an I/O error occurs.
     * @throws GitcryptSecurityException if Java security does not have providers set up for Gitcrypt.
     */
    public void encrypt(Path keyFile, Path inputFile, Path outputFile)
    throws IOException, GitcryptSecurityException
    {
        GitcryptKey key;
        try (InputStream in = inputStreamForArg(keyFile))
        {
            key = GitcryptKey.read(in);
        }

        try (InputStream in = inputStreamForArg(inputFile);
             OutputStream out = outputStreamForArg(outputFile))
        {
            new GitcryptDecoder(key).encode(in, out);
        }
    }

    /**
     * Generates a new gitcrypt key file.
     *
     * @param keyFile the key file to generate.  If null, will use System.out.
     *
     * @throws IOException if an I/O error occurs.
     * @throws GitcryptSecurityException if Java security does not have providers set up for Gitcrypt.
     */
    public void generateKey(Path keyFile)
    throws IOException, GitcryptSecurityException
    {
        GitcryptKey key = GitcryptKey.generate();
        try (OutputStream out = outputStreamForArg(keyFile))
        {
            key.write(out);
        }
    }

    /**
     * Creates an input stream for a file, or if it is null uses System.in.
     */
    private InputStream inputStreamForArg(Path path)
    throws IOException
    {
        if (path == null)
            return System.in;
        else
            return Files.newInputStream(path);
    }

    /**
     * Creates an output stream for a file, or if it is null uses System.out.
     */
    private OutputStream outputStreamForArg(Path path)
    throws IOException
    {
        if (path == null)
            return System.out;
        else
            return Files.newOutputStream(path);
    }
}
