package fi.internetix.edelphi.utils;

import org.apache.commons.codec.digest.DigestUtils;

public class TupasUtils {

  public static String calculateRequestMac(String actionId, String vers, String rcvId, String rcvKey, String langCode, String stamp, String idType, String retLink, String canLink, String rejLink, String keyVers, String alg) {
    return encodeMac(createMacString(actionId, vers, rcvId, langCode, stamp, idType, retLink, canLink, rejLink, keyVers, alg, rcvKey), alg);
  }
  
  public static String calculateResponseMac(String vers, String timestmp, String idnbr, String stamp, String custName, String keyVers, String alg, String custId, String custType, String rcvKey) {
    return encodeMac(createMacString(vers, timestmp, idnbr, stamp, custName, keyVers, alg, custId, custType, rcvKey), alg);
  }
  
  private static String createMacString(String... values) {
    StringBuilder macBuilder = new StringBuilder();
    
    for (String value : values) {
      macBuilder.append(value);
      macBuilder.append('&');
    }
    
    return macBuilder.toString();
  }
  
  private static String encodeMac(String mac, String alg) {
    if ("01".equals(alg)) {
      return DigestUtils.md5Hex(mac).toUpperCase();
    } else {
      return DigestUtils.shaHex(mac).toUpperCase();
    }
  }
  
}
