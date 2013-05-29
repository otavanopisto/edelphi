package fi.internetix.edelphi.taglib;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTagSupport;

import org.apache.taglibs.standard.tag.common.core.ParamParent;
import org.apache.taglibs.standard.tag.common.core.ParamSupport.ParamManager;

public class IncludeTag extends BodyTagSupport implements ParamParent {

  private static final long serialVersionUID = 1002047266043979433L;

  public IncludeTag() {
    super();
    init();
  }

  @Override
  public int doStartTag() throws JspException {
    paramManager = new ParamManager();
    return EVAL_BODY_INCLUDE;
  }

  @Override
  public int doEndTag() throws JspException {
    String includeUrl = paramManager.aggregateParams(page);
    try {
      pageContext.include(includeUrl);
    } catch (ServletException e) {
      throw new javax.servlet.jsp.JspTagException(e);
    } catch (IOException e) {
      throw new javax.servlet.jsp.JspTagException(e);
    }

    return EVAL_BODY_INCLUDE;
  }

  @Override
  public void addParameter(String name, String value) {
    paramManager.addParameter(name, value);
  }

  @Override
  public void release() {
    super.release();
    init();
  }

  private void init() {
    page = null;
    paramManager = null;
  }

  public void setPage(String page) {
    this.page = page;
  }

  public String getPage() {
    return page;
  }

  private String page;
  private ParamManager paramManager;
}
