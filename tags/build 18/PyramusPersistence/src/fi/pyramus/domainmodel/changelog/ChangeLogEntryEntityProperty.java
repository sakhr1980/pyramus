package fi.pyramus.domainmodel.changelog;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.TableGenerator;

import org.hibernate.validator.constraints.NotEmpty;

@Entity
public class ChangeLogEntryEntityProperty {

  public Long getId() {
    return id;
  }
  
  public String getName() {
    return name;
  }
  
  public void setName(String name) {
    this.name = name;
  }

  public ChangeLogEntryEntity getEntity() {
    return entity;
  }

  public void setEntity(ChangeLogEntryEntity entity) {
    this.entity = entity;
  }

  @Id 
  @GeneratedValue(strategy=GenerationType.TABLE, generator="ChangeLogEntryEntityProperty")  
  @TableGenerator(name="ChangeLogEntryEntityProperty", allocationSize=1)
  private Long id;
  
  @NotEmpty
  @Column
  private String name;

  @ManyToOne  
  @JoinColumn(name="entity")
  private ChangeLogEntryEntity entity;
}
