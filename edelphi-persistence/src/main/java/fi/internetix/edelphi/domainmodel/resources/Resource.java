package fi.internetix.edelphi.domainmodel.resources;

import java.util.Date;

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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;

import fi.internetix.edelphi.domainmodel.base.ArchivableEntity;
import fi.internetix.edelphi.domainmodel.base.ModificationTrackedEntity;
import fi.internetix.edelphi.domainmodel.users.User;

@Entity
@Inheritance(strategy=InheritanceType.JOINED)
@Cacheable
public class Resource implements ArchivableEntity, ModificationTrackedEntity {
  
  /**
   * Returns internal unique id
   * 
   * @return Internal unique id
   */
  public Long getId() {
    return id;
  }
  
  public void setParentFolder(Folder parentFolder) {
    this.parentFolder = parentFolder;
  }

  public Folder getParentFolder() {
    return parentFolder;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }
  
  public String getDescription() {
    return description;
  }
  
  public void setDescription(String description) {
    this.description = description;
  }

  public ResourceType getType() {
    return type;
  }
  
  protected void setType(ResourceType type) {
    this.type = type;
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

  public void setVisible(Boolean visible) {
    this.visible = visible;
  }

  public Boolean getVisible() {
    return visible;
  }

  public void setUrlName(String urlName) {
    this.urlName = urlName;
  }

  public String getUrlName() {
    return urlName;
  }
  
  @Transient
  public String getFullPath() {
    String result = "";
    
    Resource current = this;
    Folder folder = current.getParentFolder();
    while (folder != null) {
      result = "/" + current.getUrlName() + result;
      
      current = folder;
      folder = current.getParentFolder();
    }
    
    return result;
  }

  public Integer getIndexNumber() {
    return indexNumber;
  }

  public void setIndexNumber(Integer indexNumber) {
    this.indexNumber = indexNumber;
  }

  @Id 
  @GeneratedValue(strategy=GenerationType.TABLE, generator="Resource")  
  @TableGenerator(name="Resource", initialValue=1, allocationSize=100, table = "hibernate_sequences", pkColumnName = "sequence_name", valueColumnName = "sequence_next_hi_value")
  private Long id;
  
  @NotNull
  @Column (nullable = false)
  @NotEmpty
  private String name;

  @NotNull
  @Column (nullable = false)
  @NotEmpty
  private String urlName;
  
  @Column (length=1073741824)
  private String description;
  
  // TODO: url / type
  
  @ManyToOne
  private Folder parentFolder;
  
  @NotNull
  @Column (nullable = false)
  @Enumerated (EnumType.STRING)
  private ResourceType type;

  @NotNull
  @Column(nullable = false)
  private Boolean visible = Boolean.TRUE;

  @NotNull
  @Column(nullable = false)
  private Integer indexNumber;
  
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
