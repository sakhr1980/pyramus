package fi.pyramus.services.entities.base;


public class SubjectEntity {

  public SubjectEntity(Long id, String code, String name, EducationTypeEntity educationType, Boolean archived) {
    super();
    this.id = id;
    this.code = code;
    this.name = name;
    this.educationType = educationType;
    this.archived = archived;
  }

  public Long getId() {
    return id;
  }

  public String getCode() {
    return code;
  }

  public String getName() {
    return name;
  }

  public Boolean getArchived() {
    return archived;
  }

  public EducationTypeEntity getEducationType() {
    return educationType;
  }

  private final Long id;
  private final String code;
  private final String name;
  private final Boolean archived;
  private final EducationTypeEntity educationType;
}
