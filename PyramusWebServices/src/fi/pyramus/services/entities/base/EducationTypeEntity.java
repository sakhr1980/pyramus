package fi.pyramus.services.entities.base;

public class EducationTypeEntity {

  public EducationTypeEntity(Long id, String name, String code, EducationSubtypeEntity[] subtypes, Boolean archived) {
    super();
    this.id = id;
    this.name = name;
    this.code = code;
    this.subtypes = subtypes;
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

  public EducationSubtypeEntity[] getSubtypes() {
    return subtypes;
  }

  public Boolean getArchived() {
    return archived;
  }

  private Long id;
  private String name;
  private String code;
  private EducationSubtypeEntity subtypes[];
  private Boolean archived;
}
