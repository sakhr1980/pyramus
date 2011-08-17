package fi.pyramus.services.entities.students;

import java.util.Date;

public class AbstractStudentEntity {
  
  public AbstractStudentEntity(Long id, Date birthday, String socialSecurityNumber, String sex) {
    this.id = id;
    this.birthday = birthday;
    this.socialSecurityNumber = socialSecurityNumber;
    this.sex = sex;
  }
  
  public Long getId() {
    return id;
  }
  
  public Date getBirthday() {
    return birthday;
  }
  
  public String getSocialSecurityNumber() {
    return socialSecurityNumber;
  }
  
  public String getSex() {
    return sex;
  }

  private Long id;
  private Date birthday;
  private String socialSecurityNumber;  
  private String sex;
}
