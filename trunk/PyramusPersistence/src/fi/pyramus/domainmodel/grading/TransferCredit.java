package fi.pyramus.domainmodel.grading;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;

import fi.pyramus.domainmodel.base.EducationalLength;
import fi.pyramus.domainmodel.base.School;
import fi.pyramus.domainmodel.base.Subject;
import fi.pyramus.persistence.usertypes.CreditType;

@Entity
@PrimaryKeyJoinColumn(name = "id")
public class TransferCredit extends Credit {

  public TransferCredit() {
    super();
    setCreditType(CreditType.CourseAssessment);
  }

  public String getCourseName() {
    return courseName;
  }

  public void setCourseName(String courseName) {
    this.courseName = courseName;
  }

  public EducationalLength getCourseLength() {
    return courseLength;
  }

  public void setCourseLength(EducationalLength courseLength) {
    this.courseLength = courseLength;
  }

  public School getSchool() {
    return school;
  }

  public void setSchool(School school) {
    this.school = school;
  }

  public Subject getSubject() {
    return subject;
  }

  public void setSubject(Subject subject) {
    this.subject = subject;
  }

  @Override
  public CreditType getCreditType() {
    return CreditType.TransferCredit;
  }

  @NotNull
  @Column(nullable = false)
  @NotEmpty
  private String courseName;

  @OneToOne
  @JoinColumn(name = "courseLength")
  private EducationalLength courseLength;

  @ManyToOne
  @JoinColumn(name = "school")
  private School school;

  @ManyToOne
  @JoinColumn(name = "subject")
  private Subject subject;
}
