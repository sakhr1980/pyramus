package fi.pyramus.domainmodel.resources;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.TableGenerator;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.search.annotations.DocumentId;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.validator.constraints.NotEmpty;

import fi.pyramus.domainmodel.base.ArchivableEntity;

@Entity
@Indexed
@Cache (usage = CacheConcurrencyStrategy.READ_WRITE)
public class ResourceCategory implements ArchivableEntity {

  public Long getId() {
    return id;
  }
  
  @NotNull
  @Column (nullable = false) 
  public String getName() {
    return name;
  }
  
  public void setName(String name) {
    this.name = name;
  }

  public void setArchived(Boolean archived) {
    this.archived = archived;
  }

  public Boolean getArchived() {
    return archived;
  }

  @Id 
  @GeneratedValue(strategy=GenerationType.TABLE, generator="ResourceCategory")  
  @TableGenerator(name="ResourceCategory", allocationSize=1)
  @DocumentId
  private Long id;

  @NotEmpty
  private String name;

  @NotNull
  @Column(nullable = false)
  private Boolean archived = Boolean.FALSE;

}
