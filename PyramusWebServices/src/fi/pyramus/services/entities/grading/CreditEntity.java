package fi.pyramus.services.entities.grading;

import java.util.Date;

import fi.pyramus.services.entities.users.UserEntity;

public class CreditEntity {

  public CreditEntity(Long id, Long studentId, Date date, GradeEntity grade, String verbalAssessment, UserEntity assessingUser, Boolean archived) {
    super();
    this.id = id;
    this.studentId = studentId;
    this.date = date;
    this.grade = grade;
    this.verbalAssessment = verbalAssessment;
    this.assessingUser = assessingUser;
    this.archived = archived;
  }

  public Long getId() {
    return id;
  }

  public Long getStudentId() {
    return studentId;
  }

  public Date getDate() {
    return date;
  }

  public GradeEntity getGrade() {
    return grade;
  }
  
  public String getVerbalAssessment() {
    return verbalAssessment;
  }

  public UserEntity getAssessingUser() {
    return assessingUser;
  }

  public Boolean getArchived() {
    return archived;
  }

  private Long id;
  private Long studentId;
  private Date date;
  private GradeEntity grade;
  private String verbalAssessment;
  private UserEntity assessingUser;
  private Boolean archived;
}
