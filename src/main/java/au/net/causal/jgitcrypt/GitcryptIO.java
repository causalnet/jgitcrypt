package au.net.causal.jgitcrypt;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

class GitcryptIO
{
    private static final int FIELD_ID_END = 0;

    static Map<Integer, byte[]> readFields(DataInputStream data)
    throws IOException
    {
        Map<Integer, byte[]> dataMap = new LinkedHashMap<>();
        int fieldId;
        do
        {
            fieldId = data.readInt();
            if (fieldId != FIELD_ID_END)
            {
                int fieldLen = data.readInt();
                byte[] fieldData = new byte[fieldLen];
                data.readFully(fieldData);
                dataMap.put(fieldId, fieldData);
            }
        }
        while (fieldId != FIELD_ID_END);

        return dataMap;
    }
}
