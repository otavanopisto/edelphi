package fi.internetix.edelphi.domainmodel.querydata;

import javax.persistence.Entity;
import javax.persistence.PrimaryKeyJoinColumn;

@Entity
@PrimaryKeyJoinColumn(name="id")
public class QueryQuestionNumericAnswer extends QueryQuestionAnswer {

  public void setData(Double data) {
    this.data = data;
  }

  public Double getData() {
    return data;
  }

  private Double data;
}
