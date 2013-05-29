package fi.internetix.edelphi.pages.panel.admin.report.util;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;

import fi.internetix.edelphi.domainmodel.panels.PanelStamp;
import fi.internetix.edelphi.domainmodel.querydata.QueryReply;
import fi.internetix.smvc.controllers.RequestContext;

public class QueryReportChartContext {

  public static final String CHART_PARAMETER_PREFIX = "chart_";
  public static final String CHART_FILTER_PARAMETER_PREFIX = "filter:";

  public QueryReportChartContext(Locale locale, PanelStamp panelStamp) {
    this(locale, null, panelStamp);
    this.panelStamp = panelStamp;
  }

  public QueryReportChartContext(Locale locale, Map<String, String> parameters, PanelStamp panelStamp) {
    this.locale = locale;
    if (parameters != null)
      this.parameters.putAll(parameters);
    this.panelStamp = panelStamp;
  }
  
  public PanelStamp getStamp() {
    return panelStamp;
  }

  public Map<String, String> getParameters() {
    return parameters;
  }

  public void putParameters(Map<String, String> parameters) {
    this.parameters.putAll(parameters);
  }
  
  public Long getLong(String paramName) {
    String value = getString(paramName);
    return NumberUtils.isNumber(value) ? NumberUtils.toLong(value) : null;  
  }

  public String getString(String paramName) {
    String value = parameters.get(paramName);
    
    return StringUtils.isBlank(value) ? null : value;
  }
  
  public List<QueryReplyFilter> getReplyFilters() {
    return replyFilters;
  }

  public void addFilter(QueryReplyFilter filter) {
    this.replyFilters.add(filter);
  }

  public List<QueryReply> filterReplies(List<QueryReply> queryReplies) {
    List<QueryReply> result = queryReplies;
    
    for (QueryReplyFilter filter : replyFilters) {
      result = filter.filterList(result);
    }
    
    return result;
  }
  
  public void populateRequestParameters(RequestContext requestContext) {
    Enumeration<?> names = requestContext.getRequest().getParameterNames();
    
    while (names.hasMoreElements()) {
      String name = (String) names.nextElement();
      if (name.startsWith(CHART_PARAMETER_PREFIX)) {
        String value = requestContext.getRequest().getParameter(name);
        
        name = name.substring(CHART_PARAMETER_PREFIX.length());

        if (!name.startsWith(CHART_FILTER_PARAMETER_PREFIX))
          this.parameters.put(name, value);
        else {
          name = name.substring(CHART_FILTER_PARAMETER_PREFIX.length());

          QueryReplyFilter queryReplyFilter = QueryReplyFilter.createFilter(name, value);
          this.replyFilters.add(queryReplyFilter);
        }
      }
    }
  }
  
  public Locale getLocale() {
    return locale;
  }

  private Locale locale;
  private PanelStamp panelStamp;
  private final List<QueryReplyFilter> replyFilters = new ArrayList<QueryReplyFilter>();
  private final Map<String, String> parameters = new HashMap<String, String>();
}
