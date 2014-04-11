package fi.internetix.edelphi.pages.panel.admin.report.util;

import java.io.Serializable;
import java.util.Map;

import org.apache.commons.lang.math.NumberUtils;
import org.codehaus.jackson.annotate.JsonIgnore;

public class ChartContext implements Serializable {

  private static final long serialVersionUID = 5714256034959299760L;
  
  public ChartContext() {
  }

  public ChartContext(ReportContext reportContext, Map<String, String> parameters) {
    this.reportContext = reportContext;
    this.parameters = parameters;
  }
  
  public ReportContext getReportContext() {
    return reportContext;
  }
  
  public void setReportContext(ReportContext reportContext) {
    this.reportContext = reportContext;
  }
  
  @JsonIgnore
  public String getParameter(String key) {
    return parameters.get(key);
  }

  @JsonIgnore
  public Long getLong(String key) {
    String value = getParameter(key);
    return NumberUtils.isNumber(value) ? NumberUtils.toLong(value) : null;  
  }

  public Map<String, String> getParameters() {
    return parameters;
  }

  public void setParameters(Map<String, String> parameters) {
    this.parameters = parameters;
  }

  public void addParameter(String name, String value) {
    parameters.put(name, value);
  }

  private ReportContext reportContext;
  private Map<String, String> parameters;

}
