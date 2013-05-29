package fi.internetix.edelphi.pages.panel.admin.report.util;

import org.eclipse.birt.chart.model.Chart;

import fi.internetix.edelphi.domainmodel.querylayout.QueryPage;
import fi.internetix.edelphi.domainmodel.querylayout.QueryPageType;
import fi.internetix.smvc.controllers.RequestContext;

public abstract class QueryReportPageController {

  public QueryReportPageController(QueryPageType queryPageType) {
    this.queryPageType = queryPageType;
  }

  public abstract QueryReportPageData loadPageData(RequestContext requestContext, QueryReportChartContext chartContext, QueryPage queryPage);
  
  public Chart constructChart(QueryReportChartContext chartContext, QueryPage queryPage) {
    return null;
  }
  
  public QueryPageType getQueryPageType() {
    return queryPageType;
  }
  
  private final QueryPageType queryPageType;
}
