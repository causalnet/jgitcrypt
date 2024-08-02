package au.net.causal.maven.plugins.jgitcrypt;

import au.net.causal.jgitcrypt.GitcryptDecoder;
import au.net.causal.jgitcrypt.GitcryptKey;
import au.net.causal.jgitcrypt.GitcryptSecurityException;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Encrypt a single file, subclasses define source and target as streams.
 */
public abstract class AbstractEncryptFileMojo extends AbstractKeyBasedMojo
{
    /**
     * Stream of data to encrypt.
     */
    protected abstract InputStream sourceInputStream()
    throws IOException;

    /**
     * Stream to write encrypted data to.
     */
    protected abstract OutputStream targetOutputStream()
    throws IOException;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException
    {
        GitcryptKey key = loadGitcryptKey();
        GitcryptDecoder decoder = new GitcryptDecoder(key);

        try (InputStream is = sourceInputStream();
             OutputStream os = targetOutputStream())
        {
            decoder.encode(is, os);
        }
        catch (IOException | GitcryptSecurityException e)
        {
            throw new MojoExecutionException(e.getMessage(), e);
        }
    }
}
