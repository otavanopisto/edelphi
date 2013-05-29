package fi.internetix.edelphi.dao.querylayout;

import java.util.Date;

import fi.internetix.edelphi.dao.GenericDAO;
import fi.internetix.edelphi.domainmodel.base.LocalizedEntry;
import fi.internetix.edelphi.domainmodel.querylayout.QueryPageTemplate;
import fi.internetix.edelphi.domainmodel.querylayout.QueryPageType;
import fi.internetix.edelphi.domainmodel.users.User;

public class QueryPageTemplateDAO extends GenericDAO<QueryPageTemplate> {

  public QueryPageTemplate create(QueryPageType pageType, User creator, LocalizedEntry name, String iconName) {
    Date now = new Date();

    QueryPageTemplate queryPageTemplate = new QueryPageTemplate();
    queryPageTemplate.setName(name);
    queryPageTemplate.setIconName(iconName);
    queryPageTemplate.setPageType(pageType);
    queryPageTemplate.setArchived(Boolean.FALSE);
    queryPageTemplate.setCreated(now);
    queryPageTemplate.setLastModified(now);
    queryPageTemplate.setCreator(creator);
    queryPageTemplate.setLastModifier(creator);

    getEntityManager().persist(queryPageTemplate);
    return queryPageTemplate;
  }

}
