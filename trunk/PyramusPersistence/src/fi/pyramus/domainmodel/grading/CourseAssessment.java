package fi.pyramus.domainmodel.grading;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import fi.pyramus.domainmodel.courses.Course;
import fi.pyramus.persistence.usertypes.CreditType;

@Entity
@PrimaryKeyJoinColumn(name="id")
public class CourseAssessment extends Credit {
  
  public CourseAssessment() {
    super();
    setCreditType(CreditType.CourseAssessment);
  }
  
  public void setCourse(Course course) {
    this.course = course;
  }
  
  public Course getCourse() {
    return course;
  }
  
  @Override
  public CreditType getCreditType() {
    return CreditType.CourseAssessment;
  }
  
  @ManyToOne (optional = false)
  @JoinColumn(name="course")
  private Course course;
}