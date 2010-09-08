package fi.pyramus.services.entities.grading;

public class GradeEntity {

  public GradeEntity(Long id, String name, String description, Long gradingScaleId, Boolean passingGrade, Boolean archived, String qualification, Double gpa) {
    super();
    this.id = id;
    this.name = name;
    this.description = description;
    this.gradingScaleId = gradingScaleId;
    this.passingGrade = passingGrade;
    this.archived = archived;
    this.qualification = qualification;
    GPA = gpa;
  }

  public Long getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public String getDescription() {
    return description;
  }

  public Long getGradingScaleId() {
    return gradingScaleId;
  }

  public Boolean getPassingGrade() {
    return passingGrade;
  }
  
  public Boolean getArchived() {
    return archived;
  }

  public String getQualification() {
    return qualification;
  }

  public Double getGPA() {
    return GPA;
  }

  private Long id;
  private String name;
  private String description;
  private Long gradingScaleId;
  private Boolean passingGrade;
  private Boolean archived;
  private String qualification;
  private Double GPA;
}
