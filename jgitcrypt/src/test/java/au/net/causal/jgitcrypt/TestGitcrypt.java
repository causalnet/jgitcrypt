package au.net.causal.jgitcrypt;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HexFormat;

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
        try (VerifiableInputStream is = decoder.decode(TestGitcrypt.class.getResourceAsStream("/testrepo/secrets.txt")))
        {
            String content = new String(is.readAllBytes(), StandardCharsets.UTF_8);
            assertThat(content).isEqualToIgnoringNewLines("This is a secret");
            is.verify();
        }
    }

    @Test
    void testEncryptAndCompare() throws Exception
    {
        GitcryptKey key;
        try (InputStream is = TestGitcrypt.class.getResourceAsStream("/testkey/thekey"))
        {
            key = GitcryptKey.read(is);
        }

        byte[] encryptedFromGitcrypt;
        try (InputStream is = TestGitcrypt.class.getResourceAsStream("/testrepo/secrets.txt"))
        {
            encryptedFromGitcrypt = is.readAllBytes();
        }

        GitcryptDecoder decoder = new GitcryptDecoder(key);
        byte[] decodedData;
        try (VerifiableInputStream is = decoder.decode(new ByteArrayInputStream(encryptedFromGitcrypt)))
        {
            decodedData = is.readAllBytes();
            String content = new String(decodedData, StandardCharsets.UTF_8);
            assertThat(content).isEqualToIgnoringNewLines("This is a secret");
            is.verify();
        }

        ByteArrayOutputStream encryptedByMeStream = new ByteArrayOutputStream();
        try (InputStream is = new ByteArrayInputStream(decodedData))
        {
            decoder.encode(is, encryptedByMeStream);
        }
        byte[] encryptedByMe = encryptedByMeStream.toByteArray();

        //Compare (with hexdump to ease debugging)
        HexFormat hex = HexFormat.ofDelimiter(" ").withUpperCase();
        assertThat(hex.formatHex(encryptedByMe)).isEqualTo(hex.formatHex(encryptedFromGitcrypt));
        assertThat(encryptedByMe).contains(encryptedFromGitcrypt);
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
        try (VerifiableInputStream is = decoder.decode(new ByteArrayInputStream(encodeBuf.toByteArray())))
        {
            String content = new String(is.readAllBytes(), StandardCharsets.UTF_8);
            assertThat(content).isEqualTo(stringToEncode);
            is.verify();
        }
    }
}
