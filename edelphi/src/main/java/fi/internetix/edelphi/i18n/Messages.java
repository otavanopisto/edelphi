package fi.internetix.edelphi.i18n;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

public class Messages {

  public static Messages getInstance() {
    return instance;
  }
  
  private static Messages instance;
  
  public String getText(Locale locale, String key) {
    return getResourceBundle(locale).getString(key);
  }
  
  public String getText(Locale locale, String key, Object[] params) {
    return MessageFormat.format(getText(locale, key), params);
  }
  
  public ResourceBundle getResourceBundle(Locale locale) {
    if (!bundles.containsKey(locale)) {
      ResourceBundle localeBundle = ResourceBundle.getBundle("fi.internetix.edelphi.i18n.locale", locale); 
      bundles.put(locale, localeBundle);
    }

    return bundles.get(locale);
  }
  
  private Map<Locale, ResourceBundle> bundles = new HashMap<Locale, ResourceBundle>();
  
  static {
    instance = new Messages();
  }
}

