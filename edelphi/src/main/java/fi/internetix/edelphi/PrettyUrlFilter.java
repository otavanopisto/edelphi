package fi.internetix.edelphi;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

public class PrettyUrlFilter implements Filter {

  public void init(FilterConfig arg0) throws ServletException {
  }

  public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException {
    String ctxPath = ((HttpServletRequest) request).getContextPath();
    String uri = ((HttpServletRequest) request).getRequestURI();
    if (ctxPath.length() > 1) {
      uri = uri.substring(ctxPath.length());
    }
    if (uri.startsWith("/_")) {
      filterChain.doFilter(request, response);
    }
    else {
      request.getRequestDispatcher("/_app" + uri).forward(request, response);
    }
  }

  public void destroy() {
  }

}
