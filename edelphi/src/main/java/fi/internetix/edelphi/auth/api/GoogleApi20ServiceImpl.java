package fi.internetix.edelphi.auth.api;

import net.sf.json.JSONObject;

import org.scribe.builder.api.DefaultApi20;
import org.scribe.model.OAuthConfig;
import org.scribe.model.OAuthConstants;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Token;
import org.scribe.model.Verifier;
import org.scribe.oauth.OAuth20ServiceImpl;

public class GoogleApi20ServiceImpl extends OAuth20ServiceImpl {

  public GoogleApi20ServiceImpl(DefaultApi20 api, OAuthConfig config) {
    super(api, config);

    this.api_ = api;
    this.config_ = config;
  }

  @Override
  public Token getAccessToken(Token requestToken, Verifier verifier) {
    OAuthRequest request = new OAuthRequest(api_.getAccessTokenVerb(), api_.getAccessTokenEndpoint());
    request.addBodyParameter(OAuthConstants.CLIENT_ID, config_.getApiKey());
    request.addBodyParameter(OAuthConstants.CLIENT_SECRET, config_.getApiSecret());
    request.addBodyParameter(OAuthConstants.CODE, verifier.getValue());
    request.addBodyParameter(OAuthConstants.REDIRECT_URI, config_.getCallback());
    request.addBodyParameter("grant_type", "authorization_code");
    if (config_.hasScope())
      request.addBodyParameter(OAuthConstants.SCOPE, config_.getScope());
    Response response = request.send();
    JSONObject jsonObject = JSONObject.fromObject(response.getBody());
    return api_.getAccessTokenExtractor().extract(jsonObject.toString(0));
  }

  private final OAuthConfig config_;
  private final DefaultApi20 api_;
}
