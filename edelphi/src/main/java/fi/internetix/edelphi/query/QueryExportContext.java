package fi.internetix.edelphi.query;

import java.util.List;
import java.util.Locale;

import fi.internetix.edelphi.domainmodel.panels.PanelStamp;
import fi.internetix.edelphi.domainmodel.querydata.QueryReply;
import fi.internetix.edelphi.domainmodel.querylayout.QueryPage;

public interface QueryExportContext {

  public Locale getLocale();
  
  public QueryPage getQueryPage();
  
  public List<QueryReply> getQueryReplies();
  
  public PanelStamp getStamp();

  public int addColumn(String columnName);

  public void addCellValue(QueryReply queryReply, int columnIndex, Object value);
}
