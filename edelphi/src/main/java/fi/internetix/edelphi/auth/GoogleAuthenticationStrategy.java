package fi.internetix.edelphi.auth;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

import net.sf.json.JSONObject;

import org.scribe.builder.api.Api;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Token;
import org.scribe.model.Verb;
import org.scribe.model.Verifier;
import org.scribe.oauth.OAuthService;

import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets.Details;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson.JacksonFactory;

import fi.internetix.edelphi.auth.api.GoogleApi20;
import fi.internetix.edelphi.utils.AuthUtils;
import fi.internetix.smvc.controllers.RequestContext;


public class GoogleAuthenticationStrategy extends OAuthAuthenticationStrategy {

	private static final JsonFactory JSON_FACTORY = new JacksonFactory();
	private static final HttpTransport TRANSPORT = new NetHttpTransport();

	
  public GoogleAuthenticationStrategy() {
    super("https://www.googleapis.com/auth/userinfo.email", "https://www.googleapis.com/auth/userinfo.profile");
  }
  
  @Override
  protected String getApiKey() {
    return settings.get("oauth.google.apiKey");
  }

  @Override
  protected String getApiSecret() {
    return settings.get("oauth.google.apiSecret");
  }

  @Override
  protected String getOAuthCallbackURL(RequestContext requestContext) {
    return settings.get("oauth.google.callbackUrl");
  }
  
  @Override
  public String getName() {
    return "Google";
  }
  
  public GoogleCredential getCredential(RequestContext requestContext, String... scopes) {
  	
  	Details webDetails = new Details();
  	webDetails.setClientId(getApiKey());
  	webDetails.setClientSecret(getApiSecret());
  	webDetails.setRedirectUris(Arrays.asList(getOAuthCallbackURL(requestContext)));
  	webDetails.setAuthUri(GoogleApi20.AUTHORIZATION_URL);
  	webDetails.setTokenUri(GoogleApi20.TOKEN_URI);
  	
  	GoogleClientSecrets secrets = new GoogleClientSecrets();
  	secrets.setWeb(webDetails);

  	GoogleCredential credential = new GoogleCredential.Builder()
      .setClientSecrets(secrets)
      .setTransport(TRANSPORT)
      .setJsonFactory(JSON_FACTORY)
      .build();
  	
  	OAuthAccessToken accessToken = AuthUtils.getOAuthAccessToken(requestContext, getName(), scopes);
  	if (accessToken != null) {
  	  credential.setAccessToken(accessToken.getToken());
  	}
  	
  	return credential;
  }

  @Override
  protected Class<? extends Api> getApiClass() {
    return GoogleApi20.class;
  }

  protected AuthenticationResult processResponse(RequestContext requestContext, OAuthService service, String[] requestedScopes) {
    String verifier = requestContext.getString("code");
    
    Verifier v = new Verifier(verifier);
    Token accessToken = service.getAccessToken(null, v);
    JSONObject rawJson = JSONObject.fromObject(accessToken.getRawResponse());
    int expiresIn = rawJson.getInt("expires_in");
    Calendar calendar = new GregorianCalendar();
    calendar.setTime(new Date());
    calendar.add(Calendar.SECOND, expiresIn);
    Date expiresAt = calendar.getTime();
    AuthUtils.storeOAuthAccessToken(requestContext, getName(), new OAuthAccessToken(accessToken.getToken(), expiresAt, requestedScopes));
    
    List<String> scopesList = Arrays.asList(requestedScopes);
    
    boolean hasEmailScope = scopesList.contains("https://www.googleapis.com/auth/userinfo.email");
    boolean hasProfileScope = scopesList.contains("https://www.googleapis.com/auth/userinfo.profile");
    
    String email = null;
    String id = null;
    String firstName = null;
    String lastName = null;
    
    if (hasEmailScope) {
      OAuthRequest request = new OAuthRequest(Verb.GET, "https://www.googleapis.com/userinfo/email?alt=json");
      service.signRequest(accessToken, request);
      Response response = request.send();
      JSONObject emailJSON = JSONObject.fromObject(response.getBody());
      email = emailJSON.getJSONObject("data").getString("email");
    }
    
    if (hasProfileScope) {
      OAuthRequest request = new OAuthRequest(Verb.GET, "https://www.googleapis.com/oauth2/v1/userinfo?alt=json");
      service.signRequest(accessToken, request); 
      Response response = request.send();
      JSONObject userInfoJSON = JSONObject.fromObject(response.getBody());
      
      id = userInfoJSON.getString("id");
      firstName = userInfoJSON.optString("given_name");
      lastName = userInfoJSON.optString("family_name");
    }
    
    if (hasEmailScope && hasProfileScope)
      return processExternalLogin(requestContext, id, Arrays.asList(email), firstName, lastName);
    else {
      return AuthenticationResult.GRANT;
    }
  }

  @Override
  public String[] getKeys() {
    return new String[] {"oauth.google.apiKey", "oauth.google.apiSecret", "oauth.google.callbackUrl"};
  }

  @Override
  public String localizeKey(Locale locale, String key) {
    // TODO localize :)
    return key;
  }
  
}
