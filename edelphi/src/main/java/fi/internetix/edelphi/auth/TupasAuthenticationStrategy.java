package fi.internetix.edelphi.auth;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import fi.internetix.edelphi.EdelfoiStatusCode;
import fi.internetix.edelphi.i18n.Messages;
import fi.internetix.edelphi.utils.RequestUtils;
import fi.internetix.edelphi.utils.SystemUtils;
import fi.internetix.edelphi.utils.TupasUtils;
import fi.internetix.smvc.SmvcRuntimeException;
import fi.internetix.smvc.controllers.RequestContext;

public class TupasAuthenticationStrategy extends AbstractAuthenticationStrategy {

  public TupasAuthenticationStrategy() {
    String banks = SystemUtils.getSettingValue("tupas.banks");
    if (!StringUtils.isBlank(banks)) {
      String[] bankIds = banks.split(",");
      for (String bankId : bankIds) {
        String actionUrl = SystemUtils.getSettingValue("tupas." + bankId + ".ACTION_URL");
        String actionId = SystemUtils.getSettingValue("tupas." + bankId + ".A01Y_ACTION_ID");
        String vers = SystemUtils.getSettingValue("tupas." + bankId + ".A01Y_VERS");
        String rcvId = SystemUtils.getSettingValue("tupas." + bankId + ".A01Y_RCVID");
        String rcvKey = SystemUtils.getSettingValue("tupas." + bankId + ".A01Y_RCVKEY");
        String langCode = SystemUtils.getSettingValue("tupas." + bankId + ".A01Y_LANGCODE");
        String idType = SystemUtils.getSettingValue("tupas." + bankId + ".A01Y_IDTYPE");
        String keyVers = SystemUtils.getSettingValue("tupas." + bankId + ".A01Y_KEYVERS");
        String alg = SystemUtils.getSettingValue("tupas." + bankId + ".A01Y_ALG");
        
        addBank(bankId, actionUrl, actionId, vers, rcvId, rcvKey, langCode, idType, keyVers, alg);
      }
    }
  }
  
  @Override
  public String getName() {
    return "Tupas";
  }

  public boolean requiresCredentials() {
    return false;
  }

  @Override
  public AuthenticationResult processLogin(RequestContext requestContext) {
    String action = requestContext.getString("action");

    if ("ret".equals(action)) {
      String bank = requestContext.getString("bank");
      BankSettings bankSettings = banks.get(bank);
      if (bankSettings != null) {

        String vers = requestContext.getString("B02K_VERS");
        String timestmp = requestContext.getString("B02K_TIMESTMP");
        String idnbr = requestContext.getString("B02K_IDNBR");
        String stamp = requestContext.getString("B02K_STAMP");
        String custName = requestContext.getString("B02K_CUSTNAME");
        String keyVers = requestContext.getString("B02K_KEYVERS");
        String alg = requestContext.getString("B02K_ALG");
        String custId = requestContext.getString("B02K_CUSTID");
        String custType = requestContext.getString("B02K_CUSTTYPE");
        String mac = requestContext.getString("B02K_MAC");
        String calculatedMac = TupasUtils.calculateResponseMac(vers, timestmp, idnbr, stamp, custName, keyVers, alg, custId, custType, bankSettings.getRcvKey());

        if (!calculatedMac.equals(mac)) {
          Messages messages = Messages.getInstance();
          Locale locale = requestContext.getRequest().getLocale();
          throw new SmvcRuntimeException(EdelfoiStatusCode.INVALID_AUTHENTICATION_REQUEST, messages.getText(locale, "exception.1010.invalidAuthenticationRequest"));
        }
        
        String[] names = custName.split(" ");
        String lastName = names[names.length - 1];
        String firstName = "";
        for (int i = 0, l = names.length - 1; i < l; i++) {
          firstName += names[i];
          if (i < (l - 1))
            firstName += ' ';
        }
        
        return processExternalLogin(requestContext, custId, new ArrayList<String>(), firstName, lastName);
      }
      else {
        Messages messages = Messages.getInstance();
        Locale locale = requestContext.getRequest().getLocale();
        throw new SmvcRuntimeException(EdelfoiStatusCode.INVALID_AUTHENTICATION_REQUEST, messages.getText(locale, "exception.1010.invalidAuthenticationRequest"));
      }
    }
    else if ("rej".equals(action)) {
      Messages messages = Messages.getInstance();
      Locale locale = requestContext.getRequest().getLocale();
      throw new SmvcRuntimeException(EdelfoiStatusCode.INVALID_LOGIN, messages.getText(locale, "exception.1007.invalidLogin"));
    }
    else if ("can".equals(action)) {
      Messages messages = Messages.getInstance();
      Locale locale = requestContext.getRequest().getLocale();
      throw new SmvcRuntimeException(EdelfoiStatusCode.LOGIN_CANCELED, messages.getText(locale, "exception.1011.loginCanceled"));
    }
    Messages messages = Messages.getInstance();
    Locale locale = requestContext.getRequest().getLocale();
    throw new SmvcRuntimeException(EdelfoiStatusCode.INVALID_AUTHENTICATION_REQUEST, messages.getText(locale, "exception.1010.invalidAuthenticationRequest"));
  }
  
