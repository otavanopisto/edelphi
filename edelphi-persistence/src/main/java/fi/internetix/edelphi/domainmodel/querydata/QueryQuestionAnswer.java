package fi.internetix.edelphi.domainmodel.querydata;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.ManyToOne;
import javax.persistence.TableGenerator;

import fi.internetix.edelphi.domainmodel.querymeta.QueryField;

@Entity
@Inheritance(strategy=InheritanceType.JOINED)
public class QueryQuestionAnswer {

  /**
   * Returns internal unique id
   * 
   * @return Internal unique id
   */
  public Long getId() {
    return id;
  }

  public void setQueryReply(QueryReply queryReply) {
    this.queryReply = queryReply;
  }

  public QueryReply getQueryReply() {
    return queryReply;
  }

  public void setQueryField(QueryField queryField) {
    this.queryField = queryField;
  }

  public QueryField getQueryField() {
    return queryField;
  }

  @Id 
  @GeneratedValue(strategy=GenerationType.TABLE, generator="QueryQuestionAnswer")  
  @TableGenerator(name="QueryQuestionAnswer", initialValue=1, allocationSize=100, table = "hibernate_sequences", pkColumnName = "sequence_name", valueColumnName = "sequence_next_hi_value")
  private Long id;
  
  @ManyToOne
  private QueryReply queryReply;
  
  @ManyToOne
  private QueryField queryField;
}
