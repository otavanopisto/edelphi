package fi.internetix.edelphi.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

public class StreamUtils {

  public static byte[] readStreamToByteArray(InputStream inputStream) throws IOException {
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    byte[] buffer = new byte[1024];
    int l = 0;
    while ((l = inputStream.read(buffer, 0, 1024)) > 0) {
      outputStream.write(buffer, 0, l);
    }
    
    return outputStream.toByteArray();
  }
  
  public static String readStreamToString(InputStream inputStream, String charset) throws UnsupportedEncodingException, IOException {
    return new String(readStreamToByteArray(inputStream), charset);
  }
}
