package fi.pyramus.services.entities.base;

import java.util.Date;

public class AcademicTermEntity {

  public AcademicTermEntity(Long id, String name, Date startDate, Date endDate, Boolean archived) {
    super();
    this.id = id;
    this.name = name;
    this.startDate = startDate;
    this.endDate = endDate;
    this.archived = archived;
  }

  public Long getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public Date getStartDate() {
    return startDate;
  }

  public Date getEndDate() {
    return endDate;
  }

  public Boolean getArchived() {
    return archived;
  }

  private Long id;
  private String name;
  private Date startDate;
  private Date endDate;
  private Boolean archived;
}
