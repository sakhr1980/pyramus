package fi.pyramus.services.entities.base;

public class StudyProgrammeEntity {

  public StudyProgrammeEntity(Long id, String name, String code, StudyProgrammeCategoryEntity category, Boolean archived) {
    super();
    this.id = id;
    this.name = name;
    this.code = code;
    this.category = category;
    this.archived = archived;
  }

  public Long getId() {
    return id;
  }

  public String getName() {
    return name;
  }
  
  public String getCode() {
    return code;
  }

  public StudyProgrammeCategoryEntity getCategory() {
    return category;
  }
  
  public Boolean getArchived() {
    return archived;
  }

  private Long id;
  private String name;
  private String code;
  private StudyProgrammeCategoryEntity category;
  private Boolean archived;
}
