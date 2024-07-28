package au.net.causal.jgitcrypt;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.*;

class TestJGitcryptApp
{
    /**
     * Encrypt, then decrypt a file and verify we can read it back.
     */
    @Test
    void encryptAndDecrypt(@TempDir Path tempDir)
    throws IOException, GitcryptSecurityException
    {
        //Generate a key
        Path keyFile = Files.createTempFile(tempDir, "jgitcrypt-test-", ".key").toAbsolutePath();
        JGitcryptApp.main("generatekey", keyFile.toString());
        assertThat(keyFile).isNotEmptyFile();

        //Original decrypted file
        Path originalFile = Files.createTempFile(tempDir, "jgitcrypt-test-file", ".txt").toAbsolutePath();
        Files.writeString(originalFile, "This is a test file");
        assertThat(originalFile).content().isEqualTo("This is a test file");

        //Encrypt the file
        Path encryptedFile = Files.createTempFile(tempDir, "jgitcrypt-test-file", ".encrypted.txt").toAbsolutePath();
        assertThat(encryptedFile).isEmptyFile();
        JGitcryptApp.main("encrypt", keyFile.toString(), originalFile.toString(), encryptedFile.toString());
        assertThat(encryptedFile)
                .isNotEmptyFile()
                .binaryContent().isNotEqualTo(Files.readAllBytes(originalFile));

        //Decrypt the file again
        Path decryptedFile = Files.createTempFile(tempDir, "jgitcrypt-test-file", ".decrypted.txt").toAbsolutePath();
        assertThat(decryptedFile).isEmptyFile();
        JGitcryptApp.main("decrypt", keyFile.toString(), encryptedFile.toString(), decryptedFile.toString());
        assertThat(decryptedFile)
                .isNotEmptyFile()
                .hasSameTextualContentAs(originalFile);
    }
}
