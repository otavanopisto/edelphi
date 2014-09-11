package fi.internetix.edelphi.domainmodel.querymeta;

import javax.persistence.Entity;
import javax.persistence.PrimaryKeyJoinColumn;

@Entity
@PrimaryKeyJoinColumn(name="id")
public class QueryTextField extends QueryField {

  public QueryTextField() {
    setType(QueryFieldType.TEXT);
  }
  
}
