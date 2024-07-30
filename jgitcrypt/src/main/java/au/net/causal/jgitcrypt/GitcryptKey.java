package au.net.causal.jgitcrypt;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * A Gitcrypt key for encrypting and decrypting files.
 *
 * See <a href="https://github.com/AGWA/git-crypt/blob/08dbdcfed4fb182c0efaacb32a6c46481ced095b/key.cpp">key.cpp in Gitcrypt</a>
 */
public class GitcryptKey
{
    private static final int KEY_FIELD_VERSION = 1;
    private static final int KEY_FIELD_AES_KEY = 3;
    private static final int KEY_FIELD_HMAC_KEY = 5;

    /**
     * Version that defines the format of the key file.
     */
    private static final int FORMAT_VERSION = 2;

    private static final int AES_KEY_LEN_BYTES = 32;
    private static final int HMAC_KEY_LEN_BYTES = 64;

    private static final byte[] expectedSignature = "\u0000GITCRYPTKEY".getBytes(StandardCharsets.US_ASCII);

    private final Map<Integer, byte[]> headerFields;
    private final Map<Integer, byte[]> entries;

    public GitcryptKey(Map<Integer, byte[]> headerFields, Map<Integer, byte[]> entries)
    {
        this.headerFields = new LinkedHashMap<>(headerFields);
        this.entries = new LinkedHashMap<>(entries);
    }

    /**
     * @return the value of the version field in the key file.  Not the same as the file format version.
     */
    public int getVersion()
    {
        byte[] data = entries.get(KEY_FIELD_VERSION);
        if (data == null)
            return 0;
        else
            return ByteBuffer.wrap(data).getInt();
    }

    /**
     * @return the AES key used for encrypting and decrypting data.
     */
    public byte[] getAesKey()
    {
        return entries.get(KEY_FIELD_AES_KEY);
    }

    /**
     * @return the HMAC key used for verifying data.
     */
    public byte[] getHmacKey()
    {
        return entries.get(KEY_FIELD_HMAC_KEY);
    }

    public Map<Integer, byte[]> getHeaderFields()
    {
        return Collections.unmodifiableMap(headerFields);
    }

    public Map<Integer, byte[]> getEntries()
    {
        return Collections.unmodifiableMap(entries);
    }

    /**
     * Saves the key to a stream.
     */
    public void write(OutputStream os)
    throws IOException
    {
        DataOutputStream data = new DataOutputStream(os);

        data.write(expectedSignature);
        data.writeInt(FORMAT_VERSION);
        GitcryptIO.writeFields(data, getHeaderFields());
        GitcryptIO.writeFields(data, getEntries());
    }

    /**
     * Reads a key from a byte stream.
     *
     * @param is the stream to read from.
     *
     * @return the key.
     *
     * @throws GitcryptFileFormatException if the data in the stream is not recognized as a key or the key data is malformed.
     * @throws IOException if an I/O error occurs.
     */
    public static GitcryptKey read(InputStream is)
    throws GitcryptFileFormatException, IOException
    {
        DataInputStream data = new DataInputStream(is);

        //Signature
        byte[] signature = new byte[expectedSignature.length];
        data.readFully(signature);
        if (!Arrays.equals(expectedSignature, signature))
            throw new GitcryptFileFormatException("Invalid signature - not a Gitcrypt key file");

        //Version
        int version = data.readInt();
        if (version != FORMAT_VERSION)
            throw new GitcryptFileFormatException("Invalid Gitcrypt key file version: " + version);

        Map<Integer, byte[]> headerFields = GitcryptIO.readFields(data);
        Map<Integer, byte[]> entries = GitcryptIO.readFields(data);

        return new GitcryptKey(headerFields, entries);
    }

    /**
     * Creates a new random key.
     */
    public static GitcryptKey generate()
    throws GitcryptSecurityException
    {
        return generate(0);
    }

    /**
     * Creates a new random key with the specified version.
     */
    public static GitcryptKey generate(int version)
    throws GitcryptSecurityException
    {
        try
        {
            KeyGenerator aesKeyGenerator = KeyGenerator.getInstance("AES");
            aesKeyGenerator.init(AES_KEY_LEN_BYTES * Byte.SIZE);
            KeyGenerator hmacKeyGenerator = KeyGenerator.getInstance("HmacSHA1");
            hmacKeyGenerator.init(HMAC_KEY_LEN_BYTES * Byte.SIZE);
            return generate(version, aesKeyGenerator, hmacKeyGenerator);
        }
        catch (NoSuchAlgorithmException e)
        {
            throw new GitcryptSecurityException(e);
        }
    }

    /**
     * Creates a new random key with the specified version and key generators.
     */
    public static GitcryptKey generate(int version, KeyGenerator aesKeyGenerator, KeyGenerator hmacKeyGenerator)
    {
        SecretKey aesKey = aesKeyGenerator.generateKey();
        byte[] aesKeyBytes = aesKey.getEncoded();

        SecretKey hmacKey = hmacKeyGenerator.generateKey();
        byte[] hmacKeyBytes = hmacKey.getEncoded();

        byte[] versionBytes = ByteBuffer.allocate(4).putInt(version).array();

        return new GitcryptKey(Map.of(), Map.of(
                KEY_FIELD_VERSION, versionBytes,
                KEY_FIELD_AES_KEY, aesKeyBytes,
                KEY_FIELD_HMAC_KEY, hmacKeyBytes
        ));
    }
}