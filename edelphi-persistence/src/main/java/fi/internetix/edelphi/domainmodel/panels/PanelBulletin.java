package fi.internetix.edelphi.domainmodel.panels;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.PrimaryKeyJoinColumn;

import fi.internetix.edelphi.domainmodel.base.Bulletin;

@Entity
@PrimaryKeyJoinColumn(name="id")
public class PanelBulletin extends Bulletin {
  
  public Panel getPanel() {
    return panel;
  }
  
  public void setPanel(Panel panel) {
    this.panel = panel;
  }
  
  @ManyToOne
  private Panel panel;
}
