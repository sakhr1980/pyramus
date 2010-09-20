package fi.pyramus.services.entities.grading;

import java.util.Date;

public class TransferCreditEntity extends CreditEntity {

  public TransferCreditEntity(Long id, Long studentId, Date date, Long gradeId, String verbalAssessment, Long assessingUserId, boolean archived,
      String name, Double length, Long lenghtUnitId, Long schoolId, Long subjectId) {
    super(id, studentId, date, gradeId, verbalAssessment, assessingUserId, archived);
    this.name = name;
    this.length = length;
    this.lenghtUnitId = lenghtUnitId;
    this.schoolId = schoolId;
    this.subjectId = subjectId;
  }

  public String getName() {
    return name;
  }

  public Double getLength() {
    return length;
  }

  public Long getLenghtUnitId() {
    return lenghtUnitId;
  }

  public Long getSchoolId() {
    return schoolId;
  }

  public Long getSubjectId() {
    return subjectId;
  }

  private String name;
  private Double length;
  private Long lenghtUnitId;
  private Long schoolId;
  private Long subjectId;
}
