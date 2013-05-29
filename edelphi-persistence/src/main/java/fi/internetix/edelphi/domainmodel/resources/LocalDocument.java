package fi.internetix.edelphi.domainmodel.resources;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.PrimaryKeyJoinColumn;

@Entity
@PrimaryKeyJoinColumn(name="id")
public class LocalDocument extends Document {
  
  public LocalDocument() {
    setType(ResourceType.LOCAL_DOCUMENT);
  }

  public void setPages(List<LocalDocumentPage> pages) {
    this.pages = pages;
  }

  public List<LocalDocumentPage> getPages() {
    return pages;
  }
  
  @OneToMany (mappedBy = "document")
  private List<LocalDocumentPage> pages = new ArrayList<LocalDocumentPage>();
}