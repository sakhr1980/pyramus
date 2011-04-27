package fi.pyramus.services.entities.grading;

import java.util.Date;

import fi.pyramus.services.entities.users.UserEntity;

public class TransferCreditEntity extends CreditEntity {

  public TransferCreditEntity(Long id, Long studentId, Date date, GradeEntity grade, String verbalAssessment, UserEntity assessingUser, boolean archived,
      String courseName, Integer courseNumber, Double length, Long lengthUnitId, Long schoolId, Long subjectId, String optionality) {
    
    super(id, studentId, date, grade, verbalAssessment, assessingUser, archived);
    this.courseName = courseName;
    this.courseNumber = courseNumber;
    this.length = length;
    this.lengthUnitId = lengthUnitId;
    this.schoolId = schoolId;
    this.subjectId = subjectId;
    this.optionality = optionality;
  }

  public String getCourseName() {
    return courseName;
  }
  
  public Integer getCourseNumber() {
    return courseNumber;
  }

  public Double getLength() {
    return length;
  }

  public Long getLengthUnitId() {
    return lengthUnitId;
  }

  public Long getSchoolId() {
    return schoolId;
  }

  public Long getSubjectId() {
    return subjectId;
  }
  
  public String getOptionality() {
    return optionality;
  }
  
  private String courseName;
  private Integer courseNumber;
  private Double length;
  private Long lengthUnitId;
  private Long schoolId;
  private Long subjectId;
  private String optionality;
}
