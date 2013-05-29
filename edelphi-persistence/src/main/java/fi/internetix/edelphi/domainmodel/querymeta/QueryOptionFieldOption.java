package fi.internetix.edelphi.domainmodel.querymeta;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.TableGenerator;
import javax.validation.constraints.NotNull;

import fi.internetix.edelphi.domainmodel.base.ArchivableEntity;

@Entity
public class QueryOptionFieldOption implements ArchivableEntity {

  /**
   * Returns internal unique id
   * 
   * @return Internal unique id
   */
  public Long getId() {
    return id;
  }
  
  public void setOptionField(QueryOptionField optionField) {
    this.optionField = optionField;
  }

  public QueryOptionField getOptionField() {
    return optionField;
  }

  public void setText(String text) {
    this.text = text;
  }

  public String getText() {
    return text;
  }

  public void setValue(String value) {
    this.value = value;
  }

  public String getValue() {
    return value;
  }

  public Boolean getArchived() {
    return archived;
  }

  public void setArchived(Boolean archived) {
    this.archived = archived;
  }

  @Id 
  @GeneratedValue(strategy=GenerationType.TABLE, generator="QueryOptionFieldOption")  
  @TableGenerator(name="QueryOptionFieldOption", initialValue=1, allocationSize=100, table = "hibernate_sequences", pkColumnName = "sequence_name", valueColumnName = "sequence_next_hi_value")
  private Long id;
  
  @ManyToOne
  private QueryOptionField optionField;
  
  private String text;
  
  private String value;
  
  @NotNull
  @Column(nullable = false)
  private Boolean archived = Boolean.FALSE;
}
