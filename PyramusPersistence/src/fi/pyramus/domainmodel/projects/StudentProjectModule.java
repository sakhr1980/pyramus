package fi.pyramus.domainmodel.projects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.TableGenerator;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;

import fi.pyramus.domainmodel.base.AcademicTerm;
import fi.pyramus.domainmodel.modules.Module;
import fi.pyramus.persistence.usertypes.StudentProjectModuleOptionality;
import fi.pyramus.persistence.usertypes.StudentProjectModuleOptionalityUserType;

@Entity
@TypeDefs ({
  @TypeDef (name="StudentProjectModuleOptionality", typeClass=StudentProjectModuleOptionalityUserType.class)
})
public class StudentProjectModule {

  /**
   * Returns the unique identifier of this object.
   * 
   * @return The unique identifier of this object
   */
  public Long getId() {
    return id;
  }

  public Module getModule() {
    return module;
  }
  
  public void setModule(Module module) {
    this.module = module;
  }

  public void setStudentProject(StudentProject studentProject) {
    this.studentProject = studentProject;
  }

  public StudentProject getStudentProject() {
    return studentProject;
  }

  public void setOptionality(StudentProjectModuleOptionality optionality) {
    this.optionality = optionality;
  }

  public StudentProjectModuleOptionality getOptionality() {
    return optionality;
  }

  public void setAcademicTerm(AcademicTerm academicTerm) {
    this.academicTerm = academicTerm;
  }

  public AcademicTerm getAcademicTerm() {
    return academicTerm;
  }

  @SuppressWarnings("unused")
  private void setVersion(Long version) {
    this.version = version;
  }

  public Long getVersion() {
    return version;
  }

  @Id
  @GeneratedValue(strategy=GenerationType.TABLE, generator="StudentProjectModule")  
  @TableGenerator(name="StudentProjectModule", allocationSize=1)
  private Long id;
  
  @ManyToOne
  @JoinColumn(name="module")
  private Module module;

  @ManyToOne  
  @JoinColumn(name="studentProject")
  private StudentProject studentProject;

  @NotNull
  @Column (nullable = false)
  @Type (type="StudentProjectModuleOptionality")  
  private StudentProjectModuleOptionality optionality;
  
  @ManyToOne
  @JoinColumn(name="academicTerm")
  private AcademicTerm academicTerm;
  
  @Version
  @Column(nullable = false)
  private Long version;
}
