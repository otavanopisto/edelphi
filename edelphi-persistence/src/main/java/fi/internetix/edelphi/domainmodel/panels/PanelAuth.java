package fi.internetix.edelphi.domainmodel.panels;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.TableGenerator;

import fi.internetix.edelphi.domainmodel.base.AuthSource;

@Entity
public class PanelAuth {

  public Long getId() {
    return id;
  }
  
  public Panel getPanel() {
    return panel;
  }

  public void setPanel(Panel panel) {
    this.panel = panel;
  }

  public AuthSource getAuthSource() {
    return authSource;
  }

  public void setAuthSource(AuthSource authSource) {
    this.authSource = authSource;
  }

  @Id 
  @GeneratedValue(strategy=GenerationType.TABLE, generator="PanelAuth")  
  @TableGenerator(name="PanelAuth", initialValue=1, allocationSize=100, table = "hibernate_sequences", pkColumnName = "sequence_name", valueColumnName = "sequence_next_hi_value")
  private Long id;

  @ManyToOne
  private Panel panel;

  @ManyToOne
  private AuthSource authSource;

}
