package fi.internetix.edelphi.domainmodel.resources;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.PrimaryKeyJoinColumn;

@Entity
@PrimaryKeyJoinColumn(name="id")
public class LocalImage extends Image {

  public LocalImage() {
    setType(ResourceType.LOCAL_IMAGE);
  }
  
  public void setData(byte[] data) {
    this.data = data;
  }

  public byte[] getData() {
    return data;
  }

  public void setContentType(String contentType) {
    this.contentType = contentType;
  }

  public String getContentType() {
    return contentType;
  }

  private String contentType;
  
  @Column (length=1073741824)
  private byte[] data;
}
