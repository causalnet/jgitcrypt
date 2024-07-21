package au.net.causal.jgitcrypt;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public class GitcryptKey
{
    private static final int KEY_FIELD_VERSION = 1;
    private static final int KEY_FIELD_AES_KEY = 3;
    private static final int KEY_FIELD_HMAC_KEY = 5;

    private static final int FORMAT_VERSION = 2;

    private static final byte[] expectedSignature = "\u0000GITCRYPTKEY".getBytes(StandardCharsets.US_ASCII);

    private final Map<Integer, byte[]> headerFields;
    private final Map<Integer, byte[]> entries;

    public GitcryptKey(Map<Integer, byte[]> headerFields, Map<Integer, byte[]> entries)
    {
        this.headerFields = new LinkedHashMap<>(headerFields);
        this.entries = new LinkedHashMap<>(entries);
    }

    public int getVersion()
    {
        byte[] data = entries.get(KEY_FIELD_VERSION);
        if (data == null)
            return 0;
        else
        {
            try (DataInputStream is = new DataInputStream(new ByteArrayInputStream(data)))
            {
                return is.readInt();
            }
            catch (IOException e)
            {
                throw new RuntimeException(e);
            }
        }
    }

    public byte[] getAesKey()
    {
        return entries.get(KEY_FIELD_AES_KEY);
    }

    public byte[] getHmacKey()
    {
        return entries.get(KEY_FIELD_HMAC_KEY);
    }

    public Map<Integer, byte[]> getHeaderFields()
    {
        return Collections.unmodifiableMap(headerFields);
    }

    public Map<Integer, byte[]> getEntries()
    {
        return Collections.unmodifiableMap(entries);
    }

    public static GitcryptKey read(InputStream is)
    throws GitcryptFileFormatException, IOException
    {
        DataInputStream data = new DataInputStream(is);

        //Signature
        byte[] signature = new byte[expectedSignature.length];
        data.readFully(signature);
        if (!Arrays.equals(expectedSignature, signature))
            throw new GitcryptFileFormatException("Invalid signature - not a Gitcrypt key file");

        //Version
        int version = data.readInt();
        if (version != FORMAT_VERSION)
            throw new GitcryptFileFormatException("Invalid Gitcrypt key file version: " + version);

        Map<Integer, byte[]> headerFields = GitcryptIO.readFields(data);
        Map<Integer, byte[]> entries = GitcryptIO.readFields(data);

        return new GitcryptKey(headerFields, entries);
    }
}
