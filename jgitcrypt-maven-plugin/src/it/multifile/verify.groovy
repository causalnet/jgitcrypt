File buildDir = new File(basedir, 'target')
File encryptedDir = new File(buildDir, 'encrypted')
File decryptedDir = new File(buildDir, 'decrypted')
File original1 = new File(basedir, 'sample1.txt')
File original2 = new File(basedir, 'sample2.txt')

//Ensure key file was generated
File keyFile = new File(buildDir, 'gitcrypt.key')
assert keyFile.exists()
assert keyFile.size() > 0

//Check encrypted files
File encryptedSample1 = new File(encryptedDir, 'sample1.txt')
File encryptedSample2 = new File(encryptedDir, 'sample2.txt')
List<File> encryptedFiles = Arrays.asList(encryptedDir.listFiles() ?: new File[0])
assert encryptedFiles.size() == 2
assert encryptedFiles.containsAll([encryptedSample1, encryptedSample2])
assert encryptedSample1.exists()
assert encryptedSample1.size() > 0
assert encryptedSample1.getBytes() != original1.getBytes()
assert encryptedSample2.exists()
assert encryptedSample2.size() > 0
assert encryptedSample2.getBytes() != original2.getBytes()

//Check decrypted files
File decryptedSample1 = new File(decryptedDir, 'sample1.txt')
File decryptedSample2 = new File(decryptedDir, 'sample2.txt')
List<File> decryptedFiles = Arrays.asList(decryptedDir.listFiles() ?: new File[0])
assert decryptedFiles.size() == 2
assert decryptedFiles.containsAll([decryptedSample1, decryptedSample2])
assert decryptedSample1.exists()
assert decryptedSample1.size() > 0
assert decryptedSample1.getText() == original1.getText()
assert decryptedSample2.exists()
assert decryptedSample2.size() > 0
assert decryptedSample2.getText() == original2.getText()
