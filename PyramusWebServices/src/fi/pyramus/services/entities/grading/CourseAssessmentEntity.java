package fi.pyramus.services.entities.grading;

import java.util.Date;

import fi.pyramus.services.entities.users.UserEntity;

public class CourseAssessmentEntity extends CreditEntity {

  public CourseAssessmentEntity(Long id, Long studentId, Date date, GradeEntity grade, String verbalAssessment, UserEntity assessingUser, Boolean archived,
      Long courseId, Long courseStudentId) {
    super(id, studentId, date, grade, verbalAssessment, assessingUser, archived);
    this.courseId = courseId;
    this.courseStudentId = courseStudentId;
  }
  
  public Long getCourseId() {
    return courseId;
  }

  public Long getCourseStudentId() {
    return courseStudentId;
  }

  private Long courseStudentId;
  private Long courseId;
}
