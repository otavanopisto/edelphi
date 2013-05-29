package fi.internetix.edelphi.domainmodel.querylayout;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.PrimaryKeyJoinColumn;

import org.hibernate.validator.constraints.NotEmpty;

@Entity
@PrimaryKeyJoinColumn(name="id")
public class QueryPageTemplateSimpleSetting extends QueryPageTemplateSetting {

  
  public String getValue() {
    return value;
  }
  
  public void setValue(String value) {
    this.value = value;
  }
  
  @NotEmpty
  @Column (length=1073741824)
  private String value;
}
