package fi.pyramus.services.entities.courses;

public class CourseEducationTypeEntity {

  public CourseEducationTypeEntity(Long id, String name, String code, CourseEducationSubtypeEntity[] subtypes) {
    super();
    this.id = id;
    this.name = name;
    this.code = code;
    this.subtypes = subtypes;
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

  public CourseEducationSubtypeEntity[] getSubtypes() {
    return subtypes;
  }

  private Long id;
  private String name;
  private String code;
  private CourseEducationSubtypeEntity subtypes[];
}
