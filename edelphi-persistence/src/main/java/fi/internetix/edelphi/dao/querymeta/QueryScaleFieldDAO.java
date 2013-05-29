package fi.internetix.edelphi.dao.querymeta;

import fi.internetix.edelphi.dao.GenericDAO;
import fi.internetix.edelphi.domainmodel.querylayout.QueryPage;
import fi.internetix.edelphi.domainmodel.querymeta.QueryScaleField;

public class QueryScaleFieldDAO extends GenericDAO<QueryScaleField> {

  public QueryScaleField create(QueryPage queryPage, String name, Boolean mandatory, String caption, Double min, Double max, Double precision, Double step) {
    QueryScaleField queryScaleField = new QueryScaleField();
    queryScaleField.setCaption(caption);
    queryScaleField.setName(name);
    queryScaleField.setMandatory(mandatory);
    queryScaleField.setQueryPage(queryPage);
    queryScaleField.setMax(max);
    queryScaleField.setMin(min);
    queryScaleField.setPrecision(precision);
    queryScaleField.setStep(step);
    queryScaleField.setArchived(Boolean.FALSE);
    getEntityManager().persist(queryScaleField);
    return queryScaleField;
  }
  
  public QueryScaleField update(QueryScaleField queryScaleField, QueryPage queryPage, String name, Boolean mandatory, String caption, Double min, Double max, Double precision, Double step) {
    queryScaleField.setCaption(caption);
    queryScaleField.setName(name);
    queryScaleField.setMandatory(mandatory);
    queryScaleField.setQueryPage(queryPage);
    queryScaleField.setMax(max);
    queryScaleField.setMin(min);
    queryScaleField.setPrecision(precision);
    queryScaleField.setStep(step);
    getEntityManager().persist(queryScaleField);
    return queryScaleField;
  }
}
