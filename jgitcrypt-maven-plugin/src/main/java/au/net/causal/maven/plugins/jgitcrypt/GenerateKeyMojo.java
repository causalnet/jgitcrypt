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

@Mojo(name="generate-key")
public class GenerateKeyMojo extends AbstractMojo
{
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
