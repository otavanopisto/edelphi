package fi.internetix.edelphi.domainmodel.base;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.TableGenerator;

import fi.internetix.edelphi.domainmodel.resources.Folder;

@Entity
public class Delfoi {

  /**
   * Returns internal unique id
   * 
   * @return Internal unique id
   */
  public Long getId() {
    return id;
  }

  public void setRootFolder(Folder rootFolder) {
    this.rootFolder = rootFolder;
  }

  public Folder getRootFolder() {
    return rootFolder;
  }

  public void setDomain(String domain) {
    this.domain = domain;
  }

  public String getDomain() {
    return domain;
  }

  @Id 
  @GeneratedValue(strategy=GenerationType.TABLE, generator="Delfoi")  
  @TableGenerator(name="Delfoi", initialValue=1, allocationSize=100, table = "hibernate_sequences", pkColumnName = "sequence_name", valueColumnName = "sequence_next_hi_value")
  private Long id;
  
  private String domain;
  
  @ManyToOne
  private Folder rootFolder;
}
