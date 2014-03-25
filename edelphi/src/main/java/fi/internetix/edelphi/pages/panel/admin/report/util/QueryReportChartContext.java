package fi.internetix.edelphi.pages.panel.admin.report.util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;

import fi.internetix.edelphi.dao.panels.PanelStampDAO;
import fi.internetix.edelphi.domainmodel.panels.PanelStamp;
import fi.internetix.edelphi.domainmodel.querydata.QueryReply;
import fi.internetix.smvc.controllers.RequestContext;

public class QueryReportChartContext implements Serializable {

  private static final long serialVersionUID = 8128813216252853554L;

  public QueryReportChartContext(Locale locale, PanelStamp panelStamp) {
    this(locale, null, panelStamp);
    this.panelStampId = panelStamp.getId();
  }

  public QueryReportChartContext(Locale locale, Map<String, String> parameters, PanelStamp panelStamp) {
    this.locale = locale;
    if (parameters != null)
      this.parameters.putAll(parameters);
    this.panelStampId = panelStamp.getId();
  }
  
  public PanelStamp getStamp() {
    // TODO Performance?
    PanelStampDAO panelStampDAO = new PanelStampDAO();
    return panelStampDAO.findById(panelStampId);
  }
  
  public Map<String, String> getParameters() {
    return parameters;
  }
  
  public Long getLong(String paramName) {
    String value = getString(paramName);
    return NumberUtils.isNumber(value) ? NumberUtils.toLong(value) : null;  
  }

  public String getString(String paramName) {
    String value = parameters.get(paramName);
    return StringUtils.isBlank(value) ? null : value;
  }
  
  public void addParameter(String name, String value) {
    parameters.put(name, value);
  }
  
  public void addFilter(QueryReplyFilter filter) {
    filters.put(filter.getType().toString(), filter.getValue());
  }
  
  public Locale getLocale() {
    return locale;
  }

  public Map<String, String> getFilters() {
    return filters;
  }

  public Long getPanelStampId() {
    return panelStampId;
  }

  public void setPanelStampId(Long panelStampId) {
    this.panelStampId = panelStampId;
  }

  private Locale locale;
  private Long panelStampId;
  private Map<String, String> filters = new HashMap<String, String>();
  private Map<String, String> parameters = new HashMap<String, String>();

}