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
 * Decrypt a single git-crypt encrypted file.  Original encrypted file is not modified, rather the decrypted data is written
 * to a separate target file.
 */
@Mojo(name="decrypt")
public class DecryptMojo extends AbstractDecryptFileMojo
{
    /**
     * The file to decrypt.
     */
    @Parameter(property = "jgitcrypt.source.file", required = true)
    protected File sourceFile;

    /**
     * Decrypted data is written to this file.  Will be overwritten if it already exists.
     */
    @Parameter(property = "jgitcrypt.target.file", required = true)
    protected File targetFile;

    @Override
    protected InputStream sourceInputStream() throws IOException
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
        getLog().info("Decrypting " + sourceFile.getAbsolutePath() +
                " to " + targetFile.getAbsolutePath() +
                " using key " + getGitcryptKeyLocationDescription());

        if (!sourceFile.exists())
            throw new MojoExecutionException("File to decrypt " + sourceFile + " not found.");

        super.execute();
    }
}
