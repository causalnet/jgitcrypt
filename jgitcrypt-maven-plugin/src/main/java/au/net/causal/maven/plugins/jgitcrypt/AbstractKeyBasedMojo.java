package au.net.causal.maven.plugins.jgitcrypt;

import au.net.causal.jgitcrypt.GitcryptKey;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

public abstract class AbstractKeyBasedMojo extends AbstractMojo
{
    @Parameter(property = "jgitcrypt.key.file", defaultValue = "${project.build.directory}/gitcrypt.key", required = true)
    protected File keyFile;

    protected GitcryptKey loadGitcryptKey()
    throws MojoExecutionException
    {
        if (!keyFile.exists())
            throw new MojoExecutionException("Key file " + keyFile.getAbsolutePath() + " not found.");

        //Load the key
        try (InputStream is = Files.newInputStream(keyFile.toPath()))
        {
            return GitcryptKey.read(is);
        }
        catch (IOException e)
        {
            throw new MojoExecutionException(e.getMessage(), e);
        }
    }
}
