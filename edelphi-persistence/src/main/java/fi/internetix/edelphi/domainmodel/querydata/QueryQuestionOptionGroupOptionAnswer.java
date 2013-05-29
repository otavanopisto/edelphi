package fi.internetix.edelphi.domainmodel.querydata;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.PrimaryKeyJoinColumn;

import fi.internetix.edelphi.domainmodel.querymeta.QueryOptionFieldOptionGroup;

@Entity
@PrimaryKeyJoinColumn(name="id")
public class QueryQuestionOptionGroupOptionAnswer extends QueryQuestionOptionAnswer {

  public QueryOptionFieldOptionGroup getGroup() {
    return group;
  }

  public void setGroup(QueryOptionFieldOptionGroup group) {
    this.group = group;
  }

  @ManyToOne
  private QueryOptionFieldOptionGroup group;
}
