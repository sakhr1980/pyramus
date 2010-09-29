package fi.pyramus.domainmodel.help;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Transient;

import org.hibernate.search.annotations.Indexed;

@Entity
@Indexed
@PrimaryKeyJoinColumn (name="id")
public class HelpPage extends HelpItem {
  
  public List<HelpPageContent> getContents() {
    return contents;
  }
  
  public void setContents(List<HelpPageContent> contents) {
    this.contents = contents;
  }
  
  @Transient
  public void addContent(HelpPageContent helpPageContent) {
    if (helpPageContent.getPage() != null) {
      helpPageContent.getPage().removeContent(helpPageContent);
    }
      
    helpPageContent.setPage(this);
    contents.add(helpPageContent);
  }
  
  @Transient
  public void removeContent(HelpPageContent helpPageContent) {
    helpPageContent.setPage(null);
    contents.remove(helpPageContent);
  }
  
  @OneToMany (cascade = CascadeType.ALL, mappedBy="page")
  private List<HelpPageContent> contents = new ArrayList<HelpPageContent>();
}
