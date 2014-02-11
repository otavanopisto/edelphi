package fi.internetix.edelphi.auth;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import net.sf.json.JSONObject;

import org.scribe.builder.api.Api;
import org.scribe.builder.api.TwitterApi;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Token;
import org.scribe.model.Verb;
import org.scribe.model.Verifier;
import org.scribe.oauth.OAuthService;

import fi.internetix.edelphi.EdelfoiStatusCode;
import fi.internetix.edelphi.i18n.Messages;
import fi.internetix.smvc.SmvcRuntimeException;
import fi.internetix.smvc.controllers.RequestContext;

public class TwitterAuthenticationStrategy extends OAuthAuthenticationStrategy {

  @Override
  protected String getApiKey() {
    return settings.get("oauth.twitter.apiKey");
  }

  @Override
  protected String getApiSecret() {
    return settings.get("oauth.twitter.apiSecret");
  }

  @Override
  protected String getOAuthCallbackURL(RequestContext requestContext) {
    return settings.get("oauth.twitter.callbackUrl");
  }
  
  @Override
  public String getName() {
    return "Twitter";
  }

  @Override
  protected Class<? extends Api> getApiClass() {
    return TwitterApi.SSL.class;
  }

  protected AuthenticationResult processResponse(RequestContext requestContext, OAuthService service, String[] requestedScopes) {
    String verifier = requestContext.getString("oauth_verifier");
    
    Verifier v = new Verifier(verifier);
    Token requestToken = getRequestToken(requestContext);
    Token accessToken = service.getAccessToken(requestToken, v);

    OAuthRequest request = new OAuthRequest(Verb.GET, "https://api.twitter.com/1.1/account/verify_credentials.json");
    service.signRequest(accessToken, request);
    Response response = request.send();
    
    // TODO: Store token...
    
    try {
      JSONObject responseJson = JSONObject.fromObject(response.getBody());
      List<String> emails = new ArrayList<String>();
      String externalId = responseJson.getString("id");
      String name = responseJson.getString("name");
      String firstName = extractFirstName(name);
      String lastName = extractLastName(name);
      return processExternalLogin(requestContext, externalId, emails, firstName, lastName);
    }
    catch (SmvcRuntimeException smvc) {
      throw smvc;
    }
    catch (Exception ex) {
      Messages messages = Messages.getInstance();
      Locale locale = requestContext.getRequest().getLocale();
      throw new SmvcRuntimeException(EdelfoiStatusCode.INVALID_AUTHENTICATION_REQUEST, messages.getText(locale, "exception.1010.invalidAuthenticationRequest"), ex);
    }
  }

  private String extractLastName(String name) {
    int lastIndexOf = name.lastIndexOf(' ');
    
    if (lastIndexOf == -1)
      return null;
    else
      return name.substring(lastIndexOf + 1);
  }
  
  private String extractFirstName(String name) {
    int lastIndexOf = name.lastIndexOf(' ');
    
    if (lastIndexOf == -1)
      return null;
    else
      return name.substring(0, lastIndexOf);
  }

  @Override
  public String[] getKeys() {
    return new String[] {"oauth.twitter.apiKey", "oauth.twitter.apiSecret", "oauth.twitter.callbackUrl"};
  }

  @Override
  public String localizeKey(Locale locale, String key) {
    // TODO localize :)
    return key;
  }
}
