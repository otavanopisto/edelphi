package fi.internetix.edelphi.domainmodel.resources;

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
public class LocalDocumentPage {

  /**
   * Returns internal unique id
   * 
   * @return Internal unique id
   */
  public Long getId() {
    return id;
  }
  
  public void setDocument(LocalDocument document) {
    this.document = document;
  }

  public LocalDocument getDocument() {
    return document;
  }
  
  public Integer getPageNumber() {
    return pageNumber;
  }
  
  public void setPageNumber(Integer pageNumber) {
    this.pageNumber = pageNumber;
  }
  
  public String getContent() {
    return content;
  }
  
  public void setContent(String content) {
    this.content = content;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  @Id 
  @GeneratedValue(strategy=GenerationType.TABLE, generator="LocalDocumentPage")  
  @TableGenerator(name="LocalDocumentPage", initialValue=1, allocationSize=100, table = "hibernate_sequences", pkColumnName = "sequence_name", valueColumnName = "sequence_next_hi_value")
  private Long id;
  
  @ManyToOne
  private LocalDocument document;
  
  @NotNull
  @NotEmpty
  @Column (nullable=false)
  private String title;
  
  @NotNull
  @Column (nullable = false)
  private Integer pageNumber;
  
  @Column (length=1073741824)
  private String content;  
}
