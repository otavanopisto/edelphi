package fi.internetix.edelphi.domainmodel.querydata;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.PrimaryKeyJoinColumn;

import fi.internetix.edelphi.domainmodel.querymeta.QueryOptionFieldOption;

@Entity
@PrimaryKeyJoinColumn(name="id")
public class QueryQuestionOptionAnswer extends QueryQuestionAnswer {

  public void setOption(QueryOptionFieldOption option) {
    this.option = option;
  }

  public QueryOptionFieldOption getOption() {
    return option;
  }
  
  @ManyToOne
  private QueryOptionFieldOption option;
}
