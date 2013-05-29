package fi.internetix.edelphi.domainmodel.panels;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.TableGenerator;

@Entity
public class PanelUserExpertiseGroup {

  /**
   * Returns internal unique id
   * 
   * @return Internal unique id
   */
  public Long getId() {
    return id;
  }

  public void setExpertiseClass(PanelUserExpertiseClass expertiseClass) {
    this.expertiseClass = expertiseClass;
  }

  public PanelUserExpertiseClass getExpertiseClass() {
    return expertiseClass;
  }

  public void setIntressClass(PanelUserIntressClass intressClass) {
    this.intressClass = intressClass;
  }

  public PanelUserIntressClass getIntressClass() {
    return intressClass;
  }

  public void setColor(Long color) {
    this.color = color;
  }

  public Long getColor() {
    return color;
  }

  public void setPanel(Panel panel) {
    this.panel = panel;
  }

  public Panel getPanel() {
    return panel;
  }

  public PanelStamp getStamp() {
    return stamp;
  }

  public void setStamp(PanelStamp stamp) {
    this.stamp = stamp;
  }

  @Id 
  @GeneratedValue(strategy=GenerationType.TABLE, generator="PanelUserExpertiseGroup")  
  @TableGenerator(name="PanelUserExpertiseGroup", initialValue=1, allocationSize=100, table = "hibernate_sequences", pkColumnName = "sequence_name", valueColumnName = "sequence_next_hi_value")
  private Long id;
  
  @ManyToOne
  private Panel panel;
  
  @ManyToOne
  private PanelStamp stamp;

  @ManyToOne
  private PanelUserExpertiseClass expertiseClass;

  @ManyToOne
  private PanelUserIntressClass intressClass;
  
  private Long color;
}
