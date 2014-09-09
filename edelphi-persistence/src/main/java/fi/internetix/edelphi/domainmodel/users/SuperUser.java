package fi.internetix.edelphi.domainmodel.users;

import javax.persistence.Entity;
import javax.persistence.PrimaryKeyJoinColumn;

@Entity
@PrimaryKeyJoinColumn(name="id")
public class SuperUser extends User {
  
}
