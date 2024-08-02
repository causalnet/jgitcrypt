package au.net.causal.maven.plugins.jgitcrypt;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Objects;

/**
 * A source that produces an input stream from string data on the user's clipboard.
 */
public class CopyDataFromClipboardAsTextInputStream
{
    private final Charset charset;

    /**
     * Creates a clipboard source.
     *
     * @param charset the character set to use for converting textual clipboard data into bytes.
     */
    public CopyDataFromClipboardAsTextInputStream(Charset charset)
    {
        this.charset = Objects.requireNonNull(charset);
    }

    /**
     * Read text data from the user clipboard as a byte-array.
     *
     * @return an input stream that reads the clipboard, or null if the clipboard is empty or does not contain text data.
     *
     * @throws IOException if an error occurs reading clipboard data.
     */
    public InputStream copyFromClipboard()
    throws IOException
    {
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();

        Transferable contents = clipboard.getContents(null);
        if (contents != null && contents.isDataFlavorSupported(DataFlavor.stringFlavor))
        {
            try
            {
                String data = (String)contents.getTransferData(DataFlavor.stringFlavor);
                return new ByteArrayInputStream(data.getBytes(charset));
            }
            catch (UnsupportedFlavorException e)
            {
                //Fallthrough
            }
        }

        return null;
    }
}
