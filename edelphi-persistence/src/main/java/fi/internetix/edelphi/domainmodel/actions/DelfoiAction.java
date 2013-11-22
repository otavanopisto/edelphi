package fi.internetix.edelphi.domainmodel.actions;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.TableGenerator;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;

@Entity
@Cacheable
public class DelfoiAction {

  /**
   * Returns internal unique id
   * 
   * @return Internal unique id
   */
  public Long getId() {
    return id;
  }

  public void setActionName(String actionName) {
    this.actionName = actionName;
  }

  public String getActionName() {
    return actionName;
  }

  public void setScope(DelfoiActionScope scope) {
    this.scope = scope;
  }

  public DelfoiActionScope getScope() {
    return scope;
  }

  @Id 
  @GeneratedValue(strategy=GenerationType.TABLE, generator="DelfoiAction")  
  @TableGenerator(name="DelfoiAction", initialValue=1, allocationSize=100, table = "hibernate_sequences", pkColumnName = "sequence_name", valueColumnName = "sequence_next_hi_value")
  private Long id;

  @NotEmpty
  @NotNull
  @Column (nullable = false, unique = true)
  private String actionName; 
  
  @NotNull
  @Column (nullable = false)
  @Enumerated (EnumType.STRING)
  private DelfoiActionScope scope;
}
