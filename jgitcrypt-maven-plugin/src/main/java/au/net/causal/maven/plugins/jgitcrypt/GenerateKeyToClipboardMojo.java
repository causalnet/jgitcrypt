package au.net.causal.maven.plugins.jgitcrypt;

import au.net.causal.jgitcrypt.GitcryptKey;
import au.net.causal.jgitcrypt.GitcryptSecurityException;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Generates a new randomly generated git-crypt key and saves it to the clipboard as a base64 encoded string.
 * This can be used to avoid saving sensitive keys to the file system.  The data from the clipboard can then be
 * pasted/saved to a password manager or passed to an encryptor (such as mvn -ep) and the result saved in Maven settings.
 */
@Mojo(name="generate-key-to-clipboard", aggregator = true, requiresProject = false)
public class GenerateKeyToClipboardMojo extends AbstractMojo
{
    @Override
    public void execute() throws MojoExecutionException, MojoFailureException
    {
        try
        {
            getLog().info("Generating new git-crypt key to clipboard (base64 encoded)");

            //Generate random key
            GitcryptKey key = GitcryptKey.generate();

            try (OutputStream os = new CopyDataToClipboardAsBase64OutputStream())
            {
                key.write(os);
            }
        }
        catch (GitcryptSecurityException | IOException e)
        {
            throw new MojoExecutionException("Error generating git-crypt key: " + e.getMessage(), e);
        }
    }
}
