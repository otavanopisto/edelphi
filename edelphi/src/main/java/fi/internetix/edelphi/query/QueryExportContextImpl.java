package fi.internetix.edelphi.query;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import fi.internetix.edelphi.domainmodel.panels.PanelStamp;
import fi.internetix.edelphi.domainmodel.querydata.QueryReply;
import fi.internetix.edelphi.domainmodel.querylayout.QueryPage;

public class QueryExportContextImpl implements QueryExportContext {

  public QueryExportContextImpl(Locale locale, QueryPage queryPage, PanelStamp panelStamp, List<String> column, Map<QueryReply, Map<Integer, Object>> rows) {
    this.locale = locale;
    this.queryPage = queryPage;
    this.panelStamp = panelStamp;
    this.rows = rows;
    this.columns = column;
  }
  
  public QueryPage getQueryPage() {
    return queryPage;
  }

  public PanelStamp getStamp() {
    return panelStamp;
  }
  
  public Locale getLocale() {
    return locale;
  }
  
  public int addColumn(String columnName) {
    int index = columns.size();
    columns.add(columnName);
    return index;
  }
  
  public void addCellValue(QueryReply queryReply, int columnIndex, Object value) {
    Map<Integer, Object> columnValues = rows.get(queryReply);
    if (columnValues == null) {
      columnValues = new HashMap<Integer, Object>();
      rows.put(queryReply, columnValues);
    }
    
    columnValues.put(columnIndex, value);
  }
  
  public List<String> getColumns() {
    return columns;
  }
  
  public Map<QueryReply, Map<Integer, Object>> getRows() {
    return rows;
  }
  
  public List<QueryReply> getQueryReplies() {
    return queryReplies;
  }

  public void setQueryReplies(List<QueryReply> queryReplies) {
    this.queryReplies = queryReplies;
  }

  private Map<QueryReply, Map<Integer, Object>> rows;
  private List<String> columns;

  private QueryPage queryPage;
  private List<QueryReply> queryReplies;
  private PanelStamp panelStamp;
  private Locale locale;
}