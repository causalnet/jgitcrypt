package au.net.causal.maven.plugins.jgitcrypt;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

@Mojo(name="decrypt-to-clipboard")
public class DecryptToClipboardMojo extends AbstractDecryptFileMojo
{
    @Parameter(property = "jgitcrypt.clipboard.waitTimeMillis")
    private Long clipboardWaitTimeMillis;

    @Parameter(property = "jgitcrypt.textEncoding", defaultValue = "${project.build.sourceEncoding}")
    private String textEncoding;

    @Override
    protected OutputStream targetOutputStream()
    throws IOException
    {
        return new CopyDataToClipboardAsTextOutputStream(Charset.forName(textEncoding));
    }

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException
    {
        if (textEncoding == null)
            textEncoding = StandardCharsets.UTF_8.name();

        getLog().info("Decrypting " + sourceFile.getAbsolutePath() +
                " to clipboard using key " +
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
