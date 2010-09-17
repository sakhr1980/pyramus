package fi.pyramus.services.entities.courses;

import java.util.Date;

import fi.pyramus.services.entities.students.StudentEntity;

public class CourseStudentEntity {

  public CourseStudentEntity(Long id, Date enrolmentTime, StudentEntity student, CourseEntity course, CourseParticipationTypeEntity participationType,
      CourseEnrolmentTypeEntity courseEnrolmentType, Boolean lodging, Boolean archived) {
    super();
    this.id = id;
    this.enrolmentTime = enrolmentTime;
    this.student = student;
    this.course = course;
    this.participationType = participationType;
    this.courseEnrolmentType = courseEnrolmentType;
    this.lodging = lodging;
    this.archived = archived;
  }

  public Long getId() {
    return id;
  }

  public Date getEnrolmentTime() {
    return enrolmentTime;
  }

  public StudentEntity getStudent() {
    return student;
  }

  public CourseEntity getCourse() {
    return course;
  }

  public CourseParticipationTypeEntity getParticipationType() {
    return participationType;
  }

  public CourseEnrolmentTypeEntity getCourseEnrolmentType() {
    return courseEnrolmentType;
  }
  
  public Boolean getLodging() {
    return lodging;
  }

  public Boolean getArchived() {
  	return archived;
  }

  private Long id;
  private Date enrolmentTime;
  private StudentEntity student;
  private CourseEntity course;
  private CourseParticipationTypeEntity participationType;
  private CourseEnrolmentTypeEntity courseEnrolmentType;
  private Boolean lodging;
  private Boolean archived;
}
