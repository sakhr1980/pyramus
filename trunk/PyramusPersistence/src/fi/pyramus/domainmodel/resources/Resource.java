package fi.pyramus.domainmodel.resources;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.PersistenceException;
import javax.persistence.TableGenerator;
import javax.persistence.Transient;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;

import org.hibernate.HibernateException;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
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
import fi.pyramus.domainmodel.base.Tag;
import fi.pyramus.persistence.search.filters.ArchivedEntityFilterFactory;

@Entity
@Indexed
@Inheritance(strategy=InheritanceType.JOINED)
@Cache (usage = CacheConcurrencyStrategy.READ_WRITE)
@FullTextFilterDefs (
  @FullTextFilterDef (
     name="ArchivedResource",
     impl=ArchivedEntityFilterFactory.class
  )
)
public class Resource implements ArchivableEntity {

  public Long getId() {
    return id;
  }
  
  public String getName() {
    return name;
  }
  
  public void setName(String name) {
    this.name = name;
  }
  
  public ResourceCategory getCategory() {
    return category;
  }
  
  public void setCategory(ResourceCategory category) {
    this.category = category;
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
  
  public Set<Tag> getTags() {
    return tags;
  }
  
  public void setTags(Set<Tag> tags) {
    this.tags = tags;
  }
  
  public void addTag(Tag tag) {
    if (tags.contains(tag)) {
      tags.add(tag);
    } else {
      throw new PersistenceException("Entity already has this tag");
    }
  }
  
  public void removeTag(Tag tag) {
    if (tags.contains(tag)) {
      tags.add(tag);
    } else {
      throw new PersistenceException("Entity does not have this tag");
    }
  }
  
  @Field (index = Index.UN_TOKENIZED)
  public ResourceType getResourceType() {
    throw new HibernateException("Unimplemented resource type");
  }
  
  @Transient
  @Field (index = Index.UN_TOKENIZED, store = Store.YES)
  public String getNameSortable() {
    return name;
  }
  
  @SuppressWarnings("unused")
  private void setVersion(Long version) {
    this.version = version;
  }

  public Long getVersion() {
    return version;
  }

  @Id 
  @GeneratedValue(strategy=GenerationType.TABLE, generator="Resource")  
  @TableGenerator(name="Resource", allocationSize=1)
  @DocumentId
  private Long id;
  
  @NotNull
  @NotEmpty
  @Column (nullable = false) 
  @Field (index = Index.TOKENIZED)
  private String name;
  
  @ManyToOne  
  @JoinColumn(name="category")
  @IndexedEmbedded
  private ResourceCategory category;
  
  @NotNull
  @Column(nullable = false)
  @Field (index=Index.TOKENIZED)
  private Boolean archived = Boolean.FALSE;

  @ManyToMany (fetch = FetchType.LAZY)
  @JoinTable (name="__ResourceTags", joinColumns=@JoinColumn(name="resource"), inverseJoinColumns=@JoinColumn(name="tag"))
  @IndexedEmbedded 
  private Set<Tag> tags = new HashSet<Tag>();
  
  @Version
  @NotNull
  @Column(nullable = false)
  private Long version;
}
