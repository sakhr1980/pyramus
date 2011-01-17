package fi.pyramus.services.entities.courses;

public class CourseEducationSubtypeEntity {

  public CourseEducationSubtypeEntity(Long id, String name, String code, Long courseEducationTypeId) {
    super();
    this.id = id;
    this.name = name;
    this.code = code;
    this.courseEducationTypeId = courseEducationTypeId;
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

  public Long getCourseEducationTypeId() {
    return courseEducationTypeId;
  }

  private Long id;
  private String name;
  private String code;
  private Long courseEducationTypeId;
}
