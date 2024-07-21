package au.net.causal.jgitcrypt;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * An input stream whose content can also be verified against a digital signature.
 * Subclass implementations are responsible for loading a signature from somewhere and doing verification.
 */
public abstract class VerifiableInputStream extends FilterInputStream
{
    /**
     * Creates a verifiable input stream.
     *
     * @param in the underlying data stream.
     */
    protected VerifiableInputStream(InputStream in)
    {
        super(in);
    }

    /**
     * Verifies the entire stream content against its signature.  If the stream has not been fully read, this method will read all
     * remaining underlying stream content.
     *
     * @throws VerificationException if the signature of the data from the underlying stream does not match the expected signature.
     * @throws IOException if an I/O error occurs reading the underlying stream.
     */
    public abstract void verify()
    throws VerificationException, IOException;
}
