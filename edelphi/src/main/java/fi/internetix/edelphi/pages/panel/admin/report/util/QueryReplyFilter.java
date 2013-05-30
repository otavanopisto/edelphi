package fi.internetix.edelphi.pages.panel.admin.report.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fi.internetix.edelphi.EdelfoiStatusCode;
import fi.internetix.edelphi.domainmodel.querydata.QueryReply;
import fi.internetix.smvc.SmvcRuntimeException;

public abstract class QueryReplyFilter {

  public QueryReplyFilter() {
  }

  public abstract List<QueryReply> filterList(List<QueryReply> list);
  
  public abstract QueryReplyFilterType getType();
  
  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }
  
  private String value;
  
  public static QueryReplyFilter createFilter(String name, String value) {
    return createFilter(QueryReplyFilterType.valueOf(name), value);
  }
  
  public static QueryReplyFilter createFilter(QueryReplyFilterType type, String value) {
    try {
      Class<? extends QueryReplyFilter> cls = filters.get(type);
      QueryReplyFilter filter = cls.newInstance();
      filter.setValue(value);
      return filter;
    } catch (Exception e) {
      throw new SmvcRuntimeException(EdelfoiStatusCode.UNDEFINED, e.getLocalizedMessage(), e);
    }
  }

  private static Map<QueryReplyFilterType, Class<? extends QueryReplyFilter>> filters;
  
  static {
    filters = new HashMap<QueryReplyFilterType, Class<? extends QueryReplyFilter>>();
    
    filters.put(QueryReplyFilterType.EXPERTISE, QueryExpertiseReplyFilter.class);
    filters.put(QueryReplyFilterType.FORMFIELD, QueryFormFieldReplyFilter.class);
    filters.put(QueryReplyFilterType.USER_GROUPS, UserGroupsReplyFilter.class);
  }
}
