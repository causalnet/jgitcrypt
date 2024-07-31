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

@Mojo(name="encrypt")
public class EncryptMojo extends AbstractEncryptFileMojo
{
    @Parameter(property = "jgitcrypt.source.file", required = true)
    protected File sourceFile;

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
