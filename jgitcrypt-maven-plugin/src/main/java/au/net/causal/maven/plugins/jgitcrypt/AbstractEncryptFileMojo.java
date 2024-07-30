package au.net.causal.maven.plugins.jgitcrypt;

import au.net.causal.jgitcrypt.GitcryptDecoder;
import au.net.causal.jgitcrypt.GitcryptKey;
import au.net.causal.jgitcrypt.GitcryptSecurityException;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;

public abstract class AbstractEncryptFileMojo extends AbstractMojo
{
    @Parameter(property = "jgitcrypt.key.file", defaultValue = "${project.build.directory}/gitcrypt.key", required = true)
    protected File keyFile;

    @Parameter(property = "jgitcrypt.source.file", required = true)
    protected File sourceFile;

    protected abstract OutputStream targetOutputStream()
    throws IOException;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException
    {
        if (!keyFile.exists())
            throw new MojoExecutionException("Key file " + keyFile.getAbsolutePath() + " not found.");
        if (!sourceFile.exists())
            throw new MojoExecutionException("File to encrypt " + sourceFile + " not found.");

        //Load the key
        GitcryptKey key;
        try (InputStream is = Files.newInputStream(keyFile.toPath()))
        {
            key = GitcryptKey.read(is);
        }
        catch (IOException e)
        {
            throw new MojoExecutionException(e.getMessage(), e);
        }

        GitcryptDecoder decoder = new GitcryptDecoder(key);

        try (InputStream is = Files.newInputStream(sourceFile.toPath());
             OutputStream os = targetOutputStream())
        {
            decoder.encode(is, os);
        }
        catch (IOException | GitcryptSecurityException e)
        {
            throw new MojoExecutionException(e.getMessage(), e);
        }
    }
}
