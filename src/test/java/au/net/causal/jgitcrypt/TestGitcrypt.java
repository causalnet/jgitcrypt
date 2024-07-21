package au.net.causal.jgitcrypt;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

@Disabled("manual run only")
class TestGitcrypt
{
    @Test
    void testKey() throws IOException
    {
        try (InputStream is = Files.newInputStream(Path.of("testdocker/export/thekey")))
        {
            GitcryptKey key = GitcryptKey.read(is);
            System.out.println(key);
        }
    }

    @Test
    void testEverything() throws Exception
    {
        GitcryptKey key;
        try (InputStream is = Files.newInputStream(Path.of("testdocker/export/thekey")))
        {
            key = GitcryptKey.read(is);
        }

        GitcryptDecoder decoder = new GitcryptDecoder(key);

        try (InputStream is = decoder.decode(Files.newInputStream(Path.of("testdocker/export/repo/secrets.txt"))))
        {
            is.transferTo(System.out);
        }
    }

}
