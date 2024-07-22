package au.net.causal.jgitcrypt;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.*;

class TestGitcrypt
{
    @Test
    void testKey() throws IOException
    {
        try (InputStream is = TestGitcrypt.class.getResourceAsStream("/testkey/thekey"))
        {
            GitcryptKey key = GitcryptKey.read(is);
            assertThat(key.getAesKey()).isNotEmpty();
            assertThat(key.getHmacKey()).isNotEmpty();
        }
    }

    @Test
    void testDecryption() throws Exception
    {
        GitcryptKey key;
        try (InputStream is = TestGitcrypt.class.getResourceAsStream("/testkey/thekey"))
        {
            key = GitcryptKey.read(is);
        }

        GitcryptDecoder decoder = new GitcryptDecoder(key);
        try (InputStream is = decoder.decode(TestGitcrypt.class.getResourceAsStream("/testrepo/secrets.txt")))
        {
            String content = new String(is.readAllBytes(), StandardCharsets.UTF_8);
            assertThat(content).isEqualToIgnoringNewLines("This is a secret");
        }
    }
}
