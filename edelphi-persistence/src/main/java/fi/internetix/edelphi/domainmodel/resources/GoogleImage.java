package fi.internetix.edelphi.domainmodel.resources;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;

@Entity
@PrimaryKeyJoinColumn(name="id")
public class GoogleImage extends Image {
  
  public GoogleImage() {
    setType(ResourceType.GOOGLE_IMAGE);
  }

  public String getResourceId() {
    return resourceId;
  }
  
  public void setResourceId(String resourceId) {
    this.resourceId = resourceId;
  }
  
  public Date getLastSynchronized() {
		return lastSynchronized;
	}
  
  public void setLastSynchronized(Date lastSynchronized) {
		this.lastSynchronized = lastSynchronized;
	}
  
  @Column (nullable = false)
  @NotNull
  @NotEmpty
  private String resourceId;

  @Column (nullable = false)
  @NotNull
  private Date lastSynchronized;
}