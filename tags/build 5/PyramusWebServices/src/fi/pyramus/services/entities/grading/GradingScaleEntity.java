package fi.pyramus.services.entities.grading;


public class GradingScaleEntity {

  public GradingScaleEntity(Long id, String name, String description, Boolean archived, GradeEntity[] grades) {
    super();
    this.id = id;
    this.name = name;
    this.description = description;
    this.archived = archived;
    this.grades = grades;
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

  public Boolean getArchived() {
    return archived;
  }

  public GradeEntity[] getGrades() {
    return grades;
  }

  private Long id;
  private String name;
  private String description;
  private Boolean archived;
  private GradeEntity[] grades;
}
