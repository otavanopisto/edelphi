package fi.internetix.edelphi.pages.panel.admin.report.util;

import java.util.List;

public class ChartDataSeries {

  public ChartDataSeries(String caption, List<Double> data) {
    this.caption = caption;
    this.data = data;
  }

  public void setData(List<Double> data) {
    this.data = data;
  }

  public List<Double> getData() {
    return data;
  }

  public void setCaption(String caption) {
    this.caption = caption;
  }

  public String getCaption() {
    return caption;
  }

  private List<Double> data;
  
  private String caption;
}
