package au.net.causal.jgitcrypt;

public class GitcryptSecurityException extends Exception
{
    public GitcryptSecurityException()
    {
    }

    public GitcryptSecurityException(String message)
    {
        super(message);
    }

    public GitcryptSecurityException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public GitcryptSecurityException(Throwable cause)
    {
        super(cause);
    }
}
