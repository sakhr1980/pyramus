package fi.pyramus.services.entities.grading;


public class GradingScaleEntity {

  public GradingScaleEntity(Long id, String name, String description, Boolean archived) {
    super();
    this.id = id;
    this.name = name;
    this.description = description;
    this.archived = archived;
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

  private Long id;
  private String name;
  private String description;
  private Boolean archived;

}
