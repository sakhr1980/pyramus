package fi.pyramus.services.entities.courses;

public class CourseComponentEntity {

  public CourseComponentEntity(Long id, String name, String description, Double length, Long lengthUnitId) {
    super();
    this.id = id;
    this.name = name;
    this.description = description;
    this.length = length;
    this.lengthUnitId = lengthUnitId;
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

  public Double getLength() {
    return length;
  }

  public Long getLengthUnitId() {
    return lengthUnitId;
  }

  private Long id;
  private String name;
  private String description;
  private Double length;
  private Long lengthUnitId;
}
