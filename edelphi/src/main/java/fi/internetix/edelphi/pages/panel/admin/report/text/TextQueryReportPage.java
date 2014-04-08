  package fi.internetix.edelphi.pages.panel.admin.report.text;

import fi.internetix.edelphi.domainmodel.querylayout.QueryPage;
import fi.internetix.edelphi.domainmodel.querylayout.QueryPageType;
import fi.internetix.edelphi.pages.panel.admin.report.util.ReportContext;
import fi.internetix.edelphi.pages.panel.admin.report.util.QueryReportPageController;
import fi.internetix.edelphi.pages.panel.admin.report.util.QueryReportPageData;
import fi.internetix.edelphi.utils.QueryPageUtils;
import fi.internetix.edelphi.utils.QueryUtils;
import fi.internetix.smvc.controllers.RequestContext;

public class TextQueryReportPage extends QueryReportPageController {

  public TextQueryReportPage() {
    super(QueryPageType.TEXT);
  }

  @Override
  public QueryReportPageData loadPageData(RequestContext requestContext, ReportContext reportContext, QueryPage queryPage) {
    String text = QueryPageUtils.getSetting(queryPage, "text.content");
    
    QueryUtils.appendQueryPageComments(requestContext, queryPage);

    return new TextQueryReportPageData(queryPage, "/jsp/blocks/panel_admin_report/text.jsp", text);
  }
 
  public class TextQueryReportPageData extends QueryReportPageData {
    
    public TextQueryReportPageData(QueryPage queryPage, String jspFile, String text) {
      super(queryPage, jspFile, null);
      
      this.text = text;
    }
    
    public String getText() {
      return text;
    }
    
    private String text;
  }
}
