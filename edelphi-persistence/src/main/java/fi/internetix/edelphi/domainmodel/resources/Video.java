package fi.internetix.edelphi.domainmodel.resources;

import javax.persistence.Entity;
import javax.persistence.PrimaryKeyJoinColumn;

@Entity
@PrimaryKeyJoinColumn(name="id")
public class Video extends Resource {

  public Video() {
    setType(ResourceType.VIDEO);
  }
}
