# JGitcrypt

A Java implementation of git-crypt which can be used on files encrypted by 
the original git-crypt software.  It can be used as a command line app or as 
a Java library in other software, and can encrypt/decrypt files and generate
new git-crypt keys.

[git-crypt](https://github.com/AGWA/git-crypt) is software that is used to encrypt
and decrypt files in a git repository.  This Java implementation 
can be used where the original git-crypt software does not work, such as:
- on unsupported operating systems
- on platforms/systems where git is locked down and custom filters cannot be used.

## Usage

### Library

The `GitcryptDecoder` class is the entrypoint to the JGitcrypt library.
Use its `encode()` and `decode()` methods to encrypt or decrypt files.
Use `GitcryptKey.load()` to load a git-crypt key file which is used when
encrypting and decrypting files.

## Command Line Tool

Use `java -jar jgitcrypt.jar` along with command line arguments:

### decrypt

`decrypt <keyfile> <inputfile> <outputfile>`

will decrypt a single file that was previously encrypted with git-crypt and
the specified keyfile.  If <outputfile> is not specified, the decrypted file
will be sent to standard output.  If <inputfile> is not specified, it will be
read from standard input.

### encrypt

`encrypt <keyfile> <inputfile> <outputfile>`

will encrypt an input file using a git-crypt key.  If <outputfile> is not specified, 
standard output is used.  If <inputfile> is not specified, standard input will be used
as the source.

### generatekey

`generatekey <keyfile>`

creates a new random git-crypt key and saves it to a file.

## Compatibility with git-crypt

Only symmetric keys are supported with JGitcrypt - GPG keys are not supported.
The command line is also not compatible with the original tool and can't itself
be used as a git filter.

## Building

Java 17, Maven and Docker are required for building JGitcrypt.  Docker is used
as in automated tests to ensure compatibility with the original git-crypt 
software.
