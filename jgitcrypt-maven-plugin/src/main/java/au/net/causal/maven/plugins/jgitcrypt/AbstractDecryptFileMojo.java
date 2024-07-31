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

public abstract class AbstractDecryptFileMojo extends AbstractKeyBasedMojo
{
    protected abstract InputStream sourceInputStream()
    throws IOException;

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
