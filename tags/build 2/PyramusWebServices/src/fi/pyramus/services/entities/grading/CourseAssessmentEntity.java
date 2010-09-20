package fi.pyramus.services.entities.grading;

import java.util.Date;

public class CourseAssessmentEntity extends CreditEntity {

  public CourseAssessmentEntity(Long id, Long studentId, Date date, Long gradeId, String verbalAssessment, Long assessingUserId, Boolean archived,
      Long courseId) {
    super(id, studentId, date, gradeId, verbalAssessment, assessingUserId, archived);
    this.courseId = courseId;
  }
  
  public Long getCourseId() {
    return courseId;
  }

  private Long courseId;
}
