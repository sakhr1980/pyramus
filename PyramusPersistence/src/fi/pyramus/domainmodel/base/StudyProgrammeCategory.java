package fi.pyramus.domainmodel.base;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.TableGenerator;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;

@Entity
public class StudyProgrammeCategory implements ArchivableEntity {

  public Long getId() {
    return id;
  }
  
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
  @GeneratedValue(strategy=GenerationType.TABLE, generator="StudyProgrammeCategory")  
  @TableGenerator(name="StudyProgrammeCategory", allocationSize=1)
  private Long id;
  
  @NotNull
  @Column (nullable = false)
  @NotEmpty
  private String name;
  
  @NotNull
  @Column(nullable = false)
  private Boolean archived = Boolean.FALSE;

}
