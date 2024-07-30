package au.net.causal.maven.plugins.jgitcrypt;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.UnsupportedFlavorException;
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
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(stringSelection, stringSelection);

        //On some Linux platforms there is a situation where if the process terminates too early the clipboard is not preserved
        //If we read the clipboard back - this will wait for the selection notification (see see sun.awt.x11.XSelection) which fixes this
        try
        {
            clipboard.getContents(null).getTransferData(DataFlavor.stringFlavor);
        }
        catch (IOException | UnsupportedFlavorException e)
        {
            //Ignore any errors, this is just for waiting
        }
    }
}
