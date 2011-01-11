package fi.pyramus.services.entities.grading;

import java.util.Date;

public class CourseAssessmentEntity extends CreditEntity {

  public CourseAssessmentEntity(Long id, Long studentId, Date date, Long gradeId, String verbalAssessment, Long assessingUserId, Boolean archived,
      Long courseId, Long courseStudentId) {
    super(id, studentId, date, gradeId, verbalAssessment, assessingUserId, archived);
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
