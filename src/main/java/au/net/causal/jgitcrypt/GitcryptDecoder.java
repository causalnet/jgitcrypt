package au.net.causal.jgitcrypt;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
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
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Objects;

public class GitcryptDecoder
{
    private static final byte[] expectedSignature = "\u0000GITCRYPT\u0000".getBytes(StandardCharsets.US_ASCII);

    private final GitcryptKey key;

    public GitcryptDecoder(GitcryptKey key)
    {
        this.key = Objects.requireNonNull(key);
    }

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
