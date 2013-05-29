package fi.internetix.edelphi.utils;

public class QueryPageBean {

  public QueryPageBean(String title, Integer pageNumber, Integer uiPageNumber) {
    this.title = title;
    this.pageNumber = pageNumber;
    this.uiPageNumber = uiPageNumber;
  }

  public String getTitle() {
    return title;
  }

  public Integer getPageNumber() {
    return pageNumber;
  }

  public Integer getUiPageNumber() {
    return uiPageNumber;
  }

  private String title;
  private Integer pageNumber;
  private Integer uiPageNumber;

}
