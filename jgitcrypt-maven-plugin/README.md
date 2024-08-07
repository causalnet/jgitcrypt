# JGitcrypt Maven Plugin

A Maven Plugin that can encrypt and decrypt [git-crypt](https://github.com/AGWA/git-crypt)
files and generate keys.  Written in pure Java and does not need
the original git-crypt software to be installed - so it can run on platforms
where the original git-crypt won't.

Also allows files to be decrypted using the clipboard to avoid writing
any decrypted files to disk.

## Requirements

- Maven 3.6 or later
- Java 17 or later


## Usage

### Configuring the git-crypt key

Only symmetric git-crypt keys are supported, there is no GPG key support.

For the encryption/decryption goals, a key must be configured in one of these ways:

- The `jgitcrypt.key.file` property (or `keyFile` in plugin configuration) pointing to a file 
  containing the key
- The `jgitcrypt.key.base64` property set to the key itself in base64-encoded form
- The `jgitcrypt.key.serverId` property (or `keyServerId` in plugin configuration) referencing
  a server in `settings.xml`, whose `password` property is expected to contain the  
  key in base64 form
- The `jgitcrypt.key.serverId` property (or `keyServerId` in plugin configuration) referencing
  a server in `settings.xml` whose `password` property contains the key in base64 form
- The `jgitcrypt.key.serverId` property (or `keyServerId` in plugin configuration) referencing
  a server in `settings.xml` whose `privateKey` property is the key file

### Command line

#### Generate a new key to file

```
mvn au.net.causal.jgitcrypt:jgitcrypt-maven-plugin:1.2:generate-key -Djgitcrypt.key.file=key-to-generate.key
```

#### Generate a new key to clipboard in base64

```
mvn au.net.causal.jgitcrypt:jgitcrypt-maven-plugin:1.2:generate-key-to-clipboard
```

Then you can paste this key into a password manager or settings or 
[encrypt it](https://maven.apache.org/guides/mini/guide-encryption.html) using `mvn -ep`.

#### Encrypt a file

Encrypt a file named `secret-stuff.properties` into the `encrypted` directory
using the key `my-key.key`.

```
mvn au.net.causal.jgitcrypt:jgitcrypt-maven-plugin:1.2:encrypt -Dgitcrypt.key.file=/${user.home}/git-crypt-keys/my-key.key -Djgitcrypt.source.file=secret-stuff.properties -Djgitcrypt.target.file=encrypted/secret-stuff.properties
```

#### Decrypt a file

Decrypt an encrypted file named `secrets.properties` into the `decrypted` directory
using the key `my-key.key`.

```
mvn au.net.causal.jgitcrypt:jgitcrypt-maven-plugin:1.2:decrypt -Dgitcrypt.key.file=/${user.home}/git-crypt-keys/my-key.key -Djgitcrypt.source.file=secrets.properties -Djgitcrypt.target.file=decrypted/secrets.properties
```

#### Decrypt a text file to the clipboard

It is possible to decrypt a file without ever writing it to disk and placing its decrypted
form onto the clipboard instead.  Only works properly with text files.

```
mvn au.net.causal.jgitcrypt:jgitcrypt-maven-plugin:1.2:decrypt-to-clipboard -Dgitcrypt.key.file=/${user.home}/git-crypt-keys/my-key.key -Djgitcrypt.source.file=secrets.properties
```

#### Encrypt a text file from the clipboard

Once finished editing, the clipboard contents can be encrypted back to disk.

```
mvn au.net.causal.jgitcrypt:jgitcrypt-maven-plugin:1.2:encrypt-from-clipboard -Dgitcrypt.key.file=/${user.home}/git-crypt-keys/my-key.key -Djgitcrypt.target.file=secrets.properties
```

### In a project

#### Decrypting multiple files as part of the build

In a `<plugin>` section in your POM, you can decrypt multiple files to a target directory
using the `decrypt-files` goal:

```
<plugin>
    <groupId>au.net.causal.jgitcrypt</groupId>
    <artifactId>jgitcrypt-maven-plugin</artifactId>
    <version>1.2</version>
    <executions>     
        <execution>
            <id>decrypt-files</id>
            <goals>
                <goal>decrypt-files</goal>
            </goals>
            <phase>generate-resources</phase>
            <configuration>
                <keyServerId>my-gitcrypt-key</keyServerId>
                <fileSets>
                    <fileSet>
                        <directory>my-secure-data</directory>
                        <targetDirectory>${project.build.directory}/decrypted</targetDirectory>
                        <includes>
                            <include>**/*.properties</include>
                        </includes>
                    </fileSet>
                </fileSets>
            </configuration>
        </execution>
    </executions>
</plugin>
```

#### Encrypting multiple files

Similar to the decryption configuration, except using the `encrypt-files` goal instead.

```
<plugin>
    <groupId>au.net.causal.jgitcrypt</groupId>
    <artifactId>jgitcrypt-maven-plugin</artifactId>
    <version>1.2</version>
    <executions>     
        <execution>
            <id>encrypt-files</id>
            <goals>
                <goal>encrypt-files</goal>
            </goals>
            <phase>generate-resources</phase>
            <configuration>
                <keyServerId>my-gitcrypt-key</keyServerId>
                <fileSets>
                    <fileSet>
                        <directory>config</directory>
                        <targetDirectory>${project.build.directory}/encrypted</targetDirectory>
                        <includes>
                            <include>**/*.properties</include>
                        </includes>
                    </fileSet>
                </fileSets>
            </configuration>
        </execution>
    </executions>
</plugin>
```
