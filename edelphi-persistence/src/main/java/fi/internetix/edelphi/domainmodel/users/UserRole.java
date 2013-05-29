package fi.internetix.edelphi.domainmodel.users;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.ManyToOne;
import javax.persistence.TableGenerator;

import fi.internetix.edelphi.domainmodel.base.LocalizedEntry;

@Entity
@Inheritance(strategy=InheritanceType.JOINED)
public abstract class UserRole {

  /**
   * Returns internal unique id
   * 
   * @return Internal unique id
   */
  public Long getId() {
    return id;
  }

  public LocalizedEntry getName() {
    return name;
  }
  
  public void setName(LocalizedEntry name) {
    this.name = name;
  }

  @Id 
  @GeneratedValue(strategy=GenerationType.TABLE, generator="UserRole")  
  @TableGenerator(name="UserRole", initialValue=1, allocationSize=100, table = "hibernate_sequences", pkColumnName = "sequence_name", valueColumnName = "sequence_next_hi_value")
  private Long id;
  
  @ManyToOne 
  private LocalizedEntry name;

}
