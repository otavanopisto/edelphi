package fi.internetix.edelphi;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

public class EncodingFilter implements Filter {

  public void init(FilterConfig fc) throws ServletException {
    this.filterConfig = fc;
    this.encoding = filterConfig.getInitParameter("encoding");
  }

  public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws IOException, ServletException {
    req.setCharacterEncoding(encoding);
    resp.setCharacterEncoding(encoding);
    chain.doFilter(req, resp);
  }

  public void destroy() {
  }

  private String encoding;
  private FilterConfig filterConfig;

}