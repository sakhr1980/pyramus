package fi.pyramus.domainmodel.courses;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.IndexColumn;
import org.hibernate.search.annotations.DateBridge;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.FullTextFilterDef;
import org.hibernate.search.annotations.FullTextFilterDefs;
import org.hibernate.search.annotations.Index;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.IndexedEmbedded;
import org.hibernate.search.annotations.Resolution;

import fi.pyramus.domainmodel.base.CourseBase;
import fi.pyramus.domainmodel.modules.Module;
import fi.pyramus.persistence.search.filters.ArchivedCourseFilterFactory;

@Entity
@Indexed
@PrimaryKeyJoinColumn(name="id")
@FullTextFilterDefs (
  @FullTextFilterDef (
     name="ArchivedCourse",
     impl=ArchivedCourseFilterFactory.class
  )
)
public class Course extends CourseBase {

  public void setModule(Module module) {
    this.module = module;
  }

  public Module getModule() {
    return module;
  }

  public List<CourseComponent> getCourseComponents() {
    return courseComponents;
  }
  
  @SuppressWarnings("unused")
  private void setCourseComponents(List<CourseComponent> courseComponents) {
    this.courseComponents = courseComponents;
  }
  
  public void addCourseComponent(CourseComponent courseComponent) {
    if (courseComponent.getCourse() != null)
      courseComponent.getCourse().getCourseComponents().remove(courseComponent);
    courseComponent.setCourse(this);
    courseComponents.add(courseComponent);
  }
  
  public void removeCourseComponent(CourseComponent courseComponent) {
    courseComponent.setCourse(null);
    this.courseComponents.remove(courseComponent);
  } 
  
  public void setCourseUsers(List<CourseUser> courseUsers) {
    this.courseUsers = courseUsers;
  }

  public List<CourseUser> getCourseUsers() {
    return courseUsers;
  }

  public void addCourseUser(CourseUser courseUser) {
    if (courseUser.getCourse() != null)
      courseUser.getCourse().getCourseUsers().remove(courseUser);
    courseUser.setCourse(this);
    courseUsers.add(courseUser);
  }
  
  public void removeCourseUser(CourseUser courseUser) {
    courseUser.setCourse(null);
    this.courseUsers.remove(courseUser);
  } 

  public void setStudentCourseResources(List<StudentCourseResource> studentCourseResources) {
    this.studentCourseResources = studentCourseResources;
  }
  
  public List<CourseStudent> getCourseStudents() {
    return courseStudents;
  }
  
  @SuppressWarnings("unused")
  private void setCourseStudents(List<CourseStudent> courseStudents) {
    this.courseStudents = courseStudents;
  }
  
  public void addCourseStudent(CourseStudent courseStudent) {
    if (courseStudent.getCourse() != null)
      courseStudent.getCourse().removeCourseStudent(courseStudent);
    courseStudent.setCourse(this);
    courseStudents.add(courseStudent);
  }
  
  public void removeCourseStudent(CourseStudent courseStudent) {
    courseStudent.setCourse(null);
    courseStudents.remove(courseStudent);
  }

  public List<StudentCourseResource> getStudentCourseResources() {
    return studentCourseResources;
  }

  public void setBasicCourseResources(List<BasicCourseResource> basicCourseResources) {
    this.basicCourseResources = basicCourseResources;
  }

  public List<BasicCourseResource> getBasicCourseResources() {
    return basicCourseResources;
  }

  public void setGradeCourseResources(List<GradeCourseResource> gradeCourseResources) {
    this.gradeCourseResources = gradeCourseResources;
  }

  public List<GradeCourseResource> getGradeCourseResources() {
    return gradeCourseResources;
  }

  public void setOtherCosts(List<OtherCost> otherCosts) {
    this.otherCosts = otherCosts;
  }

  public List<OtherCost> getOtherCosts() {
    return otherCosts;
  }
  
  public void addBasicCourseResource(BasicCourseResource basicCourseResource) {
    if (basicCourseResource.getCourse() != null)
      basicCourseResource.getCourse().getBasicCourseResources().remove(basicCourseResource);
    basicCourseResource.setCourse(this);
    basicCourseResources.add(basicCourseResource);
  }

  public void removeBasicCourseResource(BasicCourseResource basicCourseResource) {
    basicCourseResource.setCourse(null);
    basicCourseResources.remove(basicCourseResource);
  } 

  public void addStudentCourseResource(StudentCourseResource studentCourseResource) {
    if (studentCourseResource.getCourse() != null)
      studentCourseResource.getCourse().getStudentCourseResources().remove(studentCourseResource);
    studentCourseResource.setCourse(this);
    studentCourseResources.add(studentCourseResource);
  }

