File keyDir = new File(basedir, 'keys')
File workspaceDir = new File(basedir, 'workspace')
File original = new File(basedir, 'sample.txt')

//Ensure key file was generated
File keyFile = new File(keyDir, 'mygitcrypt.key')
assert keyFile.exists()
assert keyFile.size() > 0

//Check encrypted file
File encryptedSample = new File(workspaceDir, 'sample.txt.encrypted')
assert encryptedSample.exists()
assert encryptedSample.size() > 0
assert encryptedSample.getBytes() != original.getBytes()

//Check decrypted file
File decryptedSample = new File(workspaceDir, 'sample.decrypted.txt')
assert decryptedSample.exists()
assert decryptedSample.size() > 0
assert decryptedSample.getText() == original.getText()
