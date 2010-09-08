package fi.pyramus.domainmodel.courses;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PrimaryKeyJoinColumn;

import org.hibernate.search.annotations.Indexed;

import fi.pyramus.domainmodel.base.ComponentBase;

@Entity
@Indexed
@PrimaryKeyJoinColumn(name="id")
public class CourseComponent extends ComponentBase {

  public Course getCourse() {
    return course;
  }
  
  public void setCourse(Course course) {
    this.course = course;
  }
  
  @ManyToOne
  @JoinColumn(name="course")
  private Course course;

}
