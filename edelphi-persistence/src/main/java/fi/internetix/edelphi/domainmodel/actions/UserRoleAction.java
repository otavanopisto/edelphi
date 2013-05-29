package fi.internetix.edelphi.domainmodel.actions;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.ManyToOne;
import javax.persistence.TableGenerator;

import fi.internetix.edelphi.domainmodel.users.UserRole;

@Entity
@Inheritance(strategy=InheritanceType.JOINED)
public abstract class UserRoleAction {

  /**
   * Returns internal unique id
   * 
   * @return Internal unique id
   */
  public Long getId() {
    return id;
  }

  public void setDelfoiAction(DelfoiAction delfoiAction) {
    this.delfoiAction = delfoiAction;
  }

  public DelfoiAction getDelfoiAction() {
    return delfoiAction;
  }

  public void setUserRole(UserRole userRole) {
    this.userRole = userRole;
  }

  public UserRole getUserRole() {
    return userRole;
  }

  @Id 
  @GeneratedValue(strategy=GenerationType.TABLE, generator="UserRoleAction")  
  @TableGenerator(name="UserRoleAction", initialValue=1, allocationSize=100, table = "hibernate_sequences", pkColumnName = "sequence_name", valueColumnName = "sequence_next_hi_value")
  private Long id;
  
  @ManyToOne
  private DelfoiAction delfoiAction;
  
  @ManyToOne
  private UserRole userRole;
}
