package fi.internetix.edelphi.domainmodel.base;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.TableGenerator;

import fi.internetix.edelphi.domainmodel.panels.PanelUserRole;
import fi.internetix.edelphi.domainmodel.users.DelfoiUserRole;

@Entity
public class DelfoiDefaults {

  /**
   * Returns internal unique id
   * 
   * @return Internal unique id
   */
  public Long getId() {
    return id;
  }
  
  public void setDefaultPanelCreatorRole(PanelUserRole defaultPanelCreatorRole) {
    this.defaultPanelCreatorRole = defaultPanelCreatorRole;
  }

  public PanelUserRole getDefaultPanelCreatorRole() {
    return defaultPanelCreatorRole;
  }

  public void setDefaultDelfoiUserRole(DelfoiUserRole defaultDelfoiUserRole) {
    this.defaultDelfoiUserRole = defaultDelfoiUserRole;
  }

  public DelfoiUserRole getDefaultDelfoiUserRole() {
    return defaultDelfoiUserRole;
  }

  public void setDelfoi(Delfoi delfoi) {
    this.delfoi = delfoi;
  }

  public Delfoi getDelfoi() {
    return delfoi;
  }

  @Id 
  @GeneratedValue(strategy=GenerationType.TABLE, generator="DelfoiDefaults")  
  @TableGenerator(name="DelfoiDefaults", initialValue=1, allocationSize=100, table = "hibernate_sequences", pkColumnName = "sequence_name", valueColumnName = "sequence_next_hi_value")
  private Long id;
  
  @ManyToOne
  private Delfoi delfoi;
  
  /**
   * Default role for User that registers to Delfoi
   */
  @ManyToOne
  private DelfoiUserRole defaultDelfoiUserRole;

  /**
   * Default role for User that has created a Panel
   */
  @ManyToOne
  private PanelUserRole defaultPanelCreatorRole;
}
