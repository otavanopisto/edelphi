package fi.internetix.edelphi.pages.panel.admin.report.util;

import java.util.ArrayList;
import java.util.List;

public class QueryReportPageGrouping extends QueryReportPage {

  public QueryReportPageGrouping(Long queryPageId, String title, String jspFile) {
    super(queryPageId, title, jspFile);
  }
  
  public void addGroupId(Long groupId) {
    groupIds.add(groupId);
  }
  
  public List<Long> getGroupIds() {
    return groupIds;
  }
  
  private List<Long> groupIds = new ArrayList<Long>();

}
