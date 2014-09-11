package fi.internetix.edelphi.domainmodel.querymeta;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.validation.constraints.NotNull;

@Entity
@PrimaryKeyJoinColumn(name="id")
public class QueryNumericField extends QueryField {

  public QueryNumericField() {
    setType(QueryFieldType.NUMERIC);
  }
  
  public void setMin(Double min) {
    this.min = min;
  }

  public Double getMin() {
    return min;
  }

  public void setMax(Double max) {
    this.max = max;
  }

  public Double getMax() {
    return max;
  }

  public void setPrecision(Double precision) {
    this.precision = precision;
  }

  public Double getPrecision() {
    return precision;
  }

  private Double min;
  
  private Double max;

  @NotNull
  @Column (name = "_precision", nullable = false)
  private Double precision = 1d;
}
