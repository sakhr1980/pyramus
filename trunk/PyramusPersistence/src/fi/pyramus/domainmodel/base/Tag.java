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
import org.hibernate.validator.constraints.NotEmpty;

@Entity
@Cache (usage = CacheConcurrencyStrategy.READ_WRITE)
@Indexed
public class Tag {

  public Long getId() {
    return id;
  }

  public void setText(String text) {
    this.text = text;
  }

  public String getText() {
    return text;
  }
  
  @Id 
  @GeneratedValue(strategy=GenerationType.TABLE, generator="Tag")  
  @TableGenerator(name="Tag", allocationSize=1)
  @DocumentId
  private Long id;

  @NotNull
  @NotEmpty
  @Column (nullable = false, unique = true)
  @Field (index = Index.TOKENIZED)
  private String text;
}
