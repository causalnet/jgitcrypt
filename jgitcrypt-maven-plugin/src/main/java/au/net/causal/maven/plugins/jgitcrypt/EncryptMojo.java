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
import java.nio.file.Files;

/**
 * Encrypts a single file.  The original source file is not modified, rather the encrypted data is written to
 * a separate file.
 */
@Mojo(name="encrypt", aggregator = true)
public class EncryptMojo extends AbstractEncryptFileMojo
{
    /**
     * The file to encrypt.
     */
    @Parameter(property = "jgitcrypt.source.file", required = true)
    protected File sourceFile;

    /**
     * Encrypted data is written to this file.  Overwritten if it already exists.
     */
    @Parameter(property = "jgitcrypt.target.file", required = true)
    protected File targetFile;

    @Override
    protected InputStream sourceInputStream()
    throws IOException
    {
        return Files.newInputStream(sourceFile.toPath());
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
        getLog().info("Encrypting " + sourceFile.getAbsolutePath() +
                " to " + targetFile.getAbsolutePath() +
                " using key " + getGitcryptKeyLocationDescription());

        if (!sourceFile.exists())
            throw new MojoExecutionException("File to encrypt " + sourceFile + " not found.");

        super.execute();
    }
}
