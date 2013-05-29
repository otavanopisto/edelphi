package fi.internetix.edelphi.utils;

import java.util.Date;

import fi.internetix.edelphi.domainmodel.resources.ResourceType;

public class MaterialBean {

  public MaterialBean(Long id, ResourceType type, String name, String fullPath, Boolean visible, Date created, Date lastModified, Integer indexNumber) {
    this.id = id;
    this.type = type;
    this.fullPath = fullPath;
    this.name = name;
    this.created = created;
    this.lastModified = lastModified;
    this.visible = visible;
    this.indexNumber = indexNumber;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }
  
  public String getFullPath() {
    return fullPath;
  }

  public Date getCreated() {
    return created;
  }

  public void setCreated(Date created) {
    this.created = created;
  }
  
  public Date getLastModified() {
    return lastModified;
  }
  
  public void setLastModified(Date lastModified) {
    this.lastModified = lastModified;
  }
  
  public ResourceType getType() {
    return type;
  }
  
  public Boolean getVisible() {
    return visible;
  }
  
  public void setVisible(Boolean visible) {
    this.visible = visible;
  }

  public Integer getIndexNumber() {
    return indexNumber;
  }

  private Long id;
  private String name;
  private String fullPath;
  private Date created;
  private Date lastModified;
  private ResourceType type;
  private Boolean visible;
  private final Integer indexNumber;
}
