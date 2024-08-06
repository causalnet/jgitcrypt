package au.net.causal.maven.plugins.jgitcrypt;

import au.net.causal.jgitcrypt.GitcryptDecoder;
import au.net.causal.jgitcrypt.GitcryptKey;
import au.net.causal.jgitcrypt.GitcryptSecurityException;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.codehaus.plexus.util.DirectoryScanner;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Decrypt multiple git-crypt encrypted files.  Original, encrypted files are not modified, but decrypted files are
 * saved to another directory.
 */
@Mojo(name="decrypt-files")
public class DecryptFilesMojo extends AbstractKeyBasedMojo
{
    /**
     * Files to decrypt and where to decrypt them to.
     */
    @Parameter(required = true)
    private List<FileSet> fileSets = new ArrayList<>();

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException
    {
        GitcryptKey key = loadGitcryptKey();
        GitcryptDecoder decoder = new GitcryptDecoder(key);

        DirectoryScanner scanner = new DirectoryScanner();

        for (FileSet fileSet : fileSets)
        {
            if (fileSet.getIncludes().isEmpty())
                fileSet.setIncludes(List.of("**"));

            scanner.setBasedir(fileSet.getDirectory());
            scanner.setIncludes(fileSet.getIncludes().toArray(String[]::new));
            scanner.setExcludes(fileSet.getExcludes().toArray(String[]::new));

            scanner.scan();

            for (String selectedFileName : scanner.getIncludedFiles())
            {
                Path selectedFile = fileSet.getDirectory().toPath().resolve(selectedFileName);
                Path targetFile = fileSet.getTargetDirectory().toPath().resolve(selectedFileName);

                getLog().info("Decrypting " + selectedFile.toAbsolutePath() + " to " + targetFile.toAbsolutePath());

                try
                {
                    Files.createDirectories(targetFile.getParent());
                }
                catch (IOException e)
                {
                    throw new MojoExecutionException("Failed to create directory " + targetFile.getParent().toAbsolutePath() + ": " + e, e);
                }

                try (InputStream decryptedIs = decoder.decode(Files.newInputStream(selectedFile));
                     OutputStream os = Files.newOutputStream(targetFile))
                {
                    decryptedIs.transferTo(os);
                }
                catch (IOException | GitcryptSecurityException e)
                {
                    throw new MojoExecutionException("Error decrypting file " + selectedFile.toAbsolutePath() + ": " + e, e);
                }
            }
        }
    }
}
