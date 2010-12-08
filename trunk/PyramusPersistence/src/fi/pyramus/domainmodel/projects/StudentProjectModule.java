package fi.pyramus.domainmodel.projects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.TableGenerator;
import javax.persistence.Transient;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;

import fi.pyramus.domainmodel.base.AcademicTerm;
import fi.pyramus.domainmodel.modules.Module;
import fi.pyramus.persistence.usertypes.CourseOptionality;
import fi.pyramus.persistence.usertypes.CourseOptionalityUserType;

@Entity
@TypeDefs ({
  @TypeDef (name="CourseOptionality", typeClass=CourseOptionalityUserType.class)
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

  public void setOptionality(CourseOptionality optionality) {
    this.optionality = optionality;
  }

  public CourseOptionality getOptionality() {
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

  @Transient
  public boolean hasCourseEquivalent() {
    if (getStudentProject() != null) {
      for (StudentProjectCourse studentProjectCourse : getStudentProject().getStudentProjectCourses()) {
        if (studentProjectCourse.getCourse().getModule().equals(getModule()))
          return true;
      }
    }
    
    return false;
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
  @Type (type="CourseOptionality")  
  private CourseOptionality optionality;
  
  @ManyToOne
  @JoinColumn(name="academicTerm")
  private AcademicTerm academicTerm;
  
  @Version
  @Column(nullable = false)
  private Long version;
}
