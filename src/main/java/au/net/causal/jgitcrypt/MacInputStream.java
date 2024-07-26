package au.net.causal.jgitcrypt;

import javax.crypto.Mac;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

/**
 * Input stream that maintains a digital signature of all data that is read from it.
 */
public class MacInputStream extends FilterInputStream
{
    private final Mac mac;

    /**
     * Creates a MAC input stream.
     *
     * @param in the underlying input stream.
     * @param mac the MAC to use for reading all data from this stream.
     */
    public MacInputStream(InputStream in, Mac mac)
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
            mac.update(b, off, n);

        return n;
    }

    /**
     * @return the MAC digital signature for all data already read from this stream.
     */
    public Mac getMac()
    {
        return mac;
    }
}
