package fi.pyramus.domainmodel.grading;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Transient;

import fi.pyramus.domainmodel.courses.CourseStudent;
import fi.pyramus.domainmodel.students.Student;
import fi.pyramus.persistence.usertypes.CreditType;

@Entity
@PrimaryKeyJoinColumn(name="id")
public class CourseAssessment extends Credit {
  
  public CourseAssessment() {
    super();
    setCreditType(CreditType.CourseAssessment);
  }
  
  public void setCourseStudent(CourseStudent courseStudent) {
    this.courseStudent = courseStudent;
  }
  
  public CourseStudent getCourseStudent() {
    return courseStudent;
  }

  @Transient
  public Student getStudent() {
    return courseStudent != null ? courseStudent.getStudent() : null;
  }
  
  @Override
  public CreditType getCreditType() {
    return CreditType.CourseAssessment;
  }
  
  @ManyToOne
  @JoinColumn(unique=true, name="courseStudent")
  private CourseStudent courseStudent;
}