package fi.pyramus.domainmodel.changelog;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.TableGenerator;

import org.hibernate.validator.constraints.NotEmpty;

@Entity
public class ChangeLogEntryEntity {

  public Long getId() {
    return id;
  }
  
  public String getName() {
    return name;
  }
  
  public void setName(String name) {
    this.name = name;
  }

  @Id 
  @GeneratedValue(strategy=GenerationType.TABLE, generator="ChangeLogEntryEntity")  
  @TableGenerator(name="ChangeLogEntryEntity", allocationSize=1)
  private Long id;
  
  @NotEmpty
  @Column (unique = true)
  private String name;
}
