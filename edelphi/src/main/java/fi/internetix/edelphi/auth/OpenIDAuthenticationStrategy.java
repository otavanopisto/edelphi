package fi.internetix.edelphi.auth;

import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpSession;

import org.openid4java.association.AssociationException;
import org.openid4java.consumer.ConsumerException;
import org.openid4java.consumer.ConsumerManager;
import org.openid4java.consumer.VerificationResult;
import org.openid4java.discovery.DiscoveryException;
import org.openid4java.discovery.DiscoveryInformation;
import org.openid4java.discovery.Identifier;
import org.openid4java.message.AuthRequest;
import org.openid4java.message.AuthSuccess;
import org.openid4java.message.MessageException;
import org.openid4java.message.ParameterList;
import org.openid4java.message.ax.AxMessage;
import org.openid4java.message.ax.FetchRequest;
import org.openid4java.message.ax.FetchResponse;

import fi.internetix.edelphi.EdelfoiStatusCode;
import fi.internetix.edelphi.i18n.Messages;
import fi.internetix.edelphi.utils.RequestUtils;
import fi.internetix.smvc.SmvcRuntimeException;
import fi.internetix.smvc.controllers.RequestContext;

public class OpenIDAuthenticationStrategy 
    extends AbstractAuthenticationStrategy {
  
  public OpenIDAuthenticationStrategy() {
    consumerManager = new ConsumerManager();
  }
  
  public String getName() {
    return "OpenID";
  }
  
  protected String getUserOpenIdString(RequestContext requestContext) {
    return requestContext.getString("openId");
  }

  public AuthenticationResult processLogin(RequestContext requestContext) {
    if (!"rsp".equals(requestContext.getString("_stg"))) {
      performDiscovery(requestContext);
      return AuthenticationResult.PROCESSING;
    } else {
      return processResponse(requestContext);
    }
  }
  
  public boolean requiresCredentials() {
    return false;
  }
  
  public void performDiscovery(RequestContext requestContext) {
    try {
      HttpSession session = requestContext.getRequest().getSession();
      
      String userString = getUserOpenIdString(requestContext);
      System.out.println(getName() + " auth request with id: " + userString);

      // perform discovery on the user-supplied identifier
      List<?> discoveries = consumerManager.discover(userString);

      // attempt to associate with the OpenID provider
      // and retrieve one service endpoint for authentication
      DiscoveryInformation discovered = consumerManager.associate(discoveries);
      
      // store the discovery information in the user's session for later use
      session.setAttribute("discovered", discovered);
      
      // Construct a path back to dologin.page 
      String baseURL = RequestUtils.getBaseUrl(requestContext.getRequest());
      StringBuilder returnURL = new StringBuilder(baseURL)
        .append("/dologin.page?loginType=" + getName() + "&_stg=rsp");
      requestContext.getRequest().getSession().setAttribute("expectedLoginUrl", returnURL.toString());
     
      // obtain a AuthRequest message to be sent to the OpenID provider
      AuthRequest authReq = consumerManager.authenticate(discovered, returnURL.toString());
      
      // Attribute Exchange example: fetching the 'email' attribute
      // http://openid.net/specs/openid-attribute-properties-list-1_0-01.html
      FetchRequest fetch = FetchRequest.createFetchRequest();
      fetch.addAttribute("email",
          // attribute alias
          "http://schema.openid.net/contact/email",   // type URI
          true);                                      // required
      fetch.addAttribute("firstname",
          // attribute alias
          "http://axschema.org/namePerson/first",   // type URI
          true);                                      // required
      fetch.addAttribute("lastname",
          // attribute alias
          "http://axschema.org/namePerson/last",   // type URI
          true);                                      // required
      
//      http://openid.net/schema/media/image

      // attach the extension to the authentication request
      authReq.addExtension(fetch);
      
//      if (!discovered.isVersion2()) {
        // Option 1: GET HTTP-redirect to the OpenID Provider endpoint
        // The only method supported in OpenID 1.x
        // redirect-URL usually limited ~2048 bytes
      System.out.println("DestiURL: " + authReq.getDestinationUrl(true));
        requestContext.setRedirectURL(authReq.getDestinationUrl(true));
//      } else {
//        // Option 2: HTML FORM Redirection (Allows payloads >2048 bytes)
//
//        RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("formredirection.jsp");
//        httpReq.setAttribute("parameterMap", authReq.getParameterMap());
//        httpReq.setAttribute("destinationUrl", authReq.getDestinationUrl(false));
//        dispatcher.forward(httpReq, httpResp);
//      }      
      
    } catch (DiscoveryException e) {
      throw new AuthenticationRuntimeException(e);
    } catch (MessageException e) {
      throw new AuthenticationRuntimeException(e);
    } catch (ConsumerException e) {
      throw new AuthenticationRuntimeException(e);
    }
  }

  @SuppressWarnings("unchecked")
  public AuthenticationResult processResponse(RequestContext requestContext) {
    try {
      HttpSession session = requestContext.getRequest().getSession();
      
      // extract the parameters from the authentication response
      // (which comes in as a HTTP request from the OpenID provider)
      ParameterList openidResp = new ParameterList(requestContext.getRequest().getParameterMap());
  
      // retrieve the previously stored discovery information
      DiscoveryInformation discovered = (DiscoveryInformation) session.getAttribute("discovered");
  
      // extract the receiving URL from the HTTP request
      String receivingURL = (String) requestContext.getRequest().getSession().getAttribute("expectedLoginUrl");
      requestContext.getRequest().getSession().removeAttribute("expectedLoginUrl");
  
      // verify the response
      VerificationResult verification = consumerManager.verify(receivingURL, openidResp, discovered);
      
      // examine the verification result and extract the verified identifier
      Identifier verified = verification.getVerifiedId();

      if (verified != null) {
        AuthSuccess authSuccess = (AuthSuccess) verification.getAuthResponse();
        List<String> emails = null;
        String firstName = null;
        String lastName = null;
        
        if (authSuccess.hasExtension(AxMessage.OPENID_NS_AX)) {
          FetchResponse fetchResp = (FetchResponse) authSuccess.getExtension(AxMessage.OPENID_NS_AX);
          emails = fetchResp.getAttributeValues("email");
          firstName = fetchResp.getAttributeValue("firstname");
          lastName = fetchResp.getAttributeValue("lastname");
        }
        
        System.out.println("firstName = " + firstName);
        System.out.println("lastName = " + lastName);
        System.out.println("getIdentifier = " + verified.getIdentifier());
        
        for (int i = 0; i < emails.size(); i++) {
          System.out.println("emails[" + i + "] = " + emails.get(i));
        }
        
        String loginServer = verification.getAuthResponse().getParameterValue("openid.op_endpoint");
        System.out.println(loginServer);

        // Try to identify user by identifier

        String externalId = verified.getIdentifier();

        return processExternalLogin(requestContext, externalId, emails, firstName, lastName);
      }
      
      Messages messages = Messages.getInstance();
      Locale locale = requestContext.getRequest().getLocale();
      throw new SmvcRuntimeException(EdelfoiStatusCode.INVALID_LOGIN, messages.getText(locale, "exception.1007.invalidLogin"));
    } catch (MessageException e) {
      throw new AuthenticationRuntimeException(e);
    } catch (DiscoveryException e) {
      throw new AuthenticationRuntimeException(e);
    } catch (AssociationException e) {
      throw new AuthenticationRuntimeException(e);
    }
  }

  private ConsumerManager consumerManager;

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

}
