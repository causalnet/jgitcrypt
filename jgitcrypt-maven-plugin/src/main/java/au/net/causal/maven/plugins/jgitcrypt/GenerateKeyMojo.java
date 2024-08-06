package au.net.causal.maven.plugins.jgitcrypt;

import au.net.causal.jgitcrypt.GitcryptKey;
import au.net.causal.jgitcrypt.GitcryptSecurityException;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.FileUtils;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;

/**
 * Generates a new randomly generated git-crypt key and saves it to file.
 */
@Mojo(name="generate-key", aggregator = true, requiresProject = false)
public class GenerateKeyMojo extends AbstractMojo
{
    /**
     * The key file to write.  If it already exists, this file is overwritten.
     * If running with a project, the default is ${project.build.directory}/gitcrypt.key.
     */
    @Parameter(property = "jgitcrypt.key.file")
    protected File keyFile;

    @Parameter(defaultValue = "${project}", readonly = true)
    protected MavenProject project;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException
    {
        //Default key file if we have a real project
        if (project != null && project.getFile() != null && project.getFile().exists() && keyFile == null)
            keyFile = new File(new File(project.getBuild().getDirectory()), "gitcrypt.key");

        if (keyFile == null)
            throw new MojoExecutionException("keyFile configuration or jgitcrypt.key.file property must be specified when running generate-key without a project.");

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
