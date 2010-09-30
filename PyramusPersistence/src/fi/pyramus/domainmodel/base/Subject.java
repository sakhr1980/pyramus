package fi.pyramus.domainmodel.base;

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
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Index;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.Store;
import org.hibernate.validator.constraints.NotEmpty;

@Entity
@Indexed
@Cache (usage = CacheConcurrencyStrategy.READ_WRITE)
public class Subject implements ArchivableEntity {

  public Long getId() {
    return id;
  }

  @SuppressWarnings("unused")
  private void setId(Long id) {
    this.id = id;
  }

  public void setName(String name) {
    this.name = name;
  }

  
  public String getName() {
    return name;
  }
  
  public String getCode() {
    return code;
  }
  
  public void setCode(String code) {
    this.code = code;
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

  @Id 
  @GeneratedValue(strategy=GenerationType.TABLE, generator="Subject")  
  @TableGenerator(name="Subject", allocationSize=1)
  @DocumentId
  private Long id;
  
  private String code;

  @NotNull
  @NotEmpty
  @Column (nullable = false)
  @Field(index=Index.TOKENIZED, store=Store.NO)
  private String name;
  
  @NotNull
  @Column(nullable = false)
  private Boolean archived = Boolean.FALSE;

}
