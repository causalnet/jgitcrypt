package au.net.causal.jgitcrypt;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.util.FileUtils;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

class EncryptionIT
{
    @Test
    void doEncryption()
    throws GitAPIException, IOException
    {
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
