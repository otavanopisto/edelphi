package fi.internetix.edelphi.domainmodel.resources;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.PrimaryKeyJoinColumn;

@Entity
@PrimaryKeyJoinColumn(name="id")
public class LinkedImage extends Image {

  public LinkedImage() {
    setType(ResourceType.LINKED_IMAGE);
  }
  
  public void setUrl(String url) {
    this.url = url;
  }

  public String getUrl() {
    return url;
  }

  @Column (length = 4096)
  private String url;
}
