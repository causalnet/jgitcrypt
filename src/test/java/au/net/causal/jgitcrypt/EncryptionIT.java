package au.net.causal.jgitcrypt;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.util.FileUtils;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

class EncryptionIT
{
    @Test
    void doEncryption()
    throws GitAPIException, IOException
    {
        //Generate a gitcrypt key
        //TODO for now just reuse the one generated for the input
        GitcryptKey gitcryptKey;
        try (InputStream is = TestGitcrypt.class.getResourceAsStream("/testkey/thekey"))
        {
            gitcryptKey = GitcryptKey.read(is);
        }


        //Save gitcrypt key
        Path keyFile = Path.of("target", "verifier-docker-data", "thekey");
        Files.createDirectories(keyFile.getParent());
        try (OutputStream os = Files.newOutputStream(keyFile))
        {
            gitcryptKey.write(os);
        }

        //Generate the git repo with encrypted file in it
        Path repoDir = Path.of("target", "verifier-docker-data", "it-gitrepo");
        FileUtils.delete(repoDir.toFile(), FileUtils.RECURSIVE | FileUtils.RETRY | FileUtils.SKIP_MISSING);

        try (Git git = Git.init()
                .setDirectory(repoDir.toFile())
                .setInitialBranch("main")
                .call())
        {
            //Encrypted file
            Path encryptedFile = repoDir.resolve("newsecrets.txt");
            //TODO actually encrypt it
            Files.writeString(encryptedFile, "This file should be encrypted.");
            git.add().addFilepattern(encryptedFile.getFileName().toString()).call();

            //.gitattributes file
            Path gitAttributesFile = repoDir.resolve(".gitattributes");
            Files.writeString(gitAttributesFile, encryptedFile.getFileName().toString() + " filter=git-crypt diff=git-crypt");
            git.add().addFilepattern(gitAttributesFile.getFileName().toString()).call();

            git.commit()
                    .setMessage("A commit.")
                    .setCommitter("Someone", "someone@example.com")
                    .call();
        }

        System.out.println("Do encryption so that the result can get picked up by gitcrypt run inside docker");
    }
}