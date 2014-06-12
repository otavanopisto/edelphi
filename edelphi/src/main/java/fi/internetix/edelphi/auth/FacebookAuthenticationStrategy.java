package fi.internetix.edelphi.auth;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import net.sf.json.JSONObject;

import org.scribe.builder.api.Api;
import org.scribe.builder.api.FacebookApi;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Token;
import org.scribe.model.Verb;
import org.scribe.model.Verifier;
import org.scribe.oauth.OAuthService;

import fi.internetix.smvc.controllers.RequestContext;


public class FacebookAuthenticationStrategy extends OAuthAuthenticationStrategy {

  public FacebookAuthenticationStrategy() {
    super("email");
  }
  
  @Override
  public String getName() {
    return "Facebook";
  }

  @Override
  protected String getApiKey() {
    return settings.get("oauth.facebook.apiKey");
  }

  @Override
  protected String getApiSecret() {
    return settings.get("oauth.facebook.apiSecret");
  }

  @Override
  protected String getOAuthCallbackURL(RequestContext requestContext) {
    return settings.get("oauth.facebook.callbackUrl");
  }
  
  @Override
  protected Class<? extends Api> getApiClass() {
    return FacebookApi.class;
  }

  protected AuthenticationResult processResponse(RequestContext requestContext, OAuthService service, String[] requestedScopes) {
    String verifier = requestContext.getString("code");
    
    Verifier v = new Verifier(verifier);
    Token accessToken = service.getAccessToken(null, v);

    System.out.println(accessToken);

    OAuthRequest request = new OAuthRequest(Verb.GET, "https://graph.facebook.com/me");
    service.signRequest(accessToken, request); // the access token from step 4
    Response response = request.send();
    
    // TODO: Store token...

//    System.out.println(response.getBody());

    JSONObject o = JSONObject.fromObject(response.getBody());
    
    String externalId = o.getString("id");
    String emailAddr = o.getString("email");
    String firstName = o.getString("first_name");
    String lastName = o.getString("last_name");

    List<String> emails = new ArrayList<String>();
    emails.add(emailAddr);

    return processExternalLogin(requestContext, externalId, emails, firstName, lastName);
  }

  @Override
  public String[] getKeys() {
    return new String[] {"oauth.facebook.apiKey", "oauth.facebook.apiSecret", "oauth.facebook.callbackUrl"};
  }

  @Override
  public String localizeKey(Locale locale, String key) {
    // TODO localize :)
    return key;
  }
}
