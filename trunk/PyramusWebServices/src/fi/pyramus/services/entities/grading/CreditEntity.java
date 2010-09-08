package fi.pyramus.services.entities.grading;

import java.util.Date;

public class CreditEntity {

  public CreditEntity(Long id, Long studentId, Date date, Long gradeId, String verbalAssessment, Long assessingUserId, Boolean archived) {
    super();
    this.id = id;
    this.studentId = studentId;
    this.date = date;
    this.gradeId = gradeId;
    this.verbalAssessment = verbalAssessment;
    this.assessingUserId = assessingUserId;
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

  public Long getGradeId() {
    return gradeId;
  }

  public String getVerbalAssessment() {
    return verbalAssessment;
  }

  public Long getAssessingUserId() {
    return assessingUserId;
  }

  public Boolean getArchived() {
    return archived;
  }

  private Long id;
  private Long studentId;
  private Date date;
  private Long gradeId;
  private String verbalAssessment;
  private Long assessingUserId;
  private Boolean archived;
}
