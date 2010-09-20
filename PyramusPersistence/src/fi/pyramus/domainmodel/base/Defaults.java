package fi.pyramus.domainmodel.base;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import fi.pyramus.domainmodel.courses.CourseState;

@Entity
public class Defaults {

  public Long getId() {
    return id;
  }

  public void setInitialCourseState(CourseState initialCourseState) {
    this.initialCourseState = initialCourseState;
  }

  public CourseState getInitialCourseState() {
    return initialCourseState;
  }

  public void setBaseTimeUnit(EducationalTimeUnit baseTimeUnit) {
    this.baseTimeUnit = baseTimeUnit;
  }

  public EducationalTimeUnit getBaseTimeUnit() {
    return baseTimeUnit;
  }

  @Id
  private Long id;

  @ManyToOne 
  @JoinColumn (name = "educationalTimeUnit")
  private EducationalTimeUnit baseTimeUnit;

  @ManyToOne 
  @JoinColumn (name = "courseState")
  private CourseState initialCourseState;

}
