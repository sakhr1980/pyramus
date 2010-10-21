package fi.pyramus.domainmodel.base;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;

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

  @SuppressWarnings("unused")
  private void setVersion(Long version) {
    this.version = version;
  }

  public Long getVersion() {
    return version;
  }

  @Id
  private Long id;

  @ManyToOne 
  @JoinColumn (name = "educationalTimeUnit")
  private EducationalTimeUnit baseTimeUnit;

  @ManyToOne 
  @JoinColumn (name = "courseState")
  private CourseState initialCourseState;

  @Version
  @NotNull
  @Column(nullable = false)
  private Long version;
}
