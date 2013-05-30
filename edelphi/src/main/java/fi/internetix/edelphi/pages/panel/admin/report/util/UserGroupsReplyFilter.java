package fi.internetix.edelphi.pages.panel.admin.report.util;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;

import fi.internetix.edelphi.dao.panels.PanelUserGroupDAO;
import fi.internetix.edelphi.domainmodel.panels.PanelUserGroup;
import fi.internetix.edelphi.domainmodel.querydata.QueryReply;
import fi.internetix.edelphi.domainmodel.users.User;

public class UserGroupsReplyFilter extends QueryReplyFilter {

  public UserGroupsReplyFilter() {
  }
  
  @Override
  public List<QueryReply> filterList(List<QueryReply> list) {
  	/**
    QueryReplyDAO queryReplyDAO = new QueryReplyDAO();
    
    List<QueryReply> result = new ArrayList<QueryReply>();
    List<Long> excludeIds = new ArrayList<Long>();

    String panelUserGroupIdStr = getValue();
    Long paneUserGroupId = NumberUtils.createLong(panelUserGroupIdStr);
    if (paneUserGroupId != null) {
      PanelUserGroup panelUserGroup = panelUserGroupDAO.findById(paneUserGroupId);
      List<User> users = panelUserGroup.getUsers();
      for (User user : users) {
      	List<QueryReply> userReplies = queryReplyDAO.listByUser(user);
      	for (QueryReply userReply : userReplies) {
      		excludeIds.add(userReply.getId());
      	}
      }
    }
    
    for (QueryReply queryReply : list) {
    	if (!excludeIds.contains(queryReply.getId())) {
    		result.add(queryReply);
    	}
    }
    **/
    return getReplies(list);
  }
  
  private List<QueryReply> getReplies(List<QueryReply> replies) {
  	List<QueryReply> result = new ArrayList<QueryReply>();
  	Set<Long> groupUserIds = getGroupUserIds();
  	
  	for (QueryReply reply : replies) {
  		if (groupUserIds.contains(reply.getUser().getId())) {
  			result.add(reply);
  		}
  	}
  	
  	return result;
  }
  
  private Set<Long> getGroupUserIds() {
  	Set<Long> result = new HashSet<Long>();
  	
  	for (PanelUserGroup group : getGroups()) {
  		for (User user : group.getUsers()) {
  			if (!result.contains(user.getId()))
  			  result.add(user.getId());
  		}
  	}
  	
  	return result;
  }
  
  private List<PanelUserGroup> getGroups() {
  	List<PanelUserGroup> result = new ArrayList<PanelUserGroup>();
  	
    PanelUserGroupDAO panelUserGroupDAO = new PanelUserGroupDAO();
    String value = getValue();
    if (StringUtils.isNotBlank(value)) {
    	String[] ids = value.split(",");
    	for (String id : ids) {
    		result.add( panelUserGroupDAO.findById(NumberUtils.createLong(id)) );
    	}
    }
  	
  	return result;
  }

  @Override
  public QueryReplyFilterType getType() {
    return QueryReplyFilterType.USER_GROUPS;
  }
  
}