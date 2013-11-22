package fi.internetix.edelphi.domainmodel.querymeta;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.ManyToOne;
import javax.persistence.TableGenerator;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;

import fi.internetix.edelphi.domainmodel.base.ArchivableEntity;
import fi.internetix.edelphi.domainmodel.querylayout.QueryPage;

@Entity
@Cacheable
@Inheritance(strategy=InheritanceType.JOINED)
public class QueryField implements ArchivableEntity {

  /**
   * Returns internal unique id
   * 
   * @return Internal unique id
   */
  public Long getId() {
    return id;
  }
  
  protected void setType(QueryFieldType type) {
    this.type = type;
  }

  public QueryFieldType getType() {
    return type;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }

  public void setCaption(String caption) {
    this.caption = caption;
  }

  public String getCaption() {
    return caption;
  }

  public void setMandatory(Boolean mandatory) {
    this.mandatory = mandatory;
  }

  public Boolean getMandatory() {
    return mandatory;
  }
  
  public QueryPage getQueryPage() {
    return queryPage;
  }
  
  public void setQueryPage(QueryPage queryPage) {
    this.queryPage = queryPage;
  }

  public Boolean getArchived() {
    return archived;
  }

  public void setArchived(Boolean archived) {
    this.archived = archived;
  }

  @Id 
  @GeneratedValue(strategy=GenerationType.TABLE, generator="QueryField")  
  @TableGenerator(name="QueryField", initialValue=1, allocationSize=100, table = "hibernate_sequences", pkColumnName = "sequence_name", valueColumnName = "sequence_next_hi_value")
  private Long id;

  @NotNull
  @Column (nullable = false)
  @Enumerated (EnumType.STRING)
  private QueryFieldType type;
  
  @ManyToOne
  private QueryPage queryPage;

  @NotNull
  @Column (nullable = false)
  @NotEmpty
  private String name;
  
  private String caption;
  
  @NotNull
  @Column (nullable = false)
  private Boolean mandatory = Boolean.FALSE;
  
  // TODO: Who created

  @NotNull
  @Column(nullable = false)
  private Boolean archived = Boolean.FALSE;
}
