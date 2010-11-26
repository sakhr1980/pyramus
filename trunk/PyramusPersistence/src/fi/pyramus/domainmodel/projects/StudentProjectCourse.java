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

import fi.pyramus.domainmodel.courses.Course;

@Entity
public class StudentProjectCourse {

  /**
   * Returns the unique identifier of this object.
   * 
   * @return The unique identifier of this object
   */
  public Long getId() {
    return id;
  }

  public Course getCourse() {
    return course;
  }
  
  public void setCourse(Course course) {
    this.course = course;
  }

  public void setStudentProject(StudentProject studentProject) {
    this.studentProject = studentProject;
  }

  public StudentProject getStudentProject() {
    return studentProject;
  }

  @SuppressWarnings("unused")
  private void setVersion(Long version) {
    this.version = version;
  }

  public Long getVersion() {
    return version;
  }

  @Id
  @GeneratedValue(strategy=GenerationType.TABLE, generator="StudentProjectCourse")  
  @TableGenerator(name="StudentProjectCourse", allocationSize=1)
  private Long id;
  
  @ManyToOne
  @JoinColumn(name="course")
  private Course course;

  @ManyToOne  
  @JoinColumn(name="studentProject")
  private StudentProject studentProject;
  
  @Version
  @Column(nullable = false)
  private Long version;
}
