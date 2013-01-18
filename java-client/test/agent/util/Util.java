package agent.util;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;

import org.json.JSONException;
import org.json.JSONObject;


public class Util {
    private static final String MESSAGE_CHARSET = "UTF-8";

	public static ByteBuffer encodeString(String text) {
        try {																			
            return ByteBuffer.wrap(text.getBytes(MESSAGE_CHARSET));						
        } catch (UnsupportedEncodingException e) {										
            throw new Error("Character set " + MESSAGE_CHARSET + " not found", e);		
        }
    }
	public static String decodeString(ByteBuffer buffer) {
        try {																			// try block to catch exceptions during connection
            byte[] bytes = new byte[buffer.remaining()];								// set up byte array to read in bytes
            buffer.get(bytes);															// get bytes from ByteBuffer
            return new String(bytes, MESSAGE_CHARSET);									// returns the decoded string
        } catch (UnsupportedEncodingException e) {										// catch exceptions
            throw new Error("Character set " + MESSAGE_CHARSET + " not found", e);		// throw error indicating the charset is not found
        }
    }
	
	public static String getStringFromJson(JSONObject json, String attribute) {
		try{
			return json.getString(attribute);
		}catch(JSONException e){
			try {
				return (String) json.getJSONArray(attribute).get(0);
			} catch (JSONException e2) {
				e.printStackTrace();
			}
		}
		return null;
	}

}
