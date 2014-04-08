package fi.internetix.edelphi.pages.panel.admin.report.util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.codehaus.jackson.annotate.JsonIgnore;

public class ReportContext implements Serializable {

  private static final long serialVersionUID = 8128813216252853554L;
  
  public ReportContext() {
  }

  public ReportContext(String locale, Long panelStampId) {
    this(locale, null, panelStampId);
  }

  public ReportContext(String locale, Map<String, String> parameters, Long panelStampId) {
    this.locale = locale;
    if (parameters != null)
      this.parameters.putAll(parameters);
    this.panelStampId = panelStampId;
  }
  
  public Map<String, String> getParameters() {
    return parameters;
  }
  
  public void setParameters(Map<String, String> parameters) {
    this.parameters = parameters;
  }
  
  @JsonIgnore
  public Long getLong(String paramName) {
    String value = getString(paramName);
    return NumberUtils.isNumber(value) ? NumberUtils.toLong(value) : null;  
  }

  @JsonIgnore
  public String getString(String paramName) {
    String value = parameters.get(paramName);
    return StringUtils.isBlank(value) ? null : value;
  }
  
  public void addParameter(String name, String value) {
    parameters.put(name, value);
  }
  
  public void addFilter(String filterType, String filterValue) {
    List<String> values = filters.get(filterType);
    if (values == null) {
      values = new ArrayList<String>();
    }
    values.add(filterValue);
    filters.put(filterType, values);
  }
  
  public String getLocale() {
    return locale;
  }
  
  public void setLocale(String locale) {
    this.locale = locale;
  }

  public Map<String, List<String>> getFilters() {
    return filters;
  }
  
  public void setFilters(Map<String, List<String>> filters) {
    this.filters = filters;
  }

  public Long getPanelStampId() {
    return panelStampId;
  }
  
  public void setPanelStampId(Long panelStampId) {
    this.panelStampId = panelStampId;
  }

  private String locale;
  private Long panelStampId;
  private Map<String, List<String>> filters = new HashMap<String, List<String>>();
  private Map<String, String> parameters = new HashMap<String, String>();

}