package fi.internetix.edelphi.auth;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Arrays;
import java.util.Locale;

import net.sf.json.JSONObject;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.net.URLCodec;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import fi.internetix.edelphi.EdelfoiStatusCode;
import fi.internetix.edelphi.i18n.Messages;
import fi.internetix.edelphi.utils.AuthUtils;
import fi.internetix.smvc.SmvcRuntimeException;
import fi.internetix.smvc.controllers.RequestContext;


public class NingAuthenticationStrategy extends AbstractAuthenticationStrategy implements AuthenticationProvider {

  private JSONObject doTokenRequest(URL url, String username, String password, BasicNameValuePair... data) throws IOException, DecoderException, URISyntaxException {
    HttpClient httpclient = new DefaultHttpClient();
    HttpPost httpPost = new HttpPost(url.toURI());
    
    String authString = username + ":" + password;
    byte[] authEncBytes = Base64.encodeBase64(authString.getBytes("UTF-8"));

    httpPost.addHeader("Authorization", "Basic " + new String(authEncBytes));
    httpPost.setEntity(new UrlEncodedFormEntity(Arrays.asList(data)));

    HttpResponse response = httpclient.execute(httpPost);
    HttpEntity entity = response.getEntity();
    
    if (entity != null) {
      return JSONObject.fromObject(EntityUtils.toString(entity, "UTF-8"));
    } else {
      return null;
    }
  }
  
  private JSONObject doGetRequest(URL url, String token, String tokenSecret) throws IOException, DecoderException, URISyntaxException {
    HttpClient httpclient = new DefaultHttpClient();
    HttpGet httpGet = new HttpGet(url.toURI());
    
    StringBuilder authorizationBuilder = new StringBuilder();
    
    authorizationBuilder
      .append("OAuth oauth_signature_method=\"PLAINTEXT\",")
      .append("oauth_consumer_key=\"")
      .append(encodeUrl(getApiKey()))
      .append("\",")
      .append("oauth_token=\"")
      .append(encodeUrl(token))
      .append("\",")
      .append("oauth_signature=\"")
      .append(encodeUrl(getApiSecret()) + "%26" + encodeUrl(tokenSecret))
      .append('"');
    
    httpGet.addHeader("Authorization", authorizationBuilder.toString());
    
    HttpResponse response = httpclient.execute(httpGet);
    HttpEntity entity = response.getEntity();
    
    if (entity != null) {
      return JSONObject.fromObject(EntityUtils.toString(entity, "UTF-8"));
    } else {
      return null;
    }
  }

  @Override
  public AuthenticationResult processLogin(RequestContext requestContext) {
    String username = StringUtils.lowerCase(requestContext.getString("username"));
    String password = requestContext.getString("password"); 
    
    Messages messages = Messages.getInstance();
    Locale locale = requestContext.getRequest().getLocale();

    try {
      
      URL authUrl = new URL("https://external.ningapis.com/xn/rest/" + getSubDomain() + "/1.0/Token?xn_pretty=true");
      JSONObject tokenResult = doTokenRequest(authUrl, username, password, 
        new BasicNameValuePair("oauth_signature_method", "PLAINTEXT"),
        new BasicNameValuePair("oauth_consumer_key", getApiKey()),
        new BasicNameValuePair("oauth_signature", getApiSecret() + '&')
      );
      
      if (tokenResult.getBoolean("success")) {
        JSONObject tokenEntry = tokenResult.getJSONObject("entry");
        String token = tokenEntry.getString("oauthToken");
        String tokenSecret = tokenEntry.getString("oauthTokenSecret");
        
        AuthUtils.storeOAuthAccessToken(requestContext, getName(), new OAuthAccessToken(token, null, null));
        
        URL userUrl = new URL("https://external.ningapis.com/xn/rest/" + getSubDomain() + "/1.0/User?fields=" + encodeUrl("email,fullName")); // ?id=" + author + "&");
        JSONObject userResult = doGetRequest(userUrl, token, tokenSecret);
        if (userResult.getBoolean("success")) {
          JSONObject userEntry = userResult.getJSONObject("entry");
          String id = userEntry.getString("id");
          String email = userEntry.optString("email");
          String fullName = userEntry.optString("fullName");
          String firstName = null;
          String lastName = null;
          
          if (StringUtils.isNotBlank(fullName)) {
            firstName = extractFirstName(fullName);
            lastName = extractLastName(fullName);
          }
          
          return processExternalLogin(requestContext, id, Arrays.asList(email), firstName, lastName);
        } else {
          throw new SmvcRuntimeException(EdelfoiStatusCode.INVALID_LOGIN, messages.getText(locale, "exception.1007.invalidLogin"));
        }
      } else {
        throw new SmvcRuntimeException(EdelfoiStatusCode.INVALID_LOGIN, messages.getText(locale, "exception.1007.invalidLogin"));
      }
    } catch (MalformedURLException e) {
      throw new AuthenticationRuntimeException(e);
    } catch (IOException e) {
      throw new AuthenticationRuntimeException(e);
    } catch (DecoderException e) {
      throw new AuthenticationRuntimeException(e);
    } catch (URISyntaxException e) {
      throw new AuthenticationRuntimeException(e);
    }
  }
  
  private String getApiSecret() {
    return settings.get("ning.apiSecret");
  }

  private String getApiKey() {
    return settings.get("ning.apiKey");
  }

  private String getSubDomain() {
    return settings.get("ning.subDomain");
  }

  private String encodeUrl(String value) throws UnsupportedEncodingException, DecoderException {
    return new String(URLCodec.decodeUrl(value.getBytes("UTF-8")), "UTF-8");
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
  public String getName() {
    return "Ning";
  }

  @Override
  public boolean requiresCredentials() {
    return true;
  }

  @Override
  public String[] getKeys() {
    return new String[] {"ning.apiKey", "ning.apiSecret", "ning.subDomain"};
  }

  @Override
  public String localizeKey(Locale locale, String key) {
    // TODO localize :)
    return key;
  }

}
