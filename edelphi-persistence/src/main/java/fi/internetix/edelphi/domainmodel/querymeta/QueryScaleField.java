package fi.internetix.edelphi.domainmodel.querymeta;

import javax.persistence.Entity;
import javax.persistence.PrimaryKeyJoinColumn;

@Entity
@PrimaryKeyJoinColumn(name="id")
public class QueryScaleField extends QueryNumericField {

  public QueryScaleField() {
    setType(QueryFieldType.NUMERIC_SCALE);
  }
  
  public void setStep(Double step) {
    this.step = step;
  }

  public Double getStep() {
    return step;
  }
  
  private Double step;
}
