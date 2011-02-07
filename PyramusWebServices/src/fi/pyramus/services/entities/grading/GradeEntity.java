package fi.pyramus.services.entities.grading;

public class GradeEntity {

  public GradeEntity(Long id, String name, String description, GradingScaleEntity gradingScale, Boolean passingGrade, Boolean archived, String qualification, Double gpa) {
    super();
    this.id = id;
    this.name = name;
    this.description = description;
    this.gradingScale = gradingScale;
    this.passingGrade = passingGrade;
    this.archived = archived;
    this.qualification = qualification;
    this.GPA = gpa;
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

  public GradingScaleEntity getGradingScale() {
    return gradingScale;
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
  private GradingScaleEntity gradingScale;
  private Boolean passingGrade;
  private Boolean archived;
  private String qualification;
  private Double GPA;
}
