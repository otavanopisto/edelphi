package fi.internetix.edelphi.dao.querymeta;

import fi.internetix.edelphi.dao.GenericDAO;
import fi.internetix.edelphi.domainmodel.querylayout.QueryPage;
import fi.internetix.edelphi.domainmodel.querymeta.QueryOptionField;

public class QueryOptionFieldDAO extends GenericDAO<QueryOptionField> {

  public QueryOptionField create(QueryPage queryPage, String name, Boolean mandatory, String caption) {
    QueryOptionField queryOptionField = new QueryOptionField();
    queryOptionField.setCaption(caption);
    queryOptionField.setName(name);
    queryOptionField.setMandatory(mandatory);
    queryOptionField.setQueryPage(queryPage);
    queryOptionField.setArchived(Boolean.FALSE);
    getEntityManager().persist(queryOptionField);
    return queryOptionField;
  }

  public QueryOptionField update(QueryOptionField queryOptionField, QueryPage queryPage, String name, Boolean mandatory, String caption) {
    queryOptionField.setCaption(caption);
    queryOptionField.setName(name);
    queryOptionField.setMandatory(mandatory);
    queryOptionField.setQueryPage(queryPage);
    getEntityManager().persist(queryOptionField);
    return queryOptionField;
  }

}
