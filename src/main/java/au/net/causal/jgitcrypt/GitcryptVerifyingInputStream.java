package au.net.causal.jgitcrypt;

import javax.crypto.Mac;
import java.io.InputStream;
import java.util.Arrays;

/**
 * Gitcrypt input stream verifies data from gitcrypt decoded file data and provides ability to verify its contents against a signature.
 * Does not actually perform decryption of data, this is expected to have already been done in the underlying stream.
 */
public class GitcryptVerifyingInputStream extends MacInputStream
{
    private final byte[] nonce;

    /**
     * Creates a gitcrypt verifying input stream.
     *
     * @param in underlying input stream, should already be decrypted.
     * @param mac the MAC to use for verification.
     * @param nonce Gitcrypt nonce value with the expected signature.
     */
    public GitcryptVerifyingInputStream(InputStream in, Mac mac, byte[] nonce)
    {
        super(in, mac);
        this.nonce = Arrays.copyOf(nonce, nonce.length);
    }

    @Override
    protected void checkSignature(byte[] streamDigest) throws VerificationException
    {
        if (!Arrays.equals(streamDigest, 0, nonce.length, nonce, 0, nonce.length))
            throw new VerificationException("Encrypted file failed to verify against signature.");
    }
}
