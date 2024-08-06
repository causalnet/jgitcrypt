package au.net.causal.maven.plugins.jgitcrypt;

import au.net.causal.jgitcrypt.GitcryptKey;
import au.net.causal.jgitcrypt.GitcryptSecurityException;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.codehaus.plexus.util.FileUtils;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;

/**
 * Generates a new randomly generated git-crypt key and saves it to file.
 */
@Mojo(name="generate-key", aggregator = true)
public class GenerateKeyMojo extends AbstractMojo
{
    /**
     * The key file to write.  If it already exists, this file is overwritten.
     */
    @Parameter(property = "jgitcrypt.key.file", defaultValue = "${project.build.directory}/gitcrypt.key", required = true)
    protected File keyFile;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException
    {
        try
        {
            getLog().info("Generating new git-crypt key " + keyFile.toPath().toAbsolutePath());
            FileUtils.forceMkdir(keyFile.getParentFile());
            GitcryptKey key = GitcryptKey.generate();
            try (OutputStream os = Files.newOutputStream(keyFile.toPath()))
            {
                key.write(os);
            }
        }
        catch (GitcryptSecurityException | IOException e)
        {
            throw new MojoExecutionException("Error generating git-crypt key: " + e.getMessage(), e);
        }
    }
}
