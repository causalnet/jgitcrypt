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

public class CopyDataFromClipboardAsTextInputStream
{
    private final Charset charset;

    public CopyDataFromClipboardAsTextInputStream(Charset charset)
    {
        this.charset = Objects.requireNonNull(charset);
    }

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
