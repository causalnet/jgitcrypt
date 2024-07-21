package au.net.causal.jgitcrypt;

import javax.crypto.Mac;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Objects;

/**
 * Input stream that maintains a digital signature of all data that is read from it.
 */
public abstract class MacInputStream extends VerifiableInputStream
{
    private final Mac mac;

    /**
     * Creates a MAC input stream.
     *
     * @param in the underlying input stream.
     * @param mac the MAC to use for reading all data from this stream.
     */
    protected MacInputStream(InputStream in, Mac mac)
    {
        super(in);
        this.mac = Objects.requireNonNull(mac);
    }

    @Override
    public int read() throws IOException
    {
        int ch = super.read();
        if (ch >= 0)
            mac.update((byte)ch);

        return ch;
    }

    @Override
    public int read(byte[] b) throws IOException
    {
        int n = super.read(b);
        if (n >= 0)
            mac.update(b, 0, n);

        return n;
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException
    {
        int n = super.read(b, off, len);
        if (n >= 0)
            mac.update(b, off, len);

        return n;
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
