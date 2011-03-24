package fi.pyramus.domainmodel.changelog;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.TableGenerator;

import org.hibernate.validator.constraints.NotEmpty;

import fi.pyramus.domainmodel.users.User;

@Entity
public class ChangeLogEntry {

  public Long getId() {
    return id;
  }
  
  public ChangeLogEntryType getType() {
    return type;
  }

  public void setType(ChangeLogEntryType type) {
    this.type = type;
  }
  
  public ChangeLogEntryEntity getEntity() {
    return entity;
  }

  public void setEntity(ChangeLogEntryEntity entity) {
    this.entity = entity;
  }

  public String getEntityId() {
    return entityId;
  }
  
  public void setEntityId(String entityId) {
    this.entityId = entityId;
  }
  
  public String getIp() {
    return ip;
  }
  
  public void setIp(String ip) {
    this.ip = ip;
  }

  public Date getTime() {
    return time;
  }

  public User getUser() {
    return user;
  }

  public void setTime(Date time) {
    this.time = time;
  }

  public void setUser(User user) {
    this.user = user;
  }

  @Id 
  @GeneratedValue(strategy=GenerationType.TABLE, generator="ChangeLogEntry")  
  @TableGenerator(name="ChangeLogEntry", allocationSize=1)
  private Long id;

  @Column (nullable = false)
  private ChangeLogEntryType type;

  @ManyToOne  
  @JoinColumn(name="entity")
  private ChangeLogEntryEntity entity;

  @NotEmpty
  private String entityId;
  
  private String ip;

  private Date time;

  @ManyToOne  
  @JoinColumn(name="user")
  private User user;
}
