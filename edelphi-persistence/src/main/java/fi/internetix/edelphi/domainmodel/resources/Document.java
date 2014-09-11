package fi.internetix.edelphi.domainmodel.resources;

import javax.persistence.Entity;
import javax.persistence.PrimaryKeyJoinColumn;

@Entity
@PrimaryKeyJoinColumn(name="id")
public class Document extends Resource {
  
}