  public void addBank(String bank, String actionUrl, String actionId, String vers, String rcvId, String rcvKey, String langCode, String idType, String keyVers, String alg) {
    banks.put(bank, new BankSettings(actionUrl, actionId, vers, rcvId, rcvKey, langCode, idType, keyVers, alg));
  }
  
  public void appendBankSettings(RequestContext requestContext) {
    String baseURL = RequestUtils.getBaseUrl(requestContext.getRequest());
    Set<String> bankIds = banks.keySet();
    
    requestContext.getRequest().setAttribute("tupasBankIds", bankIds);
    
    for (String bankId : bankIds) {
      BankSettings bankSettings = banks.get(bankId);

      StringBuilder returnUrlBuilder = new StringBuilder(baseURL)
         .append("/dologin.page?loginType=Tupas&bank=")
         .append(bankId);
      String returnUrl = returnUrlBuilder.toString();
      
      String retLink = returnUrl + "&action=ret";
      String canLink = returnUrl + "&action=can";
      String rejLink = returnUrl + "&action=rej";
      String stamp = createStamp();      
      
      String actionUrl = bankSettings.getActionUrl();
      String actionId = bankSettings.getActionId();
      String vers = bankSettings.getVers();
      String rcvId = bankSettings.getRcvId();
      String rcvKey = bankSettings.getRcvKey();
      String langCode = bankSettings.getLangCode();
      String idType = bankSettings.getIdType();
      String keyVers = bankSettings.getKeyVers();
      String alg = bankSettings.getAlg();

      requestContext.getRequest().setAttribute("tupas_" + bankId + "_ACTION_URL", actionUrl);
      requestContext.getRequest().setAttribute("tupas_" + bankId + "_A01Y_ACTION_ID", actionId);
      requestContext.getRequest().setAttribute("tupas_" + bankId + "_A01Y_VERS", vers);
      requestContext.getRequest().setAttribute("tupas_" + bankId + "_A01Y_RCVID", rcvId);
      requestContext.getRequest().setAttribute("tupas_" + bankId + "_A01Y_LANGCODE", langCode);
      requestContext.getRequest().setAttribute("tupas_" + bankId + "_A01Y_STAMP", stamp);
      requestContext.getRequest().setAttribute("tupas_" + bankId + "_A01Y_IDTYPE", idType);
      requestContext.getRequest().setAttribute("tupas_" + bankId + "_A01Y_RETLINK", retLink);
      requestContext.getRequest().setAttribute("tupas_" + bankId + "_A01Y_CANLINK", canLink);
      requestContext.getRequest().setAttribute("tupas_" + bankId + "_A01Y_REJLINK", rejLink);
      requestContext.getRequest().setAttribute("tupas_" + bankId + "_A01Y_KEYVERS", keyVers);
      requestContext.getRequest().setAttribute("tupas_" + bankId + "_A01Y_ALG", alg);
      requestContext.getRequest().setAttribute("tupas_" + bankId + "_A01Y_MAC", TupasUtils.calculateRequestMac(actionId, vers, rcvId, rcvKey, langCode, stamp, idType, retLink, canLink, rejLink, keyVers, alg));
    }
  }
  
  @Override
  public String[] getKeys() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public String localizeKey(Locale locale, String key) {
    // TODO Auto-generated method stub
    return null;
  }

  private String createStamp() {
    Date now = new Date();
    String stamp = stampFormat.format(now) + String.format("%02d", Math.round(Math.random() * 100));
    return stamp;
  }
  
  private SimpleDateFormat stampFormat = new SimpleDateFormat("yyyyMMddHHmmssSSS");
  
  private Map<String, BankSettings> banks = new HashMap<String, TupasAuthenticationStrategy.BankSettings>();

  private class BankSettings {

    public BankSettings(String actionUrl, String actionId, String vers, String rcvId, String rcvKey, String langCode, String idType, String keyVers, String alg) {
      this.actionUrl = actionUrl;
      this.actionId = actionId;
      this.vers = vers;
      this.rcvId = rcvId;
      this.rcvKey = rcvKey;
      this.langCode = langCode;
      this.idType = idType;
      this.keyVers = keyVers;
      this.alg = alg;
    }

    public String getActionUrl() {
      return actionUrl;
    }

    public String getActionId() {
      return actionId;
    }

    public String getVers() {
      return vers;
    }

    public String getRcvId() {
      return rcvId;
    }

    public String getRcvKey() {
      return rcvKey;
    }

    public String getLangCode() {
      return langCode;
    }

    public String getIdType() {
      return idType;
    }

    public String getKeyVers() {
      return keyVers;
    }

    public String getAlg() {
      return alg;
    }

    private String actionUrl;
    private String actionId;
    private String vers;
    private String rcvId;
    private String rcvKey;
    private String langCode;
    private String idType;
    private String keyVers;
    private String alg;
  }

}