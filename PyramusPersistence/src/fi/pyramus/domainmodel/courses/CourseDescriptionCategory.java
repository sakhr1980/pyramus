package fi.pyramus.domainmodel.courses;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.TableGenerator;
import javax.validation.constraints.NotNull;

import org.hibernate.search.annotations.DocumentId;

@Entity
public class CourseDescriptionCategory {

  /**
   * Returns the identifier of this entity.
   * 
   * @return The identifier of this entity
   */
  public Long getId() {
    return id;
  }

  /**
   * Sets the identifier of this entity.
   * 
   * @param id The identifier of this entity
   */
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

  @Id
  @DocumentId
  @GeneratedValue(strategy=GenerationType.TABLE, generator="CourseDescriptionCategory")  
  @TableGenerator(name="CourseDescriptionCategory", allocationSize=1)
  private Long id;

  @NotNull
  @Column(nullable = false)
  private String name;
}
