package fi.internetix.edelphi.domainmodel.base;

import java.util.Locale;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.TableGenerator;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;

@Entity
public class LocalizedValue {

  public Long getId() {
    return id;
  }
  
  public LocalizedEntry getEntry() {
    return entry;
  }
  
  public void setEntry(LocalizedEntry entry) {
    this.entry = entry;
  }
  
  public Locale getLocale() {
    return locale;
  }
  
  public void setLocale(Locale locale) {
    this.locale = locale;
  }
  
  public String getText() {
    return text;
  }
  
  public void setText(String text) {
    this.text = text;
  }
  
  @Id 
  @GeneratedValue(strategy=GenerationType.TABLE, generator="LocalizedEntryText")  
  @TableGenerator(name="LocalizedEntryText", initialValue=1, allocationSize=100, table = "hibernate_sequences", pkColumnName = "sequence_name", valueColumnName = "sequence_next_hi_value")
  private Long id;
  
  @ManyToOne
  private LocalizedEntry entry;
  
  @Column (nullable = false, length = 1073741824)
  @NotNull
  @NotEmpty
  private String text;
  
  @Column (nullable = false)
  @NotNull
  private Locale locale;
}
