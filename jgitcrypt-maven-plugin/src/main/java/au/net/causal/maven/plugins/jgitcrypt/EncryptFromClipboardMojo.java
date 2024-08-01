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
import java.nio.file.Files;

@Mojo(name="encrypt-from-clipboard")
public class EncryptFromClipboardMojo extends AbstractEncryptFileMojo
{
    @Parameter(property = "jgitcrypt.target.file", required = true)
    protected File targetFile;

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
        getLog().info("Encrypting from clipboard to " +
                targetFile.getAbsolutePath() + " using key " +
                getGitcryptKeyLocationDescription());

        super.execute();
    }
}
