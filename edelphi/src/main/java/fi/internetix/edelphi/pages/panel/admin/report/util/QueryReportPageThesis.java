package fi.internetix.edelphi.pages.panel.admin.report.util;

public class QueryReportPageThesis {

  public QueryReportPageThesis(String text, String description) {
    this.text = text;
    this.description = description;
  }

  public String getText() {
    return text;
  }

  public String getDescription() {
    return description;
  }

  private final String text;
  private final String description;

}
