package fi.internetix.edelphi.pages.panel.admin.report.util;

import java.util.ArrayList;
import java.util.List;

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
    this.queryPageId = queryPageId;
  }
  
  public void addComment(QueryReportPageComment comment) {
    comments.add(comment);
  }
  
  public List<QueryReportPageComment> getComments() {
    return comments;
  }

  private String jspFile;
  
  private Long queryPageId;
  private String pageTitle;
  private String thesis;
  private String description;
  private List<QueryReportPageComment> comments = new ArrayList<QueryReportPageComment>();

}
