package fi.pyramus.domainmodel.projects;

import java.util.Date;
import java.util.List;
import java.util.Vector;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.TableGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.IndexColumn;
import org.hibernate.search.annotations.DocumentId;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.FullTextFilterDef;
import org.hibernate.search.annotations.FullTextFilterDefs;
import org.hibernate.search.annotations.Index;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.IndexedEmbedded;
import org.hibernate.search.annotations.Store;
import org.hibernate.validator.constraints.NotEmpty;

import fi.pyramus.domainmodel.base.ArchivableEntity;
import fi.pyramus.domainmodel.base.EducationalLength;
import fi.pyramus.domainmodel.users.User;
import fi.pyramus.persistence.search.filters.ArchivedEntityFilterFactory;

@Entity
@Indexed
@FullTextFilterDefs (
  @FullTextFilterDef (
     name="ArchivedProject",
     impl=ArchivedEntityFilterFactory.class
  )
)
public class Project implements ArchivableEntity {

  /**
   * Returns the unique identifier of this object.
   * 
   * @return The unique identifier of this object
   */
  public Long getId() {
    return id;
  }

  /**
   * Returns the name of this entity.
   * 
   * @return The name of this entity
   */
  public String getName() {
    return name;
  }

  /**
   * Sets the name of this entity.
   * 
   * @param name The name of this entity
   */
  public void setName(String name) {
    this.name = name;
  }

  public List<ProjectModule> getProjectModules() {
    return projectModules;
  }

  @SuppressWarnings("unused")
  private void setProjectModules(List<ProjectModule> projectModules) {
    this.projectModules = projectModules;
  }
  
  public void addProjectModule(ProjectModule projectModule) {
    if (projectModule.getProject() != null) {
      projectModule.getProject().removeProjectModule(projectModule);
    }
    projectModule.setProject(this);
    projectModules.add(projectModule);
  }
  
  public void removeProjectModule(ProjectModule projectModule) {
    projectModule.setProject(null);
    projectModules.remove(projectModule);
  }

  /**
   * Returns the creator, and therefore the owner, of this entity.
   * 
   * @return The creator of this entity
   */
  public User getCreator() {
    return creator;
  }

  /**
   * Sets the creator of this entity.
   * 
   * @param creator The creator of this entity
   */
  public void setCreator(User creator) {
    this.creator = creator;
  }
  
  /**
   * Returns the creation time of this entity.
   * 
   * @return The creation time of this entity
   */
  public Date getCreated() {
    return created;
  }

  /**
   * Sets the creation time of this entity.
   * 
   * @param created The creation time of this entity
   */
  public void setCreated(Date created) {
    this.created = created;
  }
  
  /**
   * Returns the last modifier of this entity.
   * 
   * @return The last modifier of this entity
   */
  public User getLastModifier() {
    return lastModifier;
  }
  
  /**
   * Sets the last modifier of this entity.
   * 
   * @param lastModifier The last modifier of this entity
   */
  public void setLastModifier(User lastModifier) {
    this.lastModifier = lastModifier;
  }

  /**
   * Returns the last modification time of this entity.
   * 
   * @return The last modification time of this entity
   */
  public Date getLastModified() {
    return lastModified;
  }

  /**
   * Sets the last modification time of this entity.
   * 
   * @param created The last modification time of this entity
   */
  public void setLastModified(Date lastModified) {
    this.lastModified = lastModified;
  }

  /**
   * Sets the archived flag of this object.
   * 
   * @param archived The archived flag of this object
   */
  public void setArchived(Boolean archived) {
    this.archived = archived;
  }

  /**
   * Returns the archived flag of this object.
   * 
   * @return The archived flag of this object
   */
  public Boolean getArchived() {
    return archived;
  }

  /**
   * Sets the project description.
   * 
   * @param description The project description
   */
  public void setDescription(String description) {
    this.description = description;
  }

  /**
   * Returns the project description.
   * 
   * @return The project description
   */
  public String getDescription() {
    return description;
  }

  @SuppressWarnings("unused")
  private void setOptionalStudiesLength(EducationalLength optionalStudiesLength) {
    this.optionalStudiesLength = optionalStudiesLength;
  }

  public EducationalLength getOptionalStudiesLength() {
    return optionalStudiesLength;
  }

  @Transient
  @Field (index = Index.UN_TOKENIZED, store = Store.YES)
  public String getNameSortable() {
    return name;
  }
  
  @Id
  @GeneratedValue(strategy=GenerationType.TABLE, generator="Project")  
  @TableGenerator(name="Project", allocationSize=1)
  @DocumentId
  private Long id;

  @NotNull
  @Column (nullable = false)
  @NotEmpty
  @Field (index=Index.TOKENIZED)
  private String name;

  @Basic (fetch = FetchType.LAZY)
  @Column (length=1073741824)
  @Field (index=Index.TOKENIZED)
  private String description;

  @OneToOne (cascade = CascadeType.ALL, orphanRemoval = true)
  @JoinColumn (name = "optionalStudies")
  private EducationalLength optionalStudiesLength = new EducationalLength();

  @OneToMany (cascade = CascadeType.ALL, orphanRemoval = true)
  @IndexColumn (name = "indexColumn")
  @JoinColumn (name="project")
  @IndexedEmbedded
  private List<ProjectModule> projectModules = new Vector<ProjectModule>();

  @ManyToOne 
  @JoinColumn(name="creator")
  @IndexedEmbedded
  private User creator;
  
  @Column (updatable=false, nullable=false)
  @Temporal (value=TemporalType.TIMESTAMP)
  private Date created;
  
  @ManyToOne  
  @JoinColumn(name="lastModifier")
  private User lastModifier;
  
  @Column (nullable=false)
  @Temporal (value=TemporalType.TIMESTAMP)
  private Date lastModified;

  @NotNull
  @Column(nullable = false)
  @Field (index=Index.TOKENIZED)
  private Boolean archived = Boolean.FALSE;

}
