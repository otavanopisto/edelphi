package fi.internetix.edelphi.domainmodel.panels;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.TableGenerator;

import fi.internetix.edelphi.domainmodel.actions.DelfoiAction;
import fi.internetix.edelphi.domainmodel.users.UserRole;

@Entity
public class PanelSettingsTemplateRole {

  /**
   * Returns internal unique id
   * 
   * @return Internal unique id
   */
  public Long getId() {
    return id;
  }

  
  public PanelSettingsTemplate getPanelSettingsTemplate() {
    return panelSettingsTemplate;
  }

  public void setPanelSettingsTemplate(PanelSettingsTemplate panelSettingsTemplate) {
    this.panelSettingsTemplate = panelSettingsTemplate;
  }


  public DelfoiAction getDelfoiAction() {
    return delfoiAction;
  }


  public void setDelfoiAction(DelfoiAction delfoiAction) {
    this.delfoiAction = delfoiAction;
  }


  public UserRole getUserRole() {
    return userRole;
  }


  public void setUserRole(UserRole userRole) {
    this.userRole = userRole;
  }

  @Id 
  @GeneratedValue(strategy=GenerationType.TABLE, generator="PanelSettingsTemplateRole")  
  @TableGenerator(name="PanelSettingsTemplateRole", initialValue=1, allocationSize=100, table = "hibernate_sequences", pkColumnName = "sequence_name", valueColumnName = "sequence_next_hi_value")
  private Long id;
  
  @ManyToOne
  private PanelSettingsTemplate panelSettingsTemplate;
  
  @ManyToOne
  private DelfoiAction delfoiAction;
  
  @ManyToOne
  private UserRole userRole;
}
