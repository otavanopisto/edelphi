package fi.internetix.edelphi.domainmodel.panels;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.TableGenerator;

@Entity
public class PanelExpertiseGroupUser {

  /**
   * Returns internal unique id
   * 
   * @return Internal unique id
   */
  public Long getId() {
    return id;
  }

  public void setPanelUser(PanelUser panelUser) {
    this.panelUser = panelUser;
  }

  public PanelUser getPanelUser() {
    return panelUser;
  }

  public void setWeight(Double weight) {
    this.weight = weight;
  }

  public Double getWeight() {
    return weight;
  }

  public void setExpertiseGroup(PanelUserExpertiseGroup expertiseGroup) {
    this.expertiseGroup = expertiseGroup;
  }

  public PanelUserExpertiseGroup getExpertiseGroup() {
    return expertiseGroup;
  }

  @Id 
  @GeneratedValue(strategy=GenerationType.TABLE, generator="PanelExpertiseGroupUser")  
  @TableGenerator(name="PanelExpertiseGroupUser", initialValue=1, allocationSize=100, table = "hibernate_sequences", pkColumnName = "sequence_name", valueColumnName = "sequence_next_hi_value")
  private Long id;
  
  @ManyToOne
  private PanelUser panelUser;
  
  @ManyToOne
  private PanelUserExpertiseGroup expertiseGroup;

  private Double weight;
}
