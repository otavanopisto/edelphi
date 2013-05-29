package fi.internetix.edelphi.domainmodel.base;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.TableGenerator;

@Entity
public class LocalizedEntry {

  public Long getId() {
    return id;
  }
  
  @Id 
  @GeneratedValue(strategy=GenerationType.TABLE, generator="LocalizedEntry")  
  @TableGenerator(name="LocalizedEntry", initialValue=1, allocationSize=100, table = "hibernate_sequences", pkColumnName = "sequence_name", valueColumnName = "sequence_next_hi_value")
  private Long id;
}
