package fi.internetix.edelphi.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.NavigableMap;
import java.util.TreeMap;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;

import fi.internetix.edelphi.EdelfoiStatusCode;
import fi.internetix.edelphi.dao.querylayout.QueryPageDAO;
import fi.internetix.edelphi.dao.querylayout.QueryPageSettingDAO;
import fi.internetix.edelphi.dao.querylayout.QueryPageSettingKeyDAO;
import fi.internetix.edelphi.domainmodel.querylayout.QueryPage;
import fi.internetix.edelphi.domainmodel.querylayout.QueryPageSetting;
import fi.internetix.edelphi.domainmodel.querylayout.QueryPageSettingKey;
import fi.internetix.edelphi.domainmodel.users.User;
import fi.internetix.smvc.SmvcRuntimeException;


public class QueryPageUtils {
  
  private static final String JSON_SERIALIZED_FILTER_START = "/**JSS-";
  private static final String JSON_SERIALIZED_FILTER_END = "-JSS**/";
  
  public static String getSetting(QueryPage queryPage, String name) {
    QueryPageSettingDAO queryPageSettingDAO = new QueryPageSettingDAO();
    QueryPageSettingKeyDAO queryPageSettingKeyDAO = new QueryPageSettingKeyDAO();
    QueryPageSettingKey key = queryPageSettingKeyDAO.findByName(name);
    if (key != null) {
      QueryPageSetting queryPageSetting = queryPageSettingDAO.findByKeyAndQueryPage(key, queryPage); 
      if (queryPageSetting != null)
        return queryPageSetting.getValue();
    }
    
    return null;
  }
  
  public static Long getLongSetting(QueryPage queryPage, String name) {
    return NumberUtils.createLong(getSetting(queryPage, name));
  }
  
  public static Integer getIntegerSetting(QueryPage queryPage, String name) {
    return NumberUtils.createInteger(getSetting(queryPage, name));
  }
  
  public static Double getDoubleSetting(QueryPage queryPage, String name) {
    return NumberUtils.createDouble(getSetting(queryPage, name));
  }

  public static NavigableMap<String, String> getMapSetting(QueryPage queryPage, String name) {
    return parseSerializedMap(getSetting(queryPage, name));
  }

  public static NavigableMap<String, String> parseSerializedMap(String serializedData) {
    NavigableMap<String, String> parsedMap = new TreeMap<String, String>();

    if (StringUtils.isNotBlank(serializedData)) {
      String[] keyValuePairs = serializedData.split("&");
      for (String keyValuePair : keyValuePairs) {
        String[] pair = keyValuePair.split("=");
        if (pair.length > 2) {
          throw new IllegalArgumentException("Malformed key value pair");
        }
        
        try {
          String key = URLDecoder.decode(pair[0], "UTF-8");
          String value = pair.length == 1 ? null : URLDecoder.decode(pair[1], "UTF-8");
          parsedMap.put(key, value);
        }
        catch (UnsupportedEncodingException e) {
          throw new SmvcRuntimeException(EdelfoiStatusCode.UNDEFINED, e.getLocalizedMessage(), e);
        }
      }
    }
    
    return parsedMap;
  }

  public static String serializeMap(NavigableMap<String, String> map) {
    StringBuilder resultBuilder = new StringBuilder();
    
    Iterator<String> keyIterator = map.navigableKeySet().iterator();
    while (keyIterator.hasNext()) {
      String key = keyIterator.next();
      String value = map.get(key);
      
      try {
        resultBuilder.append(URLEncode(key));
        resultBuilder.append("=");
        if (StringUtils.isNotBlank(value))
          resultBuilder.append(URLEncode(value));
      }
      catch (UnsupportedEncodingException e) {
        throw new SmvcRuntimeException(EdelfoiStatusCode.UNDEFINED, e.getLocalizedMessage(), e);
      }

      if (keyIterator.hasNext())
        resultBuilder.append("&");
    }
    
    return resultBuilder.toString();
  }
  
  public static List<String> parseSerializedList(String serializedData) {
    List<String> parsedList = new ArrayList<String>();

    if (StringUtils.isNotBlank(serializedData)) {
      String[] values = serializedData.split("&");
      for (String value : values) {
        try {
          parsedList.add(URLDecoder.decode(value, "UTF-8"));
        }
        catch (UnsupportedEncodingException e) {
          throw new SmvcRuntimeException(EdelfoiStatusCode.UNDEFINED, e.getLocalizedMessage(), e);
        }
      }
    }
    
    return parsedList;
  }

  public static String serializeList(List<String> list) {
    StringBuilder resultBuilder = new StringBuilder();
    
    Iterator<String> listIterator = list.iterator();
    while (listIterator.hasNext()) {
      String value = listIterator.next();
      
      try {
        resultBuilder.append(URLEncode(value));
      }
      catch (UnsupportedEncodingException e) {
        throw new SmvcRuntimeException(EdelfoiStatusCode.UNDEFINED, e.getLocalizedMessage(), e);
      }

      if (listIterator.hasNext())
        resultBuilder.append("&");
    }
    
    return resultBuilder.toString();
  }
  
  public static void setSetting(QueryPage queryPage, String name, String value, User modifier) {
    QueryPageSettingDAO queryPageSettingDAO = new QueryPageSettingDAO();
    QueryPageSettingKeyDAO queryPageSettingKeyDAO = new QueryPageSettingKeyDAO();
    QueryPageSettingKey key = queryPageSettingKeyDAO.findByName(name);
    QueryPageDAO queryPageDAO = new QueryPageDAO();
    
    if (key == null) {
      key = queryPageSettingKeyDAO.create(name);
    }
    
    QueryPageSetting queryPageSetting = queryPageSettingDAO.findByKeyAndQueryPage(key, queryPage);
    
    if (StringUtils.isBlank(value)) {
      if (queryPageSetting != null) {
        queryPageSettingDAO.delete(queryPageSetting);
      }
    }
    else {
      if (queryPageSetting != null) 
        queryPageSettingDAO.updateValue(queryPageSetting, value);
      else
        queryPageSettingDAO.create(key, queryPage, value);
    }
    
    queryPageDAO.updateLastModified(queryPage, new Date(), modifier);
  }
  
  public static String filterJsonSerializedSetting(String value) {
    return new StringBuilder(JSON_SERIALIZED_FILTER_START).append(value).append(JSON_SERIALIZED_FILTER_END).toString();
  }
  
  public static String unfilterJsonSerializedSetting(String value) {
    if (value.startsWith(JSON_SERIALIZED_FILTER_START) && value.endsWith(JSON_SERIALIZED_FILTER_END))
      return value.substring(JSON_SERIALIZED_FILTER_START.length(), value.length() - JSON_SERIALIZED_FILTER_END.length());
    return value;
  }
  
  public static void setMapSetting(QueryPage queryPage, String name, NavigableMap<String, String> value, User modifier) {
    setSetting(queryPage, name, serializeMap(value), modifier);
  }

  private static String URLEncode(String value) throws UnsupportedEncodingException {
    if (StringUtils.isNotBlank(value))
      return URLEncoder.encode(value, "UTF-8").replace("+", "%20");
    else
      return null;
  }
}
