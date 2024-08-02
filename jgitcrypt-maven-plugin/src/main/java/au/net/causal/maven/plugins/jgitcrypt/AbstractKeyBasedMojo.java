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
import org.codehaus.plexus.util.StringUtils;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.Base64;

/**
 * Abstract mojo that uses a git-crypt key.  The key may be sourced from either a base-64 encoded property value,
 * a base64-encoded value read from a server password in the user's settings, a private key file defined in a server in the user's settings,
 * or a key file specified by property.  Only one of these three settings should be defined.
 */
public abstract class AbstractKeyBasedMojo extends AbstractMojo
{
    /**
     * The base-64 encoded git-crypt key used for encryption and decryption.
     */
    @Parameter(property = "jgitcrypt.key.base64")
    private String keyBase64;

    /**
     * The ID of a server entry in the user settings.  If a password is defined for this server, it is base64 decoded and interpreted as a
     * git-crypt key.  If a private key file is defined for this server, then this is read as the git-crypt key.
     */
    @Parameter(property = "jgitcrypt.key.serverId")
    private String keyServerId;

    /**
     * A git-crypt key file to use for encryption and decryption.
     */
    @Parameter(property = "jgitcrypt.key.file", defaultValue = "${project.build.directory}/gitcrypt.key")
    private File keyFile;

    @Parameter(defaultValue = "${session}", readonly = true, required = true)
    protected MavenSession mavenSession;

    @Component
    private SettingsDecrypter settingsDecrypter;

    /**
     * @return a textual description of the key location, used for log messages.
     */
    protected String getGitcryptKeyLocationDescription()
    {
        if (keyBase64 != null)
            return "from property";
        else if (keyServerId != null)
            return "from Maven settings (server '" + keyServerId + "')";
        else
            return keyFile.getAbsolutePath();
    }

    /**
     * Loads the git-crypt key using one of the key properties.
     *
     * @return the loaded git-crypt key.
     *
     * @throws MojoExecutionException if an error occurs loading the key, or no key is found or defined.
     */
    protected GitcryptKey loadGitcryptKey()
    throws MojoExecutionException
    {
        byte[] keyBytes;

        if (StringUtils.isNotBlank(keyBase64))
        {
            try
            {
                keyBytes = Base64.getDecoder().decode(keyBase64);
            }
            catch (IllegalArgumentException e)
            {
                //Do not use this as cause since it might contain password
                throw new MojoExecutionException("Failed to base64 decode git-crypt key from property.");
            }
        }
        else if (StringUtils.isNotBlank(keyServerId))
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
