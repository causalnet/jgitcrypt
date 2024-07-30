package au.net.causal.maven.plugins.jgitcrypt;

import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Base64;

public class CopyDataToClipboardAsBase64OutputStream extends OutputStream
{
    private final ByteArrayOutputStream buffer = new ByteArrayOutputStream();

    @Override
    public void write(byte[] b) throws IOException
    {
        buffer.write(b);
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException
    {
        buffer.write(b, off, len);
    }

    @Override
    public void write(int b) throws IOException
    {
        buffer.write(b);
    }

    @Override
    public void flush() throws IOException
    {
        buffer.flush();
    }

    @Override
    public void close() throws IOException
    {
        buffer.close();
        copyToClipboardBase64(buffer.toByteArray());
    }

    private void copyToClipboardBase64(byte[] data)
    {
        String base64Data = Base64.getEncoder().encodeToString(data);
        StringSelection stringSelection = new StringSelection(base64Data);
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(stringSelection, stringSelection);
    }
}
