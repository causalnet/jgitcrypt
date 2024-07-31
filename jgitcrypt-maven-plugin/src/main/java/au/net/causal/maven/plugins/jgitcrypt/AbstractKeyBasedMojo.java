package au.net.causal.maven.plugins.jgitcrypt;

import au.net.causal.jgitcrypt.GitcryptKey;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.settings.Server;
import org.apache.maven.settings.crypto.DefaultSettingsDecryptionRequest;
import org.apache.maven.settings.crypto.SettingsDecrypter;
import org.apache.maven.settings.crypto.SettingsDecryptionResult;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.Base64;

public abstract class AbstractKeyBasedMojo extends AbstractMojo
{
    @Parameter(property = "jgitcrypt.key.serverId")
    private String keyServerId;

    @Parameter(property = "jgitcrypt.key.file", defaultValue = "${project.build.directory}/gitcrypt.key")
    private File keyFile;

    @Parameter(defaultValue = "${session}", readonly = true, required = true)
    private MavenSession mavenSession;

    @Component
    private SettingsDecrypter settingsDecrypter;

    protected String getGitcryptKeyLocationDescription()
    {
        if (keyServerId != null)
            return "from Maven settings (server '" + keyServerId + "')";

        return keyFile.getAbsolutePath();
    }

    protected GitcryptKey loadGitcryptKey()
    throws MojoExecutionException
    {
        byte[] keyBytes;
        if (keyServerId != null)
        {
            Server server = mavenSession.getSettings().getServer(keyServerId);
            if (server == null)
                throw new MojoExecutionException("Server entry with id '" + keyServerId + "' not found in settings.  Cannot load git-crypt key.");

            SettingsDecryptionResult settingsResult = settingsDecrypter.decrypt(new DefaultSettingsDecryptionRequest(server));
            server = settingsResult.getServer();

            //Attempt to use password as base64-encoded key
            if (server.getPassword() != null)
            {
                try
                {
                    keyBytes = Base64.getDecoder().decode(server.getPassword());
                }
                catch (IllegalArgumentException e)
                {
                    //Do not use this as cause since it might contain password
                    throw new MojoExecutionException("Failed to base64 decode git-crypt key from password of server '" + keyServerId + "'.");
                }
            }
            else if (server.getPrivateKey() != null)
            {
                //Use this as the key file
                keyBytes = null;
                keyFile = new File(server.getPrivateKey());
            }
            else
                throw new MojoExecutionException("Server entry with id '" + keyServerId + "' does not have a password or key defined in settings.");
        }
        else
            keyBytes = null;

        //Load the key from servers
        if (keyBytes != null)
        {
            try (InputStream is = new ByteArrayInputStream(keyBytes))
            {
                return GitcryptKey.read(is);
            }
            catch (IOException e)
            {
                throw new MojoExecutionException(e.getMessage(), e);
            }
        }
        else //Load from file
        {
            if (!keyFile.exists())
                throw new MojoExecutionException("Key file " + keyFile.getAbsolutePath() + " not found.");

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
}
