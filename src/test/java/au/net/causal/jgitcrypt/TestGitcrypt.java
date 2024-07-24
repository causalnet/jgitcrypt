package au.net.causal.jgitcrypt;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
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

    @Test
    void testEncryptAndThenDecrypt() throws Exception
    {
        //Load a key from our test data
        GitcryptKey key;
        try (InputStream is = TestGitcrypt.class.getResourceAsStream("/testkey/thekey"))
        {
            key = GitcryptKey.read(is);
        }

        String stringToEncode = "This file is full of galahs!";
        byte[] dataToEncode = stringToEncode.getBytes(StandardCharsets.UTF_8);

        //Encrypt some data
        GitcryptDecoder decoder = new GitcryptDecoder(key);
        ByteArrayOutputStream encodeBuf = new ByteArrayOutputStream();
        decoder.encode(new ByteArrayInputStream(dataToEncode), encodeBuf);

        //Try to decrypt it again and make sure it matches original
        try (InputStream is = decoder.decode(new ByteArrayInputStream(encodeBuf.toByteArray())))
        {
            String content = new String(is.readAllBytes(), StandardCharsets.UTF_8);
            assertThat(content).isEqualTo(stringToEncode);
        }
    }
}
