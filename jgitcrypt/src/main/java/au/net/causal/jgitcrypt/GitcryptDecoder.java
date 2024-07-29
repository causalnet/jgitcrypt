package au.net.causal.jgitcrypt;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.Mac;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOError;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Objects;

/**
 * Encrypts and decrypts files using Gitcrypt encryption.  Only supports Gitcrypt symmetric keys, GPG keys are not supported.
 * <p>
 *
 * Create a decoder using a key, typically saved to a file.  The same key must be used for both encryption and decryption for it to work.
 */
public class GitcryptDecoder
{
    private static final byte[] expectedSignature = "\u0000GITCRYPT\u0000".getBytes(StandardCharsets.US_ASCII);

    private final GitcryptKey key;

    /**
     * Creates a gitcrypt decoder that uses the specified symmetric key for encryption and decryption.
     *
     * @param key the key to use.
     */
    public GitcryptDecoder(GitcryptKey key)
    {
        this.key = Objects.requireNonNull(key);
    }

    /**
     * Decrypt data that is encrypted with Gitcrypt.
     *
     * @param gitcryptData encrypted data stream.
     *
     * @return an input stream that can be used to read decrypted data.  After all data is read, you should also call
     *      {@link VerifiableInputStream#verify()} to check the data that was read matches expected signature - this ensures the correct key was used
     *      and the data was not tampered with.
     *
     * @throws GitcryptFileFormatException if the encrypted file is not a valid gitcrypt file.
     * @throws IOException if an I/O error occurs.
     * @throws GitcryptSecurityException if Java security does not have the necessary providers set up to support Gitcrypt.
     */
    public VerifiableInputStream decode(InputStream gitcryptData)
    throws GitcryptFileFormatException, IOException, GitcryptSecurityException
    {
        DataInputStream data = new DataInputStream(gitcryptData);

        //Read signature
        byte[] signature = new byte[expectedSignature.length];
        data.readFully(signature);
        if (!Arrays.equals(signature, expectedSignature))
            throw new GitcryptFileFormatException("Invalid signature - not a Gitcrypt file");

        //Nonce - 12 bytes
        byte[] nonce = new byte[12];
        data.readFully(nonce);

        //Remainder of file is AES data
        SecretKeySpec aesKey = new SecretKeySpec(key.getAesKey(), "AES");
        byte[] nonceAndCounter = generateNonceAndCounter(nonce);
        IvParameterSpec ivSpec = new IvParameterSpec(nonceAndCounter);

        try
        {
            Cipher cipher = Cipher.getInstance("AES/CTR/NoPadding");
            cipher.init(Cipher.DECRYPT_MODE, aesKey, ivSpec);
            Mac mac = hmac(key.getHmacKey());
            return new GitcryptVerifyingInputStream(new CipherInputStream(data, cipher), mac, nonce);
        }
        catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | InvalidAlgorithmParameterException e)
        {
            throw new GitcryptSecurityException(e);
        }
    }

    /**
     * Encrypts data.
     *
     * @param dataToEncode original data to encrypt.
     * @param outStream the stream to write the Gitcrypt encrypted data to.
     *
     * @throws IOException if an I/O error occurs.
     * @throws GitcryptSecurityException if Java security does not have the necessary providers set up to support Gitcrypt.
     */
    public void encode(InputStream dataToEncode, OutputStream outStream)
    throws IOException, GitcryptSecurityException
    {
        try
        {
            Mac mac = hmac(key.getHmacKey());

            //Read all data into a buffer since it needs to be streamed twice
            ByteArrayOutputStream buf = new ByteArrayOutputStream();
            MacOutputStream macStream = new MacOutputStream(buf, mac);
            dataToEncode.transferTo(macStream);

            //Signature
            outStream.write(expectedSignature);

            //Nonce - will be filled in when crypt stream is closed
            byte[] nonce = mac.doFinal();
            nonce = Arrays.copyOfRange(nonce, 0, 12); //Nonce is first 12 bytes
            outStream.write(nonce);

            //Write encrypted data
            SecretKeySpec aesKey = new SecretKeySpec(key.getAesKey(), "AES");
            byte[] nonceAndCounter = generateNonceAndCounter(nonce);
            IvParameterSpec ivSpec = new IvParameterSpec(nonceAndCounter);
            Cipher cipher = Cipher.getInstance("AES/CTR/NoPadding");
            cipher.init(Cipher.ENCRYPT_MODE, aesKey, ivSpec);

            CipherOutputStream cipherStream = new CipherOutputStream(outStream, cipher);
            cipherStream.write(buf.toByteArray());
        }
        catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | InvalidAlgorithmParameterException e)
        {
            throw new GitcryptSecurityException(e);
        }
    }

    /**
     * Generate nonce and counter bytes for initializing the AES cipher.  This has the nonce bytes followed by a big-endian 32-bit integer,
     * initialized to zero.
     *
     * @param nonce nonce value.
     *
     * @return nonce and counter.
     */
    private static byte[] generateNonceAndCounter(byte[] nonce)
    {
        ByteArrayOutputStream nonceAndCounter = new ByteArrayOutputStream(nonce.length + Integer.BYTES);
        try (DataOutputStream data = new DataOutputStream(nonceAndCounter))
        {
            data.write(nonce);
            data.writeInt(0);
            return nonceAndCounter.toByteArray();
        }
        catch (IOException e)
        {
            throw new IOError(e);
        }
    }

    /**
     * Creates a MAC used for reading or generating the digest in a Gitcrypt file.
     *
     * @param key the HMAC key from the key file.
     *
     * @return an initialized mac.
     *
     * @throws GitcryptFileFormatException if the HMAC key is invalid.
     * @throws NoSuchAlgorithmException if JCE does not have a provider for Gitcrypt's HMAC algorithm.
     */
    private static Mac hmac(byte[] key)
    throws GitcryptFileFormatException, NoSuchAlgorithmException
    {
        String algorithm = "HmacSHA1";
        try
        {
            Mac mac = Mac.getInstance(algorithm);
            mac.init(new SecretKeySpec(key, algorithm));
            return mac;
        }
        catch (InvalidKeyException e)
        {
            throw new GitcryptFileFormatException("Invalid key", e);
        }
    }
}
