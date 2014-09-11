package fi.internetix.edelphi.pages.panel.admin.report.util;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import fi.internetix.edelphi.dao.panels.PanelExpertiseGroupUserDAO;
import fi.internetix.edelphi.dao.panels.PanelUserExpertiseGroupDAO;
import fi.internetix.edelphi.domainmodel.panels.PanelExpertiseGroupUser;
import fi.internetix.edelphi.domainmodel.panels.PanelUserExpertiseGroup;
import fi.internetix.edelphi.domainmodel.querydata.QueryReply;

public class QueryExpertiseReplyFilter extends QueryReplyFilter {

  public QueryExpertiseReplyFilter() {
  }
  
  @Override
  public List<QueryReply> filterList(List<QueryReply> list) {
    PanelUserExpertiseGroupDAO expertiseGroupDAO = new PanelUserExpertiseGroupDAO();
    PanelExpertiseGroupUserDAO expertiseGroupUserDAO = new PanelExpertiseGroupUserDAO();

    StringTokenizer tokenizer = new StringTokenizer(getValue(), ",");

    List<QueryReply> result = new ArrayList<QueryReply>();
    List<Long> allowedUserIds = new ArrayList<Long>();

    // TODO: Initialization section, these ids could be cached
    
    while (tokenizer.hasMoreElements()) {
      Long id = Long.valueOf(tokenizer.nextToken());

      PanelUserExpertiseGroup expertiseGroup = expertiseGroupDAO.findById(id);
      List<PanelExpertiseGroupUser> allowedUsers = expertiseGroupUserDAO.listByGroupAndArchived(expertiseGroup, Boolean.FALSE);

      for (PanelExpertiseGroupUser user : allowedUsers) {
        allowedUserIds.add(user.getPanelUser().getUser().getId());
      }
    }
    
    for (QueryReply reply : list) {
      // Filters all anonymous answers off
      if ((reply.getUser() != null) && (allowedUserIds.contains(reply.getUser().getId())))
        result.add(reply);
    }
    
    return result;
  }

  @Override
  public QueryReplyFilterType getType() {
    return QueryReplyFilterType.EXPERTISE;
  }
  
}