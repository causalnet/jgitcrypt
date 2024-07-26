package au.net.causal.jgitcrypt;

import javax.crypto.Mac;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Input stream that maintains a digital signature of all data that is read from it and verifies a known value against it.
 */
public abstract class MacVerifiableInputStream extends VerifiableInputStream
{
    private final Mac mac;

    /**
     * Creates a MAC input stream.
     *
     * @param in the underlying input stream.
     * @param mac the MAC to use for reading all data from this stream.
     */
    protected MacVerifiableInputStream(InputStream in, Mac mac)
    {
        super(new MacInputStream(in, mac));
        this.mac = mac;
    }

    /**
     * @return the MAC digital signature for all data already read from this stream.
     */
    public Mac getMac()
    {
        return mac;
    }

    @Override
    public void verify() throws VerificationException, IOException
    {
        //Read to end of stream
        transferTo(OutputStream.nullOutputStream());

        //Verify signature
        byte[] digest = mac.doFinal();
        checkSignature(digest);
    }

    /**
     * Checks the digital signature read from the stream against an expected value.
     *
     * @param streamDigest the digest of the data read from this stream to check.
     *
     * @throws VerificationException if the stream digest does not match an expected value.
     */
    protected abstract void checkSignature(byte[] streamDigest)
    throws VerificationException;
}
