package fi.pyramus.domainmodel.students;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.PersistenceException;
import javax.persistence.TableGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import org.hibernate.search.annotations.DocumentId;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.FullTextFilterDef;
import org.hibernate.search.annotations.FullTextFilterDefs;
import org.hibernate.search.annotations.Index;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.IndexedEmbedded;
import org.hibernate.search.annotations.Store;
import org.hibernate.validator.constraints.NotEmpty;

import fi.pyramus.domainmodel.users.User;
import fi.pyramus.persistence.search.filters.ArchivedStudentGroupFilterFactory;

@Entity
@Indexed
@FullTextFilterDefs (
  @FullTextFilterDef (
     name="ArchivedStudentGroup",
     impl=ArchivedStudentGroupFilterFactory.class
  )
)
public class StudentGroup {

  /**
   * Returns unique identifier for this StudentGroup
   * 
   * @return unique id of this StudentGroup
   */
  public Long getId() {
    return id;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  @Field (index = Index.TOKENIZED)
  public String getDescription() {
    return description;
  }

  public void setArchived(Boolean archived) {
    this.archived = archived;
  }

  public Boolean getArchived() {
    return archived;
  }

  @SuppressWarnings("unused")
  private void setStudents(Set<StudentGroupStudent> students) {
    this.students = students;
  }

  public Set<StudentGroupStudent> getStudents() {
    return students;
  }
  
  public void addStudent(StudentGroupStudent student) {
    if (students.contains(student))
      throw new PersistenceException("StudentGroup.addStudent - user already exists in list");

    if (student.getStudentGroup() != null)
      student.getStudentGroup().removeStudent(student);
    student.setStudentGroup(this);
    students.add(student);
  }
  
  public void removeStudent(StudentGroupStudent student) {
    if (!students.contains(student))
      throw new PersistenceException("StudentGroup.removeStudent - student doesn't exist in StudentGroup");
    
    student.setStudentGroup(null);
    students.remove(student);
  }

  @SuppressWarnings("unused")
  private void setUsers(Set<StudentGroupUser> users) {
    this.users = users;
  }

  public Set<StudentGroupUser> getUsers() {
    return users;
  }

  public void addUser(StudentGroupUser user) {
    if (users.contains(user))
      throw new PersistenceException("StudentGroup.addUser - user already exists in list");
    
    if (user.getStudentGroup() != null)
      user.getStudentGroup().removeUser(user);
    user.setStudentGroup(this);
    users.add(user);
  }
  
  public void removeUser(StudentGroupUser user) {
    if (!users.contains(user))
      throw new PersistenceException("StudentGroup.removeUser - user doesn't exist in StudentGroup");

    user.setStudentGroup(null);
    users.remove(user);
  }
  
  public void setBeginDate(Date beginDate) {
    this.beginDate = beginDate;
  }

  public Date getBeginDate() {
    return beginDate;
  }

  @Transient
  @Field (index = Index.UN_TOKENIZED, store = Store.YES)
  public String getNameSortable() {
    return name;
  }

  public void setCreator(User creator) {
    this.creator = creator;
  }

  public User getCreator() {
    return creator;
  }

  public void setLastModifier(User lastModifier) {
    this.lastModifier = lastModifier;
  }

  public User getLastModifier() {
    return lastModifier;
  }

  public void setCreated(Date created) {
    this.created = created;
  }

  public Date getCreated() {
    return created;
  }

  public void setLastModified(Date lastModified) {
    this.lastModified = lastModified;
  }

  public Date getLastModified() {
    return lastModified;
  }

  @Id 
  @GeneratedValue(strategy=GenerationType.TABLE, generator="StudentGroup")  
  @TableGenerator(name="StudentGroup", allocationSize=1)
  @DocumentId
  private Long id;

  @NotNull
  @Column (nullable = false)
  @NotEmpty
  @Field (index = Index.TOKENIZED)
  private String name;
  
  @Basic (fetch = FetchType.LAZY)
  @Column (length=1073741824)
  private String description;
  
  @Column
  @Field (index = Index.TOKENIZED)
  private Date beginDate;

  @NotNull
  @Column (nullable = false)
  @Field (index=Index.TOKENIZED)
  private Boolean archived = Boolean.FALSE;
  
  @ManyToOne 
  @JoinColumn(name="creator")
  private User creator;
  
  @NotNull
  @Column (updatable=false, nullable=false)
  @Temporal (value=TemporalType.TIMESTAMP)
  private Date created;
  
  @ManyToOne  
  @JoinColumn(name="lastModifier")
  private User lastModifier;

  @NotNull
  @Column (nullable=false)
  @Temporal (value=TemporalType.TIMESTAMP)
  private Date lastModified;

  @OneToMany
  @JoinColumn (name="studentGroup")
  @IndexedEmbedded
  private Set<StudentGroupUser> users = new HashSet<StudentGroupUser>();
  
  @OneToMany
  @JoinColumn (name="studentGroup")
  private Set<StudentGroupStudent> students = new HashSet<StudentGroupStudent>();
}
