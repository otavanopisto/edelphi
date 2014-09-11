package fi.internetix.edelphi.domainmodel.querydata;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.PrimaryKeyJoinColumn;

@Entity
@PrimaryKeyJoinColumn(name="id")
public class QueryQuestionTextAnswer extends QueryQuestionAnswer {

  public void setData(String data) {
    this.data = data;
  }

  public String getData() {
    return data;
  }

  @Column (length=1073741824)
  private String data;
}
