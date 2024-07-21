package au.net.causal.jgitcrypt;

import java.io.IOException;

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
