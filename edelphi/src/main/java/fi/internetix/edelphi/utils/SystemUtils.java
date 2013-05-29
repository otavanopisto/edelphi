package fi.internetix.edelphi.utils;

import fi.internetix.edelphi.dao.system.SettingDAO;
import fi.internetix.edelphi.dao.system.SettingKeyDAO;
import fi.internetix.edelphi.domainmodel.system.Setting;
import fi.internetix.edelphi.domainmodel.system.SettingKey;

public class SystemUtils {
  
  public static final String ENV_PRODUCTION = "production";
  public static final String ENV_DEVELOPMENT = "development";
  
  public static boolean isProductionEnvironment() {
    return ENV_PRODUCTION.equals(getSettingValue("system.environment"));
  }
  
  public static String getSettingValue(String name) {
    SettingKeyDAO settingKeyDAO = new SettingKeyDAO();
    SettingDAO settingDAO = new SettingDAO();
    
    SettingKey settingKey = settingKeyDAO.findByName(name);
    if (settingKey != null) {
      Setting setting = settingDAO.findByKey(settingKey);
      if (setting != null)
        return setting.getValue();
    }
    
    return null;
  }
  
}
 