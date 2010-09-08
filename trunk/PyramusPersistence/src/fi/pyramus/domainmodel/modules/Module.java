package fi.pyramus.domainmodel.modules;

import java.util.List;
import java.util.Vector;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.PrimaryKeyJoinColumn;

import org.hibernate.annotations.IndexColumn;
import org.hibernate.search.annotations.FullTextFilterDef;
import org.hibernate.search.annotations.FullTextFilterDefs;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.IndexedEmbedded;

import fi.pyramus.domainmodel.base.CourseBase;
import fi.pyramus.persistence.search.filters.ArchivedModuleFilterFactory;

@Entity
@Indexed
@PrimaryKeyJoinColumn(name="id")
@FullTextFilterDefs (
  @FullTextFilterDef (
     name="ArchivedModule",
     impl=ArchivedModuleFilterFactory.class
  )
)
public class Module extends CourseBase {

  public List<ModuleComponent> getModuleComponents() {
    return moduleComponents;
  }
  
  @SuppressWarnings("unused")
  private void setModuleComponents(List<ModuleComponent> moduleComponents) {
    this.moduleComponents = moduleComponents;
  }
  
  public void addModuleComponent(ModuleComponent moduleComponent) {
    if (moduleComponent.getModule() != null)
      moduleComponent.getModule().getModuleComponents().remove(moduleComponent);
    moduleComponent.setModule(this);
    this.moduleComponents.add(moduleComponent);
  }
  
  public void removeModuleComponent(ModuleComponent moduleComponent) {
    moduleComponent.setModule(null);
    this.moduleComponents.remove(moduleComponent);
  } 

  @OneToMany (cascade = CascadeType.ALL, orphanRemoval = true)
  @IndexColumn (name = "indexColumn")
  @JoinColumn (name="module")
  @IndexedEmbedded
  private List<ModuleComponent> moduleComponents = new Vector<ModuleComponent>();

}
