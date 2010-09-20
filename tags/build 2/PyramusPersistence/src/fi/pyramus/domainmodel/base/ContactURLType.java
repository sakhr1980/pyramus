package fi.pyramus.domainmodel.base;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.TableGenerator;
import javax.validation.constraints.NotNull;

import org.hibernate.search.annotations.DocumentId;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Index;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.validator.constraints.NotEmpty;

@Entity
@Indexed
public class ContactURLType {

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
  @Field (index = Index.UN_TOKENIZED)
  private Boolean archived = Boolean.FALSE;

}
