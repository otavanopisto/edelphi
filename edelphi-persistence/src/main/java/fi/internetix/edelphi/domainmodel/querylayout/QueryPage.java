package fi.internetix.edelphi.domainmodel.querylayout;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.TableGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;

import fi.internetix.edelphi.domainmodel.base.ArchivableEntity;
import fi.internetix.edelphi.domainmodel.base.ModificationTrackedEntity;
import fi.internetix.edelphi.domainmodel.users.User;

@Entity
public class QueryPage implements ArchivableEntity, ModificationTrackedEntity{
  
  public Long getId() {
    return id;
  }
  
  public String getTitle() {
    return title;
  }
  
  public void setTitle(String title) {
    this.title = title;
  }
  
  public QuerySection getQuerySection() {
    return querySection;
  }
  
  public void setQuerySection(QuerySection querySection) {
    this.querySection = querySection;
  }
  
  public QueryPageType getPageType() {
    return pageType;
  }
  
  public void setPageType(QueryPageType pageType) {
    this.pageType = pageType;
  }

  public Integer getPageNumber() {
    return pageNumber;
  }
  
  public void setPageNumber(Integer pageNumber) {
    this.pageNumber = pageNumber;
  }
  
  public void setCreated(Date created) {
    this.created = created;
  }

  public Date getCreated() {
    return created;
  }

  public void setCreator(User creator) {
    this.creator = creator;
  }

  public User getCreator() {
    return creator;
  }

  public void setLastModified(Date lastModified) {
    this.lastModified = lastModified;
  }

  public Date getLastModified() {
    return lastModified;
  }

  public void setLastModifier(User lastModifier) {
    this.lastModifier = lastModifier;
  }

  public User getLastModifier() {
    return lastModifier;
  }
  
  public Boolean getVisible() {
    return visible;
  }
  
  public void setVisible(Boolean visible) {
    this.visible = visible;
  }

  public void setArchived(Boolean archived) {
    this.archived = archived;
  }

  public Boolean getArchived() {
    return archived;
  }

  @Id
  @GeneratedValue(strategy = GenerationType.TABLE, generator = "QueryPage")
  @TableGenerator(name = "QueryPage", allocationSize = 1, table = "hibernate_sequences", pkColumnName = "sequence_name", valueColumnName = "sequence_next_hi_value")
  private Long id;
  
  @NotNull
  @NotEmpty
  @Column (nullable=false)
  private String title;
  
  @ManyToOne
  private QuerySection querySection;

  @Enumerated (EnumType.STRING)
  private QueryPageType pageType;
  
  @NotNull
  @Column(nullable = false)
  private Integer pageNumber;

  @NotNull
  @Column (nullable=false)
  private Boolean visible;
  
  @NotNull
  @Column(nullable = false)
  private Boolean archived = Boolean.FALSE;
  
  @ManyToOne 
  private User creator;
  
  @NotNull
  @Column (updatable=false, nullable=false)
  @Temporal (value=TemporalType.TIMESTAMP)
  private Date created;
  
  @ManyToOne  
  private User lastModifier;
  
  @NotNull
  @Column (nullable=false)
  @Temporal (value=TemporalType.TIMESTAMP)
  private Date lastModified;
}
