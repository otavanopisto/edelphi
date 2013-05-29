package fi.internetix.edelphi.auth;

import java.util.Date;

public class OAuthAccessToken {
  
  public OAuthAccessToken(String token, Date expires, String[] scopes) {
    this.token = token;
    this.expires = expires;
    this.scopes = scopes;
  }

  public Date getExpires() {
    return expires;
  }
  
  public String getToken() {
    return token;
  }
  
  public String[] getScopes() {
    return scopes;
  }
  
  private Date expires;
  private String token;
  private String[] scopes;
}
