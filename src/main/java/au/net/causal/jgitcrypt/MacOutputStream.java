package au.net.causal.jgitcrypt;

import javax.crypto.Mac;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Objects;

/**
 * Output stream that maintains a digital signature of all data that is written to it.
 */
public class MacOutputStream extends FilterOutputStream
{
    private final Mac mac;

    /**
     * Creates a MAC output stream.
     *
     * @param out the underlying output stream.
     * @param mac the MAC to update with all data written to this stream.
     */
    protected MacOutputStream(OutputStream out, Mac mac)
    {
        super(out);
        this.mac = Objects.requireNonNull(mac);
    }

    @Override
    public void write(int b) throws IOException
    {
        super.write(b);
        mac.update((byte)b);
    }

    @Override
    public void write(byte[] b) throws IOException
    {
        super.write(b);
        mac.update(b, 0, b.length);
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException
    {
        super.write(b, off, len);
        mac.update(b, off, len);
    }

    /**
     * @return the MAC digital signature for all data written to the stream.
     */
    public Mac getMac()
    {
        return mac;
    }
}
