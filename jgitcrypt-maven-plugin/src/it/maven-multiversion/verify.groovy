//Each different Maven version tested generated a different file name, check each one

['3.9.6', '3.8.7', '3.6.3'].each { mavenVersion ->

    File buildDir = new File(basedir, "target-${mavenVersion}")
    File original = new File(basedir, 'sample.txt')

    //Ensure key file was generated
    File keyFile = new File(buildDir, 'gitcrypt.key')
    assert keyFile.exists()
    assert keyFile.size() > 0

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
}

return
