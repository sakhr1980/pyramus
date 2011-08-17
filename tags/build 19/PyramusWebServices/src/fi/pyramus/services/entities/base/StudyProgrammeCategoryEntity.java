package fi.pyramus.services.entities.base;

public class StudyProgrammeCategoryEntity {

  public StudyProgrammeCategoryEntity(Long id, String name, Boolean archived, EducationTypeEntity educationType) {
    super();
    this.id = id;
    this.name = name;
    this.archived = archived;
    this.educationType = educationType;
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

  public EducationTypeEntity getEducationType() {
    return educationType;
  }

  private final Long id;
  private final String name;
  private final Boolean archived;
  private final EducationTypeEntity educationType;
}
