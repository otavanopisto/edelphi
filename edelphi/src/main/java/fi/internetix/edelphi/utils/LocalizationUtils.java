package fi.internetix.edelphi.utils;

import java.util.Locale;

import fi.internetix.edelphi.dao.base.LocalizedValueDAO;
import fi.internetix.edelphi.domainmodel.base.LocalizedEntry;
import fi.internetix.edelphi.domainmodel.base.LocalizedValue;

public class LocalizationUtils {

  public static String getLocalizedText(LocalizedEntry entry, Locale locale) {
    // TODO: Default value
    LocalizedValueDAO localizedValueDAO = new LocalizedValueDAO();
    LocalizedValue localizedValue = localizedValueDAO.findByEntryAndLocale(entry, locale);
    if (localizedValue == null)
      localizedValue = localizedValueDAO.findByEntryAndLocale(entry, new Locale(locale.getLanguage()));
    
    return localizedValue != null ? localizedValue.getText() : null;
  }
}
