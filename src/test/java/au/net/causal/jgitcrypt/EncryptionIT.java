package au.net.causal.jgitcrypt;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;

class EncryptionIT
{
    @Test
    void doEncryption()
    throws GitAPIException
    {
        Git git = Git.init()
                .setDirectory(Path.of("mygitrepo").toFile())
                .setInitialBranch("main")
                .call();

        System.out.println("Do encryption so that the result can get picked up by gitcrypt run inside docker");
    }
}