  public void removeStudentCourseResource(StudentCourseResource studentCourseResource) {
    studentCourseResource.setCourse(null);
    studentCourseResources.remove(studentCourseResource);
  } 

  public void addGradeCourseResource(GradeCourseResource gradeCourseResource) {
    if (gradeCourseResource.getCourse() != null)
      gradeCourseResource.getCourse().getGradeCourseResources().remove(gradeCourseResource);
    gradeCourseResource.setCourse(this);
    gradeCourseResources.add(gradeCourseResource);
  }

  public void removeGradeCourseResource(GradeCourseResource gradeCourseResource) {
    gradeCourseResource.setCourse(null);
    gradeCourseResources.remove(gradeCourseResource);
  } 

  public void addOtherCost(OtherCost otherCost) {
    if (otherCost.getCourse() != null)
      otherCost.getCourse().getOtherCosts().remove(otherCost);
    otherCost.setCourse(this);
    otherCosts.add(otherCost);
  }

  public void removeOtherCost(OtherCost otherCost) {
    otherCost.setCourse(null);
    otherCosts.remove(otherCost);
  } 

  public void setBeginDate(Date beginDate) {
    this.beginDate = beginDate;
  }

  public Date getBeginDate() {
    return beginDate;
  }

  public void setEndDate(Date endDate) {
    this.endDate = endDate;
  }

  public Date getEndDate() {
    return endDate;
  }
  
  public Integer getStudentCount() {
    return courseStudents.size();
  }
  
  public String getNameExtension() {
  	return nameExtension;
  }
  
  public void setNameExtension(String nameExtension) {
	  this.nameExtension = nameExtension;
  }

  public void setLocalTeachingDays(Double localTeachingDays) {
    this.localTeachingDays = localTeachingDays;
  }

  public Double getLocalTeachingDays() {
    return localTeachingDays;
  }

  public void setState(CourseState state) {
    this.state = state;
  }

  public CourseState getState() {
    return state;
  }

  public void setTeachingHours(Integer teachingHours) {
    this.teachingHours = teachingHours;
  }

  public Integer getTeachingHours() {
    return teachingHours;
  }

  public void setDistanceTeachingDays(Double distanceTeachingDays) {
    this.distanceTeachingDays = distanceTeachingDays;
  }

  public Double getDistanceTeachingDays() {
    return distanceTeachingDays;
  }

  @ManyToOne
  @JoinColumn(name="module")
  private Module module;
  
  @ManyToOne
  @JoinColumn(name="state")
  @IndexedEmbedded
  private CourseState state;
  
  @Field (index=Index.TOKENIZED)
  private String nameExtension;
  
  @Temporal (value=TemporalType.TIMESTAMP)
  @Field (index = Index.UN_TOKENIZED)
  @DateBridge (resolution = Resolution.DAY)
  private Date beginDate;
  
  @Temporal (value=TemporalType.TIMESTAMP)
  @Field (index = Index.UN_TOKENIZED)
  @DateBridge (resolution = Resolution.DAY)
  private Date endDate;
  
  private Double distanceTeachingDays;
  
  private Double localTeachingDays;
  
  private Integer teachingHours;
  
  @OneToMany (cascade = CascadeType.ALL, orphanRemoval = true)
  @IndexColumn (name = "indexColumn")
  @JoinColumn (name="course")
  @IndexedEmbedded
  private List<CourseComponent> courseComponents = new Vector<CourseComponent>();
  
  @OneToMany (cascade = CascadeType.ALL, orphanRemoval = true)
  @JoinColumn (name="course")
  @IndexedEmbedded
  private List<CourseUser> courseUsers = new Vector<CourseUser>();

  @OneToMany (cascade = CascadeType.ALL, orphanRemoval = true)
  @JoinColumn (name="course")
  private List<CourseStudent> courseStudents = new ArrayList<CourseStudent>();

  @OneToMany (cascade = CascadeType.ALL, orphanRemoval = true)
  @JoinColumn (name="course")
  private List<BasicCourseResource> basicCourseResources = new Vector<BasicCourseResource>();

  @OneToMany (cascade = CascadeType.ALL, orphanRemoval = true)
  @JoinColumn (name="course")
  private List<StudentCourseResource> studentCourseResources = new Vector<StudentCourseResource>();

  @OneToMany (cascade = CascadeType.ALL, orphanRemoval = true)
  @JoinColumn (name="course")
  private List<GradeCourseResource> gradeCourseResources = new Vector<GradeCourseResource>();

  @OneToMany (cascade = CascadeType.ALL, orphanRemoval = true)
  @JoinColumn (name="course")
  private List<OtherCost> otherCosts = new Vector<OtherCost>();

}