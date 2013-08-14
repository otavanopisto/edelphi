package fi.internetix.edelphi.domainmodel.querylayout;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.TableGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

import fi.internetix.edelphi.domainmodel.base.ArchivableEntity;
import fi.internetix.edelphi.domainmodel.base.LocalizedEntry;
import fi.internetix.edelphi.domainmodel.base.ModificationTrackedEntity;
import fi.internetix.edelphi.domainmodel.users.User;

@Entity
public class QueryPageTemplate implements ArchivableEntity, ModificationTrackedEntity{
  
  public Long getId() {
    return id;
  }
  
  public LocalizedEntry getName() {
    return name;
  }
  
  public void setName(LocalizedEntry name) {
    this.name = name;
  }
  
  public String getIconName() {
    return iconName;
  }
  
  public void setIconName(String iconName) {
    this.iconName = iconName;
  }
  
  public QueryPageType getPageType() {
    return pageType;
  }
  
  public void setPageType(QueryPageType pageType) {
    this.pageType = pageType;
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

  public void setArchived(Boolean archived) {
    this.archived = archived;
  }

  public Boolean getArchived() {
    return archived;
  }

  public LocalizedEntry getDescription() {
    return description;
  }

  public void setDescription(LocalizedEntry description) {
    this.description = description;
  }

  @Id
  @GeneratedValue(strategy = GenerationType.TABLE, generator = "QueryPageTemplate")
  @TableGenerator(name = "QueryPageTemplate", allocationSize = 1, table = "hibernate_sequences", pkColumnName = "sequence_name", valueColumnName = "sequence_next_hi_value")
  private Long id;

  @ManyToOne 
  private LocalizedEntry name;
  
  @ManyToOne 
  private LocalizedEntry description;
  
  @NotNull
  @Column(nullable = false)
  private String iconName;

  @NotNull
  @Column(nullable = false)
  private QueryPageType pageType;
  
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
