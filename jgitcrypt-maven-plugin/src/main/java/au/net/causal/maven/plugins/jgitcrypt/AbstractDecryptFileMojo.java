package au.net.causal.maven.plugins.jgitcrypt;

import au.net.causal.jgitcrypt.GitcryptDecoder;
import au.net.causal.jgitcrypt.GitcryptKey;
import au.net.causal.jgitcrypt.GitcryptSecurityException;
import au.net.causal.jgitcrypt.VerifiableInputStream;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Decrypt a single file, subclasses define source and target as streams.
 */
public abstract class AbstractDecryptFileMojo extends AbstractKeyBasedMojo
{
    /**
     * Stream of encrypted data to decrypt.
     */
    protected abstract InputStream sourceInputStream()
    throws IOException;

    /**
     * Stream to write decrypted data to.
     */
    protected abstract OutputStream targetOutputStream()
    throws IOException;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException
    {
        GitcryptKey key = loadGitcryptKey();
        GitcryptDecoder decoder = new GitcryptDecoder(key);

        try (VerifiableInputStream decryptIs = decoder.decode(sourceInputStream());
             OutputStream os = targetOutputStream())
        {
            decryptIs.transferTo(os);
            decryptIs.verify();
        }
        catch (IOException | GitcryptSecurityException e)
        {
            throw new MojoExecutionException(e.getMessage(), e);
        }
    }
}
