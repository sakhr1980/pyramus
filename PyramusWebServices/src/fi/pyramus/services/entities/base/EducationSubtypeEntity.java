package fi.pyramus.services.entities.base;

public class EducationSubtypeEntity {

  public EducationSubtypeEntity(Long id, String name, String code, Long educationTypeId, Boolean archived) {
    super();
    this.id = id;
    this.name = name;
    this.code = code;
    this.educationTypeId = educationTypeId;
    this.archived = archived;
  }

  public Long getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public Long getEducationTypeId() {
    return educationTypeId;
  }

  public Boolean getArchived() {
    return archived;
  }
  
  public String getCode() {
    return code;
  }

  private Long id;
  private String name;
  private String code;
  private Long educationTypeId;
  private Boolean archived;
}
