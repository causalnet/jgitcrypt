package au.net.causal.maven.plugins.jgitcrypt;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;

/**
 * Encrypts a file onto the user's clipboard as a base64 encoded string.
 */
@Mojo(name="encrypt-to-clipboard")
public class EncryptToClipboardMojo extends AbstractEncryptFileMojo
{
    /**
     * The file to encrypt.
     */
    @Parameter(property = "jgitcrypt.source.file", required = true)
    protected File sourceFile;

    /**
     * If specified, wait for this many milliseconds after saving the encrypted data to the clipboard before continuing.  This may be used
     * to work around problems on some platforms where clipboard data is not saved if a process terminates too quickly after copying.
     */
    @Parameter(property = "jgitcrypt.clipboard.waitTimeMillis")
    private Long clipboardWaitTimeMillis;

    @Override
    protected InputStream sourceInputStream() throws IOException
    {
        return Files.newInputStream(sourceFile.toPath());
    }

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
                getGitcryptKeyLocationDescription());

        if (!sourceFile.exists())
            throw new MojoExecutionException("File to encrypt " + sourceFile + " not found.");

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
