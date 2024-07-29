package au.net.causal.maven.plugins.jgitcrypt;

import au.net.causal.jgitcrypt.GitcryptKey;
import au.net.causal.jgitcrypt.GitcryptSecurityException;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;

@Mojo(name="generate-key-to-clipboard")
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

            //Get base64 string of key
            byte[] keyData;
            try (ByteArrayOutputStream os = new ByteArrayOutputStream())
            {
                key.write(os);
                keyData = os.toByteArray();
            }
            String keyDataEncoded = Base64.getEncoder().encodeToString(keyData);

            //Copy to clipboard
            StringSelection selection = new StringSelection(keyDataEncoded);
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            clipboard.setContents(selection, selection);
        }
        catch (GitcryptSecurityException | IOException e)
        {
            throw new MojoExecutionException("Error generating git-crypt key: " + e.getMessage(), e);
        }
    }
}
