package au.net.causal.maven.plugins.jgitcrypt;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

/**
 * Decrypt a single git-crypt encrypted file to the user's clipboard.  This can be used to avoid saving any decrypted data to
 * disk.  The file being decrypted should be a text file.
 */
@Mojo(name="decrypt-to-clipboard", aggregator = true)
public class DecryptToClipboardMojo extends AbstractDecryptFileMojo
{
    /**
     * The file to decrypt.
     */
    @Parameter(property = "jgitcrypt.source.file", required = true)
    protected File sourceFile;

    /**
     * If specified, wait for this many milliseconds after saving the decrypted data to the clipboard before continuing.  This may be used
     * to work around problems on some platforms where clipboard data is not saved if a process terminates too quickly after copying.
     */
    @Parameter(property = "jgitcrypt.clipboard.waitTimeMillis")
    private Long clipboardWaitTimeMillis;

    /**
     * Encoding to use for converting decrypted data to text.  Defaults to using the project's source encoding, or if that is not defined, uses
     * UTF-8.
     */
    @Parameter(property = "jgitcrypt.textEncoding", defaultValue = "${project.build.sourceEncoding}", required = true)
    private String textEncoding;

    @Override
    protected InputStream sourceInputStream() throws IOException
    {
        return Files.newInputStream(sourceFile.toPath());
    }

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
                getGitcryptKeyLocationDescription());

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
