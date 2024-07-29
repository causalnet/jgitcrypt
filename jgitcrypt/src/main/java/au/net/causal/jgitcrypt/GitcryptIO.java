package au.net.causal.jgitcrypt;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Utility class for reading and writing sets of fields that are in Gitcrypt key files.
 *
 * See <a href="https://github.com/AGWA/git-crypt/blob/08dbdcfed4fb182c0efaacb32a6c46481ced095b/key.cpp">key.cpp in Gitcrypt</a>
 */
class GitcryptIO
{
    /**
     * Marker for when there are no more fields.
     */
    private static final int FIELD_ID_END = 0;

    /**
     * Reads all fields until end marker.
     *
     * @param data stream to read fields from.
     *
     * @return a map of field IDs to field data.
     *
     * @throws IOException if an I/O error occurs.
     */
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

    /**
     * Writes fields to a stream.
     *
     * @param data stream to write fields to.
     * @param dataMap a map of field IDs to field data.
     *
     * @throws IOException if an I/O error occurs.
     */
    static void writeFields(DataOutputStream data, Map<Integer, byte[]> dataMap)
    throws IOException
    {
        for (Map.Entry<Integer, byte[]> entry : dataMap.entrySet())
        {
            int fieldId = entry.getKey();
            int fieldLen = entry.getValue().length;
            byte[] fieldData = entry.getValue();

            data.writeInt(fieldId);
            data.writeInt(fieldLen);
            data.write(fieldData);
        }

        //End marker
        data.writeInt(FIELD_ID_END);
    }
}
