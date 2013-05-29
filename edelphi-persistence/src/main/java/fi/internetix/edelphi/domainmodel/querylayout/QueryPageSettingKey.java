package fi.internetix.edelphi.domainmodel.querylayout;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.TableGenerator;

import org.hibernate.validator.constraints.NotEmpty;

@Entity
public class QueryPageSettingKey {

  public Long getId() {
    return id;
  } 
  
  public String getName() {
    return name;
  }
  
  public void setName(String name) {
    this.name = name;
  }

  @Id
  @GeneratedValue(strategy = GenerationType.TABLE, generator = "QueryPageSettingKey")
  @TableGenerator(name = "QueryPageSettingKey", allocationSize = 1, table = "hibernate_sequences", pkColumnName = "sequence_name", valueColumnName = "sequence_next_hi_value")
  private Long id;

  @Column (nullable = false, unique = true)
  @NotEmpty
  private String name;
}
