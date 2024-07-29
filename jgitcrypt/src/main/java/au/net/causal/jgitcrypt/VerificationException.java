package au.net.causal.jgitcrypt;

import java.io.IOException;

/**
 * Thrown when verifying file content against a digital signature fails.  This can happen if the file was
 * tampered with.
 */
public class VerificationException extends IOException
{
    public VerificationException()
    {
    }

    public VerificationException(String message)
    {
        super(message);
    }

    public VerificationException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public VerificationException(Throwable cause)
    {
        super(cause);
    }
}
