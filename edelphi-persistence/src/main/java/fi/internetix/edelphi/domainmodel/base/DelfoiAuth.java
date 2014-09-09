package fi.internetix.edelphi.domainmodel.base;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.TableGenerator;

import fi.internetix.edelphi.domainmodel.base.AuthSource;

@Entity
public class DelfoiAuth {

  public Long getId() {
    return id;
  }
  
  public Delfoi getDelfoi() {
    return delfoi;
  }

  public void setDelfoi(Delfoi delfoi) {
    this.delfoi = delfoi;
  }

  public AuthSource getAuthSource() {
    return authSource;
  }

  public void setAuthSource(AuthSource authSource) {
    this.authSource = authSource;
  }

  @Id 
  @GeneratedValue(strategy=GenerationType.TABLE, generator="DelfoiAuth")  
  @TableGenerator(name="DelfoiAuth", initialValue=1, allocationSize=100, table = "hibernate_sequences", pkColumnName = "sequence_name", valueColumnName = "sequence_next_hi_value")
  private Long id;

  @ManyToOne
  private Delfoi delfoi;

  @ManyToOne
  private AuthSource authSource;

}
