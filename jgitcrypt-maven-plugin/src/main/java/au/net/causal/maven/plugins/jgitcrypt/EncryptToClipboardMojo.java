package au.net.causal.maven.plugins.jgitcrypt;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.IOException;
import java.io.OutputStream;

@Mojo(name="encrypt-to-clipboard")
public class EncryptToClipboardMojo extends AbstractEncryptFileMojo
{
    @Parameter(property = "jgitcrypt.clipboard.waitTimeMillis")
    private Long clipboardWaitTimeMillis;

    @Override
    protected OutputStream targetOutputStream()
    throws IOException
    {
        return new CopyDataToClipboardAsBase64OutputStream();
    }

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException
    {
        getLog().info("Encrypting " + sourceFile.getAbsolutePath() +
                " to clipboard (in base64) using key " +
                keyFile.getAbsolutePath());

        super.execute();

        if (clipboardWaitTimeMillis != null)
        {
            try
            {
                Thread.sleep(clipboardWaitTimeMillis);
            }
            catch (InterruptedException ex)
            {
                //Ignore
            }
        }
    }
}
