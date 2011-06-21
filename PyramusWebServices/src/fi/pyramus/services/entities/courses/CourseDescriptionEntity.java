package fi.pyramus.services.entities.courses;


public class CourseDescriptionEntity {

  public CourseDescriptionEntity(Long id, Long courseBaseId, CourseDescriptionCategoryEntity category, String description) {
    this.id = id;
    this.courseBaseId = courseBaseId;
    this.category = category;
    this.description = description;
  }
  
  public Long getId() {
    return id;
  }

  public Long getCourseBaseId() {
    return courseBaseId;
  }

  public String getDescription() {
    return description;
  }
  
  public CourseDescriptionCategoryEntity getCategory() {
    return category;
  }

  private final Long id;
  private final Long courseBaseId;
  private final CourseDescriptionCategoryEntity category;
  private final String description;
}
