package au.net.causal.jgitcrypt;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;

class TestGitcrypt
{
    @Test
    void testKey() throws IOException
    {
        try (InputStream is = TestGitcrypt.class.getResourceAsStream("/testkey/thekey"))
        {
            GitcryptKey key = GitcryptKey.read(is);
            System.out.println(key);
        }
    }

    @Test
    void testEverything() throws Exception
    {
        GitcryptKey key;
        try (InputStream is = TestGitcrypt.class.getResourceAsStream("/testkey/thekey"))
        {
            key = GitcryptKey.read(is);
        }

        GitcryptDecoder decoder = new GitcryptDecoder(key);

        try (InputStream is = decoder.decode(TestGitcrypt.class.getResourceAsStream("/testrepo/secrets.txt")))
        {
            is.transferTo(System.out);
        }
    }
}
