package fi.internetix.edelphi.pages.panel.admin.report.util;

import fi.internetix.edelphi.domainmodel.querylayout.QueryPage;

public class QueryReportPageData {

  public QueryReportPageData(QueryPage queryPage, String jspFile, QueryFieldDataStatistics statistics) {
    this.queryPage = queryPage;
    this.jspFile = jspFile;
    this.statistics = statistics;
  }
  
  public String getJspFile() {
    return jspFile;
  }

  public QueryPage getQueryPage() {
    return queryPage;
  }

  public QueryFieldDataStatistics getStatistics() {
    return statistics;
  }
  
  private final QueryFieldDataStatistics statistics;
  private final String jspFile;
  private final QueryPage queryPage;

}
