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

/**
 * An output stream that saves its content to the user clipboard as a base64-encoded string when closed.
 */
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

    /**
     * Closes the stream and saves any content written to it to the clipboard.
     *
     * @throws IOException if an I/O error occurs.
     */
    @Override
    public void close() throws IOException
    {
        buffer.close();
        copyToClipboardBase64(buffer.toByteArray());
    }

    /**
     * Base-64 encodes data and saves it to the clipboard as a string.
     *
     * @param data the data to save.
     */
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
