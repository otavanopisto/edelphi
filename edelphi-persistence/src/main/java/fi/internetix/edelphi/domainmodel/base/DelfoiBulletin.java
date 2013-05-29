package fi.internetix.edelphi.domainmodel.base;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.PrimaryKeyJoinColumn;

@Entity
@PrimaryKeyJoinColumn(name="id")
public class DelfoiBulletin extends Bulletin {
  
  public Delfoi getDelfoi() {
    return delfoi;
  }
  
  public void setDelfoi(Delfoi delfoi) {
    this.delfoi = delfoi;
  }
  
  @ManyToOne
  private Delfoi delfoi;
}
