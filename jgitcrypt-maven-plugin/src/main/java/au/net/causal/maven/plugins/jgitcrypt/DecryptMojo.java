package au.net.causal.maven.plugins.jgitcrypt;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;

@Mojo(name="decrypt")
public class DecryptMojo extends AbstractEncryptFileMojo
{
    @Parameter(property = "jgitcrypt.target.file", required = true)
    protected File targetFile;

    @Override
    protected OutputStream targetOutputStream()
    throws IOException
    {
        return Files.newOutputStream(targetFile.toPath());
    }

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException
    {
        getLog().info("Decrypting " + sourceFile.getAbsolutePath() +
                " to " + targetFile.getAbsolutePath() +
                " using key " + keyFile.getAbsolutePath());

        super.execute();
    }
}
