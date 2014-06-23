package fi.internetix.edelphi.pages.panel.admin.report.util;

public class QueryReportPage {

  public QueryReportPage(Long queryPageId, String title, String jspFile) {
    setPageTitle(title);
    setJspFile(jspFile);
    setQueryPageId(queryPageId);
  }
  
  public String getJspFile() {
    return jspFile;
  }
  public void setJspFile(String jspFile) {
    this.jspFile = jspFile;
  }

  public String getPageTitle() {
    return pageTitle;
  }

  public void setPageTitle(String pageTitle) {
    this.pageTitle = pageTitle;
  }

  public String getThesis() {
    return thesis;
  }

  public void setThesis(String thesis) {
    this.thesis = thesis;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public Long getQueryPageId() {
    return queryPageId;
  }

  public void setQueryPageId(Long queryPageId) {
    if (queryPageId == 0) {
      throw new RuntimeException("SIVULLE " + pageTitle + " TUUPATTIIN NOLLA?!?!?!");
    }
    this.queryPageId = queryPageId;
  }

  private String jspFile;
  
  private Long queryPageId;
  private String pageTitle;
  private String thesis;
  private String description;
  

}
