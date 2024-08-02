package au.net.causal.maven.plugins.jgitcrypt;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.codehaus.plexus.util.FileUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

/**
 * Encrypts text content from the user clipboard to a file.  This can be used to avoid saving any decrypted data to
 * disk.  Only works with clipboard content in text form, will fail if there is no clipboard data or clipboard data is
 * not text.
 */
@Mojo(name="encrypt-from-clipboard")
public class EncryptFromClipboardMojo extends AbstractEncryptFileMojo
{
    /**
     * The file to save encrypted data to.  Overwritten if it already exists.
     */
    @Parameter(property = "jgitcrypt.target.file", required = true)
    protected File targetFile;

    /**
     * Encoding to use for converting decrypted data to text.  Defaults to using the project's source encoding, or if that is not defined, uses
     * UTF-8.
     */
    @Parameter(property = "jgitcrypt.textEncoding", defaultValue = "${project.build.sourceEncoding}", required = true)
    private String textEncoding;

    @Override
    protected InputStream sourceInputStream() throws IOException
    {
        InputStream is = new CopyDataFromClipboardAsTextInputStream(Charset.forName(textEncoding)).copyFromClipboard();
        if (is == null)
            throw new IOException("No text present on clipboard");

        return is;
    }

    @Override
    protected OutputStream targetOutputStream()
    throws IOException
    {
        FileUtils.forceMkdir(targetFile.getParentFile());
        return Files.newOutputStream(targetFile.toPath());
    }

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException
    {
        if (textEncoding == null)
            textEncoding = StandardCharsets.UTF_8.name();

        getLog().info("Encrypting from clipboard to " +
                targetFile.getAbsolutePath() + " using key " +
                getGitcryptKeyLocationDescription());

        super.execute();
    }
}
