package fi.pyramus.services.entities.base;

public class StudyProgrammeCategoryEntity {

  public StudyProgrammeCategoryEntity(Long id, String name, Boolean archived) {
    super();
    this.id = id;
    this.name = name;
    this.archived = archived;
  }

  public Long getId() {
    return id;
  }

  public String getName() {
    return name;
  }
  
  public Boolean getArchived() {
    return archived;
  }

  private Long id;
  private String name;
  private Boolean archived;

}
