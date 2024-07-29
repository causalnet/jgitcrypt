package au.net.causal.jgitcrypt;

/**
 * Thrown when Java security does not have necessary providers set up to perform Gitcrypt operations.
 * This is abnormal as Java comes with these providers as standard.  This may happen if a JDK has been minimized or perhaps running
 * using Graal where JCE providers have not been included.
 */
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
