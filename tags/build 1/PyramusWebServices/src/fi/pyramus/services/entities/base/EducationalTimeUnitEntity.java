package fi.pyramus.services.entities.base;

public class EducationalTimeUnitEntity {

  public EducationalTimeUnitEntity(Long id, Double baseUnits, String name, Boolean archived) {
    super();
    this.id = id;
    this.baseUnits = baseUnits;
    this.name = name;
    this.archived = archived;
  }

  public Long getId() {
    return id;
  }

  public Double getBaseUnits() {
    return baseUnits;
  }

  public String getName() {
    return name;
  }

  public Boolean getArchived() {
    return archived;
  }

  private Long id;
  private Double baseUnits;
  private String name;
  private Boolean archived;
}
