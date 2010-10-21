package fi.pyramus.domainmodel.base;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.TableGenerator;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;

import org.hibernate.search.annotations.DocumentId;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Index;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.validator.constraints.NotEmpty;

@Entity
@Indexed
public class ContactURLType implements ArchivableEntity {

  public Long getId() {
    return id;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }

  public void setArchived(Boolean archived) {
    this.archived = archived;
  }

  public Boolean getArchived() {
    return archived;
  }

  @SuppressWarnings("unused")
  private void setVersion(Long version) {
    this.version = version;
  }

  public Long getVersion() {
    return version;
  }

  @Id 
  @DocumentId
  @GeneratedValue(strategy=GenerationType.TABLE, generator="ContactURLType")  
  @TableGenerator(name="ContactURLType", allocationSize=1)
  private Long id;

  @NotNull
  @NotEmpty
  @Column (nullable = false)
  @Field (index = Index.TOKENIZED)
  private String name;

  @NotNull
  @Column (nullable = false)
  @Field (index = Index.TOKENIZED)
  private Boolean archived = Boolean.FALSE;

  @Version
  @NotNull
  @Column(nullable = false)
  private Long version;
}
