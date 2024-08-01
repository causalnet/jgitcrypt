File buildDir = new File(basedir, 'target')
File original = new File(basedir, 'sample.txt')

//Check encrypted file
File encryptedSample = new File(buildDir, 'sample.txt.encrypted')
assert encryptedSample.exists()
assert encryptedSample.size() > 0
assert encryptedSample.getBytes() != original.getBytes()

//Check decrypted file
File decryptedSample = new File(buildDir, 'sample.decrypted.txt')
assert decryptedSample.exists()
assert decryptedSample.size() > 0
assert decryptedSample.getText() == original.getText()
