package au.net.causal.jgitcrypt;

import java.io.IOException;

/**
 * Thrown when a gitcrypt file (key or encrypted file) does not have a known valid format.
 */
public class GitcryptFileFormatException extends IOException
{
    public GitcryptFileFormatException()
    {
    }

    public GitcryptFileFormatException(String message)
    {
        super(message);
    }

    public GitcryptFileFormatException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public GitcryptFileFormatException(Throwable cause)
    {
        super(cause);
    }
}
