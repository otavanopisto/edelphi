package fi.internetix.edelphi.domainmodel.querydata;

import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.PrimaryKeyJoinColumn;

import fi.internetix.edelphi.domainmodel.querymeta.QueryOptionFieldOption;

@Entity
@PrimaryKeyJoinColumn(name="id")
public class QueryQuestionMultiOptionAnswer extends QueryQuestionAnswer {

  public Set<QueryOptionFieldOption> getOptions() {
    return options;
  }
  
  public void setOptions(Set<QueryOptionFieldOption> options) {
    this.options = options;
  }
  
  @ManyToMany
  @JoinTable (name="__QueryQuestionMultiOptionAnswers", joinColumns=@JoinColumn(name="answer_id"), inverseJoinColumns=@JoinColumn(name="option_id"))
  private Set<QueryOptionFieldOption> options;
}
