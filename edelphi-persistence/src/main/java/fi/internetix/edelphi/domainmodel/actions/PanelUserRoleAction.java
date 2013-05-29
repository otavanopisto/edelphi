package fi.internetix.edelphi.domainmodel.actions;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.PrimaryKeyJoinColumn;

import fi.internetix.edelphi.domainmodel.panels.Panel;

/**
 * Entity implementation class for Entity: PanelUserRoleAction
 *
 */
@Entity
@PrimaryKeyJoinColumn(name="id")
public class PanelUserRoleAction extends UserRoleAction {

  public void setPanel(Panel panel) {
    this.panel = panel;
  }

  public Panel getPanel() {
    return panel;
  }

  @ManyToOne
  private Panel panel;
}
