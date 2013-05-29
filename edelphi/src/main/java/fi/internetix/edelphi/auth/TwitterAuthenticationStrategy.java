package fi.internetix.edelphi.auth;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.xpath.XPathAPI;
import org.scribe.builder.api.Api;
import org.scribe.builder.api.TwitterApi;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Token;
import org.scribe.model.Verb;
import org.scribe.model.Verifier;
import org.scribe.oauth.OAuthService;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

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
    return TwitterApi.class;
  }

  protected AuthenticationResult processResponse(RequestContext requestContext, OAuthService service, String[] requestedScopes) {
    String verifier = requestContext.getString("oauth_verifier");
    
    Verifier v = new Verifier(verifier);
    Token requestToken = getRequestToken(requestContext);
    Token accessToken = service.getAccessToken(requestToken, v);

    OAuthRequest request = new OAuthRequest(Verb.GET, "http://api.twitter.com/1/account/verify_credentials.xml");
    service.signRequest(accessToken, request);
    Response response = request.send();
    
    // TODO: Store token...
    
    String responseStr = response.getBody();

    try {
      DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
      DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
      Document document = documentBuilder.parse(new ByteArrayInputStream(responseStr.getBytes("UTF-8")));

      Element documentElement = document.getDocumentElement();
      
      Node idNode = XPathAPI.selectSingleNode(documentElement, "id");
      Node nameNode = XPathAPI.selectSingleNode(documentElement, "name");

      String name = nameNode.getTextContent();
      
      List<String> emails = new ArrayList<String>();
      String externalId = idNode.getTextContent();
      String firstName = extractFirstName(name);
      String lastName = extractLastName(name);
      
      return processExternalLogin(requestContext, externalId, emails, firstName, lastName);

    } catch (SmvcRuntimeException smvc) {
      throw smvc;
    } catch (Exception ex) {
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
