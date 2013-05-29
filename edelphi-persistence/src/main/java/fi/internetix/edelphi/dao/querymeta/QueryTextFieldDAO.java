package fi.internetix.edelphi.dao.querymeta;

import fi.internetix.edelphi.dao.GenericDAO;
import fi.internetix.edelphi.domainmodel.querylayout.QueryPage;
import fi.internetix.edelphi.domainmodel.querymeta.QueryTextField;

public class QueryTextFieldDAO extends GenericDAO<QueryTextField> {

  public QueryTextField create(QueryPage queryPage, String name, Boolean mandatory, String caption) {
    QueryTextField queryTextField = new QueryTextField();
    queryTextField.setCaption(caption);
    queryTextField.setName(name);
    queryTextField.setMandatory(mandatory);
    queryTextField.setQueryPage(queryPage);
    queryTextField.setArchived(Boolean.FALSE);
    getEntityManager().persist(queryTextField);
    return queryTextField;
  }

  public QueryTextField update(QueryTextField queryTextField, QueryPage queryPage, String name, Boolean mandatory, String caption) {
    queryTextField.setCaption(caption);
    queryTextField.setName(name);
    queryTextField.setMandatory(mandatory);
    queryTextField.setQueryPage(queryPage);
    getEntityManager().persist(queryTextField);
    return queryTextField;
  }
    
}
