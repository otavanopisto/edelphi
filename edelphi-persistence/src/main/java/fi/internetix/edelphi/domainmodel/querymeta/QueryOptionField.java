package fi.internetix.edelphi.domainmodel.querymeta;

import javax.persistence.Entity;
import javax.persistence.PrimaryKeyJoinColumn;

@Entity
@PrimaryKeyJoinColumn(name="id")
public class QueryOptionField extends QueryField {

  public QueryOptionField() {
    setType(QueryFieldType.OPTIONFIELD);
  }
  
  // TODO: options or not?
}
