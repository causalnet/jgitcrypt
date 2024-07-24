package au.net.causal.jgitcrypt;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.*;

class EncryptionVerify
{
    private String readResource(String resourceName)
    {
        try (InputStream is = EncryptionVerify.class.getResource(resourceName).openStream())
        {
            return new String(is.readAllBytes(), StandardCharsets.UTF_8);
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    @Test
    void verifyAfterDockerRun()
    {
        String resultTxt = readResource("/results/newsecrets.txt");
        assertThat(resultTxt).isEqualToIgnoringNewLines("This file should be encrypted.");
    }
}
