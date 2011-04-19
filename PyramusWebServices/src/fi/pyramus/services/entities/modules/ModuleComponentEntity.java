package fi.pyramus.services.entities.modules;

public class ModuleComponentEntity {

  public ModuleComponentEntity(Long id, String name, String description,
      Double length, Long lengthUnitId, Long moduleId) {
    super();
    this.id = id;
    this.name = name;
    this.description = description;
    this.length = length;
    this.lengthUnitId = lengthUnitId;
    this.moduleId = moduleId;
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

  public Long getModuleId() {
    return moduleId;
  }

  private Long id;
  private String name;
  private String description;
  private Double length;
  private Long lengthUnitId;
  private Long moduleId;
}
