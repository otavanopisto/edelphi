package fi.internetix.edelphi;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;

import fi.internetix.edelphi.utils.MailUtils;
import fi.internetix.edelphi.utils.SystemUtils;
import fi.internetix.smvc.AccessDeniedException;
import fi.internetix.smvc.LoginRequiredException;
import fi.internetix.smvc.PageNotFoundException;
import fi.internetix.smvc.SmvcRuntimeException;

public class PlatformErrorListener implements fi.internetix.smvc.dispatcher.PlatformErrorListener {

  @Override
  public void onLoginRequiredException(HttpServletRequest request, HttpServletResponse response, LoginRequiredException e) {
  }

  @Override
  public void onPageNotFoundException(HttpServletRequest request, HttpServletResponse response, PageNotFoundException e) {
  }

  @Override
  public void onAccessDeniedException(HttpServletRequest request, HttpServletResponse response, AccessDeniedException e) {
  }

  @Override
  public void onSmvcRuntimeException(HttpServletRequest request, HttpServletResponse response, SmvcRuntimeException e) {
  }

  @Override
  public void onUncontrolledException(HttpServletRequest request, HttpServletResponse response, Exception e) {
    mailException(request, response, e);
  }

  @Override
  public void onTransactionCommitException(HttpServletRequest request, HttpServletResponse response, Exception e) {
    mailException(request, response, e);
  }

  private void mailException(HttpServletRequest request, HttpServletResponse response, Exception e) {
    try {
      String address = SystemUtils.getSettingValue("system.errorEmail"); 
      if (StringUtils.isNotBlank(address) && SystemUtils.isProductionEnvironment()) {
        String subject = "Critical failure on eDelphi";
        
        HttpSession session = request.getSession(false);
        
        StringWriter contentWriter = new StringWriter();
        contentWriter.append("Critical error occured on eDelfoi:" + '\n');
        contentWriter.append('\n');
        contentWriter.append("Time: " + new Date().toString() + '\n');
        contentWriter.append("Url: " + request.getRequestURL().toString() + '\n');
        contentWriter.append("Referer: " + request.getHeader("Referer") + '\n');
        contentWriter.append("Browser: " + request.getHeader("user-agent") + '\n');
        
        if (session != null) {
          Long loggedUserId = (Long) session.getAttribute("loggedUserId");
          contentWriter.append("Session id: " + session.getId() + '\n');
          contentWriter.append("Logged user id: " + loggedUserId + '\n');
        }
        contentWriter.append('\n');
        contentWriter.append("Stack trace: " + '\n');
        contentWriter.append('\n');
        
        e.printStackTrace(new PrintWriter(contentWriter));
        
        MailUtils.sendMail(request.getLocale(), address, subject, contentWriter.toString());
      }
    } catch (Exception ex) {
      // There is very little we can do anymore so we just eat away this exception
    } 
  }
}
