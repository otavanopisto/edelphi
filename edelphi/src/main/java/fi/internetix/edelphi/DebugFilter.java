package fi.internetix.edelphi;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import fi.internetix.edelphi.utils.SessionUtils;

public class DebugFilter implements Filter {

  public void init(FilterConfig arg0) throws ServletException {

  }

  public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException {
    if (request instanceof HttpServletRequest) {
      HttpSession session = ((HttpServletRequest) request).getSession();
      session.setAttribute("delfoiId", new Long(1));

//      if (session.getAttribute("loggedUserId") == null) {
//        session.setAttribute("loggedUserId", new Long(1));
//        session.setAttribute("loggedUserRoleId", new Long(1)); // 1 = Administrator in initial data
//        session.setAttribute("loggedUserFullName", "John Doe");
//      }
      SessionUtils.setCurrentTheme(session, "default");
    }

    filterChain.doFilter(request, response);
  }

  public void destroy() {
  }
}
