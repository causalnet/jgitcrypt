package au.net.causal.maven.plugins.jgitcrypt;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.Objects;

/**
 * An output stream that saves its content to the user clipboard as text when closed.
 */
public class CopyDataToClipboardAsTextOutputStream extends OutputStream
{
    private final Charset charset;
    private final ByteArrayOutputStream buffer = new ByteArrayOutputStream();

    /**
     * Creates a clipboard output stream.
     *
     * @param charset the character set used to decode byte data written to this stream when copying to the clipboard as a string.
     */
    public CopyDataToClipboardAsTextOutputStream(Charset charset)
    {
        this.charset = Objects.requireNonNull(charset);
    }

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
        copyToClipboardAsString(buffer.toByteArray());
    }

    /**
     * Saves data written to this stream to the clipboard as a string.
     *
     * @param data the data to save.
     */
    private void copyToClipboardAsString(byte[] data)
    {
        String stringData = new String(data, charset);
        StringSelection stringSelection = new StringSelection(stringData);
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
