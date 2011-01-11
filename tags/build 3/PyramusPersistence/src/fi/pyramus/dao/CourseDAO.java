package fi.pyramus.dao;

import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;

import org.apache.commons.lang.StringUtils;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.apache.lucene.util.Version;
import org.hibernate.Session;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.search.FullTextQuery;
import org.hibernate.search.FullTextSession;
import org.hibernate.search.Search;

import fi.pyramus.domainmodel.base.CourseBase;
import fi.pyramus.domainmodel.base.CourseBaseVariable;
import fi.pyramus.domainmodel.base.CourseBaseVariableKey;
import fi.pyramus.domainmodel.base.CourseEducationSubtype;
import fi.pyramus.domainmodel.base.CourseEducationType;
import fi.pyramus.domainmodel.base.EducationSubtype;
import fi.pyramus.domainmodel.base.EducationType;
import fi.pyramus.domainmodel.base.EducationalTimeUnit;
import fi.pyramus.domainmodel.base.Subject;
import fi.pyramus.domainmodel.base.Tag;
import fi.pyramus.domainmodel.courses.BasicCourseResource;
import fi.pyramus.domainmodel.courses.Course;
import fi.pyramus.domainmodel.courses.CourseComponent;
import fi.pyramus.domainmodel.courses.CourseComponentResource;
import fi.pyramus.domainmodel.courses.CourseEnrolmentType;
import fi.pyramus.domainmodel.courses.CourseParticipationType;
import fi.pyramus.domainmodel.courses.CourseState;
import fi.pyramus.domainmodel.courses.CourseStudent;
import fi.pyramus.domainmodel.courses.CourseUser;
import fi.pyramus.domainmodel.courses.CourseUserRole;
import fi.pyramus.domainmodel.courses.GradeCourseResource;
import fi.pyramus.domainmodel.courses.OtherCost;
import fi.pyramus.domainmodel.courses.StudentCourseResource;
import fi.pyramus.domainmodel.modules.Module;
import fi.pyramus.domainmodel.resources.Resource;
import fi.pyramus.domainmodel.students.Student;
import fi.pyramus.domainmodel.users.User;
import fi.pyramus.persistence.search.SearchResult;
import fi.pyramus.persistence.search.SearchTimeFilterMode;
import fi.pyramus.persistence.usertypes.CourseOptionality;
import fi.pyramus.persistence.usertypes.MonetaryAmount;

/**
 * The Data Access Object for course related operations.  
 */
public class CourseDAO extends PyramusDAO {

  /**
   * Creates a new course into the database.
   * 
   * @param module The module of the course
   * @param name Course name
   * @param subject Course subject
   * @param courseNumber Course number
   * @param beginDate Course begin date
   * @param endDate Course end date
   * @param courseLength Course length
   * @param description Course description
   * @param creatingUser Course owner
   * 
   * @return The created course
   */
  public Course createCourse(Module module, String name, String nameExtension, CourseState state, Subject subject,
      Integer courseNumber, Date beginDate, Date endDate, Double courseLength,
      EducationalTimeUnit courseLengthTimeUnit, Double distanceTeachingDays, Double localTeachingDays, Double teachingHours, 
      Double planningHours, Double assessingHours, String description, User creatingUser) {
    Session s = getHibernateSession();

    Date now = new Date(System.currentTimeMillis());

    Course course = new Course();
    course.setModule(module);
    course.setName(name);
    course.setState(state);
    course.setNameExtension(nameExtension);
    course.setDescription(description);
    course.setSubject(subject);
    course.setCourseNumber(courseNumber);
    course.setBeginDate(beginDate);
    course.setEndDate(endDate);
    course.getCourseLength().setUnit(courseLengthTimeUnit);
    course.getCourseLength().setUnits(courseLength);
    course.setLocalTeachingDays(localTeachingDays);
    course.setDistanceTeachingDays(distanceTeachingDays);
    course.setTeachingHours(teachingHours);    
    course.setPlanningHours(planningHours);
    course.setAssessingHours(assessingHours);
    course.setCreator(creatingUser);
    course.setCreated(now);
    course.setLastModifier(creatingUser);
    course.setLastModified(now);

    s.save(course);

    return course;
  }

  /**
   * Updates a course to the database.
   * 
   * @param course The course to be updated
   * @param name Course name
   * @param subject Course subject
   * @param courseNumber Course number
   * @param beginDate Course begin date
   * @param endDate Course end date
   * @param courseLength Course length 
   * @param courseLengthTimeUnit Course length unit
   * @param description Course description
   * @param user The user making the update, stored as the last modifier of the course
   */
  public void updateCourse(Course course, String name, String nameExtension, CourseState courseState, Subject subject,
      Integer courseNumber, Date beginDate, Date endDate, Double courseLength,
      EducationalTimeUnit courseLengthTimeUnit, Double distanceTeachingDays, Double localTeachingDays, Double teachingHours, 
      Double planningHours, Double assessingHours, String description, User user) {
    Session s = getHibernateSession();
    Date now = new Date(System.currentTimeMillis());
    course.setName(name);
    course.setNameExtension(nameExtension);
    course.setState(courseState);
    course.setDescription(description);
    course.setSubject(subject);
    course.setCourseNumber(courseNumber);
    course.setBeginDate(beginDate);
    course.setEndDate(endDate);
    course.getCourseLength().setUnit(courseLengthTimeUnit);
    course.getCourseLength().setUnits(courseLength);
    course.setDistanceTeachingDays(distanceTeachingDays);
    course.setLocalTeachingDays(localTeachingDays);
    course.setTeachingHours(teachingHours);
    course.setPlanningHours(planningHours);
    course.setAssessingHours(assessingHours);
    course.setLastModifier(user);
    course.setLastModified(now);
    
    s.saveOrUpdate(course);
  }
  
  public Course setCourseTags(Course course, Set<Tag> tags) {
    EntityManager entityManager = getEntityManager();
    
    course.setTags(tags);
    
    entityManager.persist(course);
    
    return course;
  }

  private void updateCourseBaseVariable(CourseBaseVariable courseBaseVariable, String value) {
    Session s = getHibernateSession();
    courseBaseVariable.setValue(value);
    s.saveOrUpdate(courseBaseVariable);
  }

  /**
   * Archives a course.
   * 
   * @param course The course to be archived
   */
  public void archiveCourse(Course course) {
    Session s = getHibernateSession();
    course.setArchived(Boolean.TRUE);
    s.saveOrUpdate(course);
  }

  /**
   * Archives a course component.
   * 
   * @param courseComponent The course component to be archived
   */
  public void archiveCourseComponent(CourseComponent courseComponent) {
    Session s = getHibernateSession();
    courseComponent.setArchived(Boolean.TRUE);
    s.saveOrUpdate(courseComponent);
  }
  
  public void unarchiveCourseComponent(CourseComponent courseComponent) {
    Session s = getHibernateSession();
    courseComponent.setArchived(Boolean.FALSE);
    s.saveOrUpdate(courseComponent);
  }
  
  /**
   * Unarchives a course.
   * 
   * @param course The course to be unarchived
   */
  public void unarchiveCourse(Course course) {
    Session s = getHibernateSession();
    course.setArchived(Boolean.FALSE);
    s.saveOrUpdate(course);
  }

  /**
   * Deletes a course.
   * 
   * @param course The course to be deleted
   */
  public void deleteCourse(Course course) {
    Session s = getHibernateSession();
    s.delete(course);
  }

  private CourseBaseVariable createCourseBaseVariable(Course course, CourseBaseVariableKey key, String value) {
    Session s = getHibernateSession();

    CourseBaseVariable courseBaseVariable = new CourseBaseVariable();
    courseBaseVariable.setCourseBase(course);
    courseBaseVariable.setKey(key);
    courseBaseVariable.setValue(value);
    s.saveOrUpdate(courseBaseVariable);

    course.getVariables().add(courseBaseVariable);
    s.saveOrUpdate(course);

    return courseBaseVariable;
  }

  /**
   * Creates a course component to the database.
   * 
   * @param course The course
   * @param length Component length
   * @param name Component name
   * @param description Component description
   * 
   * @return The created course component
   */
  public CourseComponent createCourseComponent(Course course, Double componentLength,
      EducationalTimeUnit componentLengthTimeUnit, String name, String description) {
    Session s = getHibernateSession();

    CourseComponent courseComponent = new CourseComponent();
    courseComponent.setCourse(course);
    courseComponent.setName(name);
    courseComponent.getLength().setUnit(componentLengthTimeUnit);
    courseComponent.getLength().setUnits(componentLength);
    courseComponent.setDescription(description);
    s.save(courseComponent);

    course.addCourseComponent(courseComponent);
    s.saveOrUpdate(course);

    return courseComponent;
  }

  /**
   * Updates a course component to the database.
   * 
   * @param courseComponent The course component to be updated
   * @param length Component length
   * @param name Component name
   * @param description Component description
   */
  public CourseComponent updateCourseComponent(CourseComponent courseComponent, Double length, EducationalTimeUnit lenghtTimeUnit,
      String name, String description) {
    Session s = getHibernateSession();

    courseComponent.setName(name);
    courseComponent.getLength().setUnit(lenghtTimeUnit);
    courseComponent.getLength().setUnits(length);
    courseComponent.setDescription(description);

    s.saveOrUpdate(courseComponent);
    
    return courseComponent;
  }

  /**
   * Returns the course component corresponding to the given identifier.
   * 
   * @param courseComponentId Course component identifier
   * 
   * @return The course component corresponding to the given identifier
   */
  public CourseComponent getCourseComponent(Long courseComponentId) {
    Session s = getHibernateSession();
    return (CourseComponent) s.load(CourseComponent.class, courseComponentId);
  }

  /**
   * Deletes the given course component from the database.
   * 
   * @param courseComponent The course component to be deleted
   */
  public void deleteCourseComponent(CourseComponent courseComponent) {
    Session s = getHibernateSession();
    if (courseComponent.getCourse() != null)
      courseComponent.getCourse().removeCourseComponent(courseComponent);
    s.delete(courseComponent);
  }

  /**
   * Creates a basic course resource to the database.
   * 
   * @param course The course
   * @param resource The resource
   * @param hours Resource hours
   * @param hourlyCost Resource hourly cost
   * @param units Resource units
   * @param unitCost Resource unit cost
   * 
   * @return The created basic course resource
   */
  public BasicCourseResource createBasicCourseResource(Course course, Resource resource, Double hours,
      MonetaryAmount hourlyCost, Integer units, MonetaryAmount unitCost) {
    Session s = getHibernateSession();

    BasicCourseResource basicCourseResource = new BasicCourseResource(course, resource);
    basicCourseResource.setHours(hours);
    basicCourseResource.setHourlyCost(hourlyCost);
    basicCourseResource.setUnits(units);
    basicCourseResource.setUnitCost(unitCost);
    s.save(basicCourseResource);

    course.getBasicCourseResources().add(basicCourseResource);
    s.saveOrUpdate(course);
    return basicCourseResource;
  }

  /**
   * Updates the given basic course resource to the database.
   * 
   * @param basicCourseResource The basic course resource to be updated
   * @param hours Resource hours
   * @param hourlyCost Resource hourly cost
   * @param units Resource units
   * @param unitCost Resource unit cost
   */
  public void updateBasicCourseResource(BasicCourseResource basicCourseResource, Double hours,
      MonetaryAmount hourlyCost, Integer units, MonetaryAmount unitCost) {
    Session s = getHibernateSession();
    basicCourseResource.setHours(hours);
    basicCourseResource.setHourlyCost(hourlyCost);
    basicCourseResource.setUnits(units);
    basicCourseResource.setUnitCost(unitCost);
    s.saveOrUpdate(basicCourseResource);
  }

  /**
   * Returns a list of all basic course resources in the course corresponding to the given
   * identifier.
   * 
   * @param courseId The course identifier
   * 
   * @return A list of all basic course resources in the course corresponding to the given
   * identifier
   */
  @SuppressWarnings("unchecked")
  public List<BasicCourseResource> listBasicCourseResources(Long courseId) {
    Session s = getHibernateSession();
    return s.createCriteria(BasicCourseResource.class).add(Restrictions.eq("course", getCourse(courseId))).list();
  }

  /**
   * Returns the basic course resource corresponding to the given identifier.
   * 
   * @param basicCourseResourceId Basic course resource identifier
   * 
   * @return The basic course resource corresponding to the given identifier
   */
  public BasicCourseResource getBasicCourseResource(Long basicCourseResourceId) {
    Session s = getHibernateSession();
    return (BasicCourseResource) s.load(BasicCourseResource.class, basicCourseResourceId);
  }

  /**
   * Deletes the given basic course resource from the database.
   * 
   * @param basicCourseResource The basic course resource to be deleted
   */
  public void deleteBasicCourseResource(BasicCourseResource basicCourseResource) {
    Session s = getHibernateSession();
    if (basicCourseResource.getCourse() != null)
      basicCourseResource.getCourse().removeBasicCourseResource(basicCourseResource);
    s.delete(basicCourseResource);
  }

  /**
   * Creates a student course resource to the database.
   * 
   * @param course The course
   * @param resource The resource
   * @param hours Resource hours
   * @param hourlyCost Resource hourly cost
   * @param unitCost Resource unit cost
   * 
   * @return The created student course resource
   */
  public StudentCourseResource createStudentCourseResource(Course course, Resource resource, Double hours,
      MonetaryAmount hourlyCost, MonetaryAmount unitCost) {
    Session s = getHibernateSession();

    StudentCourseResource studentCourseResource = new StudentCourseResource(course, resource);
    studentCourseResource.setHours(hours);
    studentCourseResource.setHourlyCost(hourlyCost);
    studentCourseResource.setUnitCost(unitCost);
    s.save(studentCourseResource);

    course.getStudentCourseResources().add(studentCourseResource);
    s.saveOrUpdate(course);
    return studentCourseResource;
  }

  /**
   * Updates the given student course resource to the database.
   * 
   * @param studentCourseResource The student course resource to be updated
   * @param hours Resource hours
   * @param hourlyCost Resource hourly cost
   * @param unitCost Resource unit cost
   */
  public void updateStudentCourseResource(StudentCourseResource studentCourseResource, Double hours,
      MonetaryAmount hourlyCost, MonetaryAmount unitCost) {
    Session s = getHibernateSession();

    studentCourseResource.setHours(hours);
    studentCourseResource.setHourlyCost(hourlyCost);
    studentCourseResource.setUnitCost(unitCost);
    s.saveOrUpdate(studentCourseResource);
  }

  @SuppressWarnings("unchecked")
  public List<StudentCourseResource> listStudentCourseResources(Long courseId) {
    Session s = getHibernateSession();
    return s.createCriteria(StudentCourseResource.class).add(Restrictions.eq("course", getCourse(courseId))).list();
  }

  /**
   * Returns the student course resource corresponding to the given identifier.
   * 
   * @param studentCourseResourceId Student course resource identifier
   * 
   * @return The student course resource corresponding to the given identifier
   */
  public StudentCourseResource getStudentCourseResource(Long studentCourseResourceId) {
    Session s = getHibernateSession();
    return (StudentCourseResource) s.load(StudentCourseResource.class, studentCourseResourceId);
  }

  /**
   * Deletes the given student course resource from the database.
   * 
   * @param studentCourseResource The student course resource to be deleted
   */
  public void deleteStudentCourseResource(StudentCourseResource studentCourseResource) {
    Session s = getHibernateSession();
    if (studentCourseResource.getCourse() != null)
      studentCourseResource.getCourse().removeStudentCourseResource(studentCourseResource);
    s.delete(studentCourseResource);
  }

  /**
   * Creates a grade course resource to the database.
   * 
   * @param course The course
   * @param resource The resource
   * @param hours Resource hours
   * @param hourlyCost Resource hourly cost
   * @param unitCost Resource unit cost
   * 
   * @return The created grade course resource
   */
  public GradeCourseResource createGradeCourseResource(Course course, Resource resource, Double hours,
      MonetaryAmount hourlyCost, MonetaryAmount unitCost) {
    Session s = getHibernateSession();

    GradeCourseResource gradeCourseResource = new GradeCourseResource(course, resource);
    gradeCourseResource.setHours(hours);
    gradeCourseResource.setHourlyCost(hourlyCost);
    gradeCourseResource.setUnitCost(unitCost);
    s.save(gradeCourseResource);

    course.getGradeCourseResources().add(gradeCourseResource);
    s.saveOrUpdate(course);
    return gradeCourseResource;
  }

  /**
   * Updates the given grade course resource to the database.
   * 
   * @param gradeCourseResource The grade course resource to be updated
   * @param hours Resource hours
   * @param hourlyCost Resource hourly cost
   * @param unitCost Resource unit cost
   */
  public void updateGradeCourseResource(GradeCourseResource gradeCourseResource, Double hours,
      MonetaryAmount hourlyCost, MonetaryAmount unitCost) {
    Session s = getHibernateSession();
    gradeCourseResource.setHours(hours);
    gradeCourseResource.setHourlyCost(hourlyCost);
    gradeCourseResource.setUnitCost(unitCost);
    s.saveOrUpdate(gradeCourseResource);
  }

  @SuppressWarnings("unchecked")
  public List<GradeCourseResource> listGradeCourseResources(Long courseId) {
    Session s = getHibernateSession();
    return s.createCriteria(GradeCourseResource.class).add(Restrictions.eq("course", getCourse(courseId))).list();
  }

  /**
   * Returns the grade course resource corresponding to the given identifier.
   * 
   * @param gradeCourseResourceId Grade course resource identifier
   * 
   * @return The grade course resource corresponding to the given identifier
   */
  public GradeCourseResource getGradeCourseResource(Long gradeCourseResourceId) {
    Session s = getHibernateSession();
    return (GradeCourseResource) s.load(GradeCourseResource.class, gradeCourseResourceId);
  }

  /**
   * Deletes the given grade course resource from the database.
   * 
   * @param gradeCourseResource The grade course resource to be deleted
   */
  public void deleteGradeCourseResource(GradeCourseResource gradeCourseResource) {
    Session s = getHibernateSession();
    if (gradeCourseResource.getCourse() != null)
      gradeCourseResource.getCourse().removeGradeCourseResource(gradeCourseResource);
    s.delete(gradeCourseResource);
  }

  /**
   * Creates an other cost to the database.
   * 
   * @param course The course the other cost belongs to
   * @param name Other cost name
   * @param cost Other cost value
   * 
   * @return The created other cost
   */
  public OtherCost createOtherCost(Course course, String name, MonetaryAmount cost) {
    Session s = getHibernateSession();

    OtherCost otherCost = new OtherCost(course);
    otherCost.setName(name);
    otherCost.setCost(cost);
    s.save(otherCost);

    course.getOtherCosts().add(otherCost);
    s.saveOrUpdate(course);
    return otherCost;
  }

  /**
   * Updates an other cost to the database.
   * 
   * @param otherCost The other cost to be updated
   * @param name Other cost name
   * @param cost Other cost value
   */
  public void updateOtherCost(OtherCost otherCost, String name, MonetaryAmount cost) {
    Session s = getHibernateSession();
    otherCost.setName(name);
    otherCost.setCost(cost);
    s.saveOrUpdate(otherCost);
  }

  /**
   * Returns the other cost corresponding to the given identifier.
   * 
   * @param otherCostId Other cost identifier
   * 
   * @return The other cost corresponding to the given identifier
   */
  public OtherCost getOtherCost(Long otherCostId) {
    Session s = getHibernateSession();
    return (OtherCost) s.load(OtherCost.class, otherCostId);
  }

  /**
   * Returns a list of all other costs in the course corresponding to the given identifier.
   * 
   * @param courseId The course identifier
   * 
   * @return A list of all other costs in the course corresponding to the given identifier
   */
  @SuppressWarnings("unchecked")
  public List<OtherCost> listOtherCosts(Long courseId) {
    Session s = getHibernateSession();
    return s.createCriteria(OtherCost.class).add(Restrictions.eq("course", getCourse(courseId))).list();
  }

  /**
   * Deletes the given other cost from the database.
   * 
   * @param otherCost The other cost to be deleted
   */
  public void deleteOtherCost(OtherCost otherCost) {
    Session s = getHibernateSession();
    if (otherCost.getCourse() != null)
      otherCost.getCourse().removeOtherCost(otherCost);
    s.delete(otherCost);
  }

  /**
   * Returns the course education type corresponding to the given identifier.
   * 
   * @param courseEducationTypeId Course education type identifier
   * 
   * @return The course education type corresponding to the given identifier
   */
  public CourseEducationType getCourseEducationType(Long courseEducationTypeId) {
    Session s = getHibernateSession();
    return (CourseEducationType) s.load(CourseEducationType.class, courseEducationTypeId);
  }

  public void removeCourseEducationType(CourseEducationType courseEducationType) {
    Session s = getHibernateSession();
    CourseBase courseBase = courseEducationType.getCourseBase();
    courseBase.removeCourseEducationType(courseEducationType);
    s.saveOrUpdate(courseBase);
  }

  public CourseEducationType addCourseEducationType(CourseBase courseBase, EducationType educationType) {
    Session s = getHibernateSession();
    CourseEducationType courseEducationType = new CourseEducationType(educationType);
    s.saveOrUpdate(courseEducationType);
    courseBase.addCourseEducationType(courseEducationType);
    s.saveOrUpdate(courseBase);
    return courseEducationType;
  }

  public CourseEducationSubtype addCourseEducationSubtype(CourseEducationType courseEducationType,
      EducationSubtype educationSubtype) {
    Session s = getHibernateSession();
    CourseEducationSubtype courseEducationSubtype = new CourseEducationSubtype(educationSubtype);
    s.saveOrUpdate(courseEducationSubtype);
    courseEducationType.addSubtype(courseEducationSubtype);
    s.saveOrUpdate(courseEducationType);
    return courseEducationSubtype;
  }

  /**
   * Returns a list of all courses in the database.
   * 
   * @return A list of all courses in the database
   */
  @SuppressWarnings("unchecked")
  public List<Course> listCourses() {
    Session s = getHibernateSession();
    return s.createCriteria(Course.class).add(Restrictions.eq("archived", Boolean.FALSE)).list();
  }
  
  /**
   * Returns a list of all course user roles in the system.
   * 
   * @return A list of all course user roles in the system
   */
  @SuppressWarnings("unchecked")
  public List<CourseUserRole> listCourseUserRoles() {
    Session s = getHibernateSession();
    return s.createCriteria(CourseUserRole.class).list();
  }
  
  public void updateCourseUser(CourseUser courseUser, User user, CourseUserRole role) {
    Session s = getHibernateSession();
    courseUser.setUser(user);
    courseUser.setRole(role);
    s.saveOrUpdate(courseUser);
  }

  @SuppressWarnings("unchecked")
  public List<CourseState> listCourseStates() {
    Session s = getHibernateSession();
    return s.createCriteria(CourseState.class).add(Restrictions.eq("archived", Boolean.FALSE)).list();
  }
  public void deleteCourseUser(CourseUser courseUser) {
    Session s = getHibernateSession();
    Course course = courseUser.getCourse();
    course.removeCourseUser(courseUser);
    s.delete(courseUser);
  }

  /**
   * Returns the course corresponding to the given identifier.
   * 
   * @param courseId The course identifier
   * 
   * @return The course corresponding to the given identifier
   */
  public Course getCourse(Long courseId) {
    Session s = getHibernateSession();
    return (Course) s.load(Course.class, courseId);
  }

  public CourseEnrolmentType createCourseEnrolmentType(String name) {
    Session s = getHibernateSession();

    CourseEnrolmentType courseEnrolmentType = new CourseEnrolmentType();
    courseEnrolmentType.setName(name);

    s.save(courseEnrolmentType);

    return courseEnrolmentType;
  }

  /**
   * Deletes the given course enrolment type from the database.
   * 
   * @param courseEnrolmentType The course enrolment type to be deleted
   */
  public void deleteCourseEnrolmentType(CourseEnrolmentType courseEnrolmentType) {
    Session s = getHibernateSession();
    s.delete(courseEnrolmentType);
  }

  /**
   * Returns the course enrolment type corresponding to the given identifier.
   * 
   * @param courseEnrolmentTypeId The course enrolment type identifier
   * 
   * @return The course enrolment type corresponding to the given identifier
   */
  public CourseEnrolmentType getCourseEnrolmentType(Long courseEnrolmentTypeId) {
    Session s = getHibernateSession();
    return (CourseEnrolmentType) s.load(CourseEnrolmentType.class, courseEnrolmentTypeId);
  }

  public CourseParticipationType createCourseParticipationType(String name) {
    Session s = getHibernateSession();

    CourseParticipationType courseParticipationType = new CourseParticipationType();
    courseParticipationType.setName(name);

    s.save(courseParticipationType);

    return courseParticipationType;
  }

  public CourseParticipationType updateCourseParticipationType(CourseParticipationType courseParticipationType, String name) {
    Session s = getHibernateSession();

    courseParticipationType.setName(name);

    s.saveOrUpdate(courseParticipationType);

    return courseParticipationType;
  }

  public CourseState createCourseState(String name) {
    Session s = getHibernateSession();
    CourseState courseState = new CourseState();
    courseState.setName(name);
    s.save(courseState);
    return courseState;
  }

  public CourseState updateCourseState(CourseState courseState, String name) {
    Session s = getHibernateSession();
    
    courseState.setName(name);
    s.saveOrUpdate(courseState);
    
    return courseState;
  }

  public CourseUser createCourseUser(Course course, User user, CourseUserRole role) {
    Session s = getHibernateSession();

    CourseUser courseUser = new CourseUser();
    courseUser.setCourse(course);
    courseUser.setUser(user);
    courseUser.setRole(role);
    s.save(courseUser);

    course.addCourseUser(courseUser);
    s.saveOrUpdate(course);

    return courseUser;
  }

  public CourseParticipationType getCourseParticipationType(Long courseParticipationTypeId) {
    Session s = getHibernateSession();
    return (CourseParticipationType) s.load(CourseParticipationType.class, courseParticipationTypeId);
  }

  public CourseState getCourseState(Long courseStateId) {
    Session s = getHibernateSession();
    return (CourseState) s.load(CourseState.class, courseStateId);
  }

  public CourseUser getCourseUser(Long courseUserId) {
    Session s = getHibernateSession();
    return (CourseUser) s.load(CourseUser.class, courseUserId);
  }

  public CourseUserRole getCourseUserRole(Long courseUserRoleId) {
    Session s = getHibernateSession();
    return (CourseUserRole) s.load(CourseUserRole.class, courseUserRoleId);
  }

  /**
   * Deletes the given course participation type from the database.
   * 
   * @param courseParticipationType The course participation type to be deleted
   */
  public void deleteCourseParticipationType(CourseParticipationType courseParticipationType) {
    Session s = getHibernateSession();
    s.delete(courseParticipationType);
  }

  @SuppressWarnings("unchecked")
  public List<CourseComponent> listCourseComponents(Course course) {
    Session s = getHibernateSession();
    return s.createCriteria(CourseComponent.class).add(Restrictions.eq("course", course)).add(
        Restrictions.eq("archived", Boolean.FALSE)).list();
  }

  @SuppressWarnings("unchecked")
  public List<CourseEnrolmentType> listCourseEnrolmentTypes() {
    Session s = getHibernateSession();
    return s.createCriteria(CourseEnrolmentType.class).list();
  }

  @SuppressWarnings("unchecked")
  public List<CourseParticipationType> listCourseParticipationTypes() {
    Session s = getHibernateSession();
    List<CourseParticipationType> courseParticipationTypes = s.createCriteria(CourseParticipationType.class).add(
        Restrictions.eq("archived", Boolean.FALSE)).list();
    Collections.sort(courseParticipationTypes, new Comparator<CourseParticipationType>() {
      public int compare(CourseParticipationType o1, CourseParticipationType o2) {
        return o1.getIndexColumn() == null ? -1 : o2.getIndexColumn() == null ? 1 : o1.getIndexColumn().compareTo(o2.getIndexColumn());
      }
    });
    return courseParticipationTypes;
  }

  @SuppressWarnings("unchecked")
  public List<CourseUser> listCourseUsers(Course course) {
    Session s = getHibernateSession();
    return s.createCriteria(CourseUser.class).add(Restrictions.eq("course", course)).list();
  }

  @SuppressWarnings("unchecked")
  public SearchResult<Course> searchCoursesBasic(int resultsPerPage, int page, String text, boolean filterArchived) {
    int firstResult = page * resultsPerPage;

    StringBuilder queryBuilder = new StringBuilder();

    if (!StringUtils.isBlank(text)) {
      queryBuilder.append("+(");
      addTokenizedSearchCriteria(queryBuilder, "name", text, false);
      addTokenizedSearchCriteria(queryBuilder, "description", text, false);
      addTokenizedSearchCriteria(queryBuilder, "nameExtension", text, false);
      addTokenizedSearchCriteria(queryBuilder, "courseComponents.name", text, false);
      addTokenizedSearchCriteria(queryBuilder, "courseComponents.description", text, false);
      addTokenizedSearchCriteria(queryBuilder, "tags.text", text, false);
      queryBuilder.append(")");
    }

    Session s = getHibernateSession();
    FullTextSession fullTextSession = Search.getFullTextSession(s);

    try {
      QueryParser parser = new QueryParser(Version.LUCENE_29, "", new StandardAnalyzer(Version.LUCENE_29));
      String queryString = queryBuilder.toString();
      Query luceneQuery;

      if (StringUtils.isBlank(queryString)) {
        luceneQuery = new MatchAllDocsQuery();
      }
      else {
        luceneQuery = parser.parse(queryString);
      }
      
      System.out.println(queryString);

      FullTextQuery query = fullTextSession.createFullTextQuery(luceneQuery, Course.class)
          .setSort(new Sort(new SortField[]{SortField.FIELD_SCORE, new SortField("nameSortable", SortField.STRING)}))
          .setFirstResult(firstResult)
          .setMaxResults(resultsPerPage);

      if (filterArchived) {
        query.enableFullTextFilter("ArchivedCourse").setParameter("archived", Boolean.FALSE);
      }

      int hits = query.getResultSize();
      int pages = hits / resultsPerPage;
      if (hits % resultsPerPage > 0) {
        pages++;
      }

      int lastResult = Math.min(firstResult + resultsPerPage, hits) - 1;

      return new SearchResult<Course>(page, pages, hits, firstResult, lastResult, query.list());

    }
    catch (ParseException e) {
      throw new PersistenceException(e);
    }
  }

  @SuppressWarnings("unchecked")
  public SearchResult<Course> searchCourses(int resultsPerPage, int page, String name, String tags, String nameExtension,
      String description, CourseState courseState, Subject subject, SearchTimeFilterMode timeFilterMode,
      Date timeframeStart, Date timeframeEnd, boolean filterArchived) {
    int firstResult = page * resultsPerPage;

    String timeframeS = null;
    if (timeframeStart != null)
      timeframeS = getSearchFormattedDate(timeframeStart);

    String timeframeE = null;
    if (timeframeEnd != null)
      timeframeE = getSearchFormattedDate(timeframeEnd);

    StringBuilder queryBuilder = new StringBuilder();

    if (!StringUtils.isBlank(name)) {
      addTokenizedSearchCriteria(queryBuilder, "name", name, true);
    }

    if (!StringUtils.isBlank(tags)) {
      addTokenizedSearchCriteria(queryBuilder, "tags.text", tags, true);
    }

    if (!StringUtils.isBlank(nameExtension)) {
      addTokenizedSearchCriteria(queryBuilder, "nameExtension", nameExtension, true);
    }

    if (!StringUtils.isBlank(description)) {
      addTokenizedSearchCriteria(queryBuilder, "description", description, true);
    }
    
    if (courseState != null) {
      addTokenizedSearchCriteria(queryBuilder, "state.id", courseState.getId().toString(), true);
    }

    if (subject != null) {
      addTokenizedSearchCriteria(queryBuilder, "subject.id", subject.getId().toString(), true);
    }

    if ((timeframeS != null) && (timeframeE != null)) {
      switch (timeFilterMode) {
      case EXCLUSIVE:
        /** beginDate > timeframeStart and endDate < timeframeEnd **/
        queryBuilder.append(" +(").append("+beginDate:[").append(timeframeS).append(" TO ").append(
            getSearchDateInfinityHigh()).append("]").append("+endDate:[").append(getSearchDateInfinityLow()).append(" TO ")
            .append(timeframeE).append("]").append(")");
        break;
      case INCLUSIVE:
        /**
         * (beginDate between timeframeStart - timeframeEnd or endDate between timeframeStart -
         * timeframeEnd) or (startDate less than timeframeStart and endDate more than
         * timeframeEnd)
         **/
        queryBuilder.append(" +(").append("(").append("beginDate:[").append(timeframeS).append(" TO ").append(
            timeframeE).append("] ").append("endDate:[").append(timeframeS).append(" TO ").append(timeframeE).append(
            "]").append(") OR (").append("beginDate:[").append(getSearchDateInfinityLow()).append(" TO ").append(
            timeframeS).append("] AND ").append("endDate:[").append(timeframeE).append(" TO ").append(
                getSearchDateInfinityHigh()).append("]").append(")").append(")");
        break;
      }
    }
    else if (timeframeS != null) {
      switch (timeFilterMode) {
      case EXCLUSIVE:
        /** beginDate > timeframeStart **/
        queryBuilder.append(" +(").append("+beginDate:[").append(timeframeS).append(" TO ").append(
            getSearchDateInfinityHigh()).append("]").append(")");
        break;
      case INCLUSIVE:
        /** beginDate > timeframeStart or endDate > timeframeStart **/
        queryBuilder.append(" +(").append("beginDate:[").append(timeframeS).append(" TO ").append(
            getSearchDateInfinityHigh()).append("]").append("endDate:[").append(timeframeS).append(" TO ").append(
                getSearchDateInfinityHigh()).append("]").append(")");
        break;
      }
    }
    else if (timeframeE != null) {
      switch (timeFilterMode) {
      case EXCLUSIVE:
        /** endDate < timeframeEnd **/
        queryBuilder.append(" +(").append("+endDate:[").append(getSearchDateInfinityLow()).append(" TO ").append(
            timeframeE).append("]").append(")");
        break;
      case INCLUSIVE:
        /** beginDate < timeframeEnd or endDate < timeframeEnd **/
        queryBuilder.append(" +(").append("beginDate:[").append(getSearchDateInfinityLow()).append(" TO ").append(
            timeframeE).append("] ").append("endDate:[").append(getSearchDateInfinityLow()).append(" TO ").append(
            timeframeE).append("]").append(")");
        break;
      }
    }

    Session s = getHibernateSession();
    FullTextSession fullTextSession = Search.getFullTextSession(s);

    try {
      QueryParser parser = new QueryParser(Version.LUCENE_29, "", new StandardAnalyzer(Version.LUCENE_29));
      String queryString = queryBuilder.toString();
      Query luceneQuery;

      if (StringUtils.isBlank(queryString)) {
        luceneQuery = new MatchAllDocsQuery();
      }
      else {
        luceneQuery = parser.parse(queryString);
      }

      FullTextQuery query = fullTextSession.createFullTextQuery(luceneQuery, Course.class)
          .setSort(new Sort(new SortField[]{SortField.FIELD_SCORE, new SortField("nameSortable", SortField.STRING)}))
          .setFirstResult(firstResult)
          .setMaxResults(resultsPerPage);

      if (filterArchived)
        query.enableFullTextFilter("ArchivedCourse").setParameter("archived", Boolean.FALSE);

      int hits = query.getResultSize();
      int pages = hits / resultsPerPage;
      if (hits % resultsPerPage > 0)
        pages++;

      int lastResult = Math.min(firstResult + resultsPerPage, hits) - 1;

      return new SearchResult<Course>(page, pages, hits, firstResult, lastResult, query.list());

    }
    catch (ParseException e) {
      throw new PersistenceException(e);
    }
  }

  @SuppressWarnings("unchecked")
  public List<Course> listCoursesByCourseVariable(String key, String value) {
    Session s = getHibernateSession();

    CourseBaseVariableKey courseBaseVariableKey = getCourseBaseVariableKey(key);
    return (List<Course>) s.createCriteria(CourseBaseVariable.class).add(Restrictions.eq("key", courseBaseVariableKey))
        .add(Restrictions.eq("value", value)).setProjection(Projections.property("courseBase")).list();
  }

  @SuppressWarnings("unchecked")
  public List<Course> listCoursesByModule(Module module) {
    Session s = getHibernateSession();
    
    return (List<Course>) s.createCriteria(Course.class)
      .add(Restrictions.eq("module", module))
      .add(Restrictions.eq("archived", Boolean.FALSE))
      .list();
  }

  private CourseBaseVariable getCourseBaseVariable(Course course, CourseBaseVariableKey key) {
    Session s = getHibernateSession();
    CourseBaseVariable courseBaseVariable = (CourseBaseVariable) s.createCriteria(CourseBaseVariable.class).add(
        Restrictions.eq("courseBase", course)).add(Restrictions.eq("key", key)).uniqueResult();
    return courseBaseVariable;
  }

  private void deleteCourseBaseVariable(CourseBaseVariable courseBaseVariable) {
    Session s = getHibernateSession();
    s.delete(courseBaseVariable);
  }

  private CourseBaseVariableKey getCourseBaseVariableKey(String key) {
    Session s = getHibernateSession();
    CourseBaseVariableKey courseBaseVariableKey = (CourseBaseVariableKey) s.createCriteria(CourseBaseVariableKey.class)
        .add(Restrictions.eq("variableKey", key)).uniqueResult();
    return courseBaseVariableKey;
  }

  public String getCourseVariable(Course course, String key) {
    CourseBaseVariableKey courseBaseVariableKey = getCourseBaseVariableKey(key);
    if (courseBaseVariableKey != null) {
      CourseBaseVariable courseBaseVariable = getCourseBaseVariable(course, courseBaseVariableKey);
      return courseBaseVariable == null ? null : courseBaseVariable.getValue();
    }
    else {
      throw new PersistenceException("Unknown VariableKey");
    }
  }

  public void setCourseVariable(Course course, String key, String value) {
    CourseBaseVariableKey courseBaseVariableKey = getCourseBaseVariableKey(key);
    if (courseBaseVariableKey != null) {
      CourseBaseVariable courseBaseVariable = getCourseBaseVariable(course, courseBaseVariableKey);
      if (StringUtils.isBlank(value)) {
        deleteCourseBaseVariable(courseBaseVariable);
      }
      else {
        if (courseBaseVariable == null) {
          courseBaseVariable = createCourseBaseVariable(course, courseBaseVariableKey, value);
        }
        else {
          updateCourseBaseVariable(courseBaseVariable, value);
        }
      }
    }
    else {
      throw new PersistenceException("Unknown VariableKey");
    }
  }
  
  public void archiveCourseState(CourseState courseState) {
    Session s = getHibernateSession();
    courseState.setArchived(Boolean.TRUE);
    s.saveOrUpdate(courseState);
  }
  
  public void unarchiveCourseState(CourseState courseState) {
    Session s = getHibernateSession();
    courseState.setArchived(Boolean.FALSE);
    s.saveOrUpdate(courseState);
  }
  
  public void archiveCourseParticipationType(CourseParticipationType courseParticipationType) {
    Session s = getHibernateSession();
    courseParticipationType.setArchived(Boolean.TRUE);
    s.saveOrUpdate(courseParticipationType);
  }
  
  public void unarchiveCourseParticipationType(CourseParticipationType courseParticipationType) {
    Session s = getHibernateSession();
    courseParticipationType.setArchived(Boolean.FALSE);
    s.saveOrUpdate(courseParticipationType);
  }
  
  /* CourseComponentResource */
  
  public CourseComponentResource findComponentResourceById(Long id) {
    EntityManager entityManager = getEntityManager();
    return entityManager.find(CourseComponentResource.class, id);
  }
  
  @SuppressWarnings("unchecked")
  public List<CourseComponentResource> listCourseComponentResourcesByCourseComponent(CourseComponent courseComponent) {
    Session session = getHibernateSession();
    
    return session.createCriteria(CourseComponentResource.class)
      .add(Restrictions.eq("courseComponent", courseComponent))
      .list();
  }
  
  public CourseComponentResource createCourseComponentResource(CourseComponent courseComponent, Resource resource, Double usagePercent) {
    EntityManager entityManager = getEntityManager();
    
    CourseComponentResource courseComponentResource = new CourseComponentResource();
    courseComponentResource.setResource(resource);
    courseComponentResource.setUsagePercent(usagePercent);
    
    entityManager.persist(courseComponentResource);
    
    courseComponent.addResource(courseComponentResource);
    entityManager.persist(courseComponent);
    
    return courseComponentResource;
  }
  
  public CourseComponentResource updateCourseComponentResourceUsagePercent(CourseComponentResource courseComponentResource, Double usagePercent) {
    EntityManager entityManager = getEntityManager();
    
    courseComponentResource.setUsagePercent(usagePercent);
    
    entityManager.persist(courseComponentResource);
    
    return courseComponentResource;
  }
  
  public void deleteCourseComponentResource(CourseComponentResource courseComponentResource) {
    EntityManager entityManager = getEntityManager();
    
    CourseComponent courseComponent = courseComponentResource.getCourseComponent();
    courseComponent.removeResource(courseComponentResource);
    entityManager.persist(courseComponent);
    
    entityManager.remove(courseComponentResource);
  }
  
  /* CourseStudent */

  /**
   * Returns the course student corresponding to the given identifier.
   * 
   * @param courseStudentId The course student identifier
   * 
   * @return The course student corresponding to the given identifier
   */
  public CourseStudent findCourseStudentById(Long courseStudentId) {
    Session s = getHibernateSession();
    return (CourseStudent) s.load(CourseStudent.class, courseStudentId);
  }

  public CourseStudent findCourseStudentByCourseAndStudent(Course course, Student student) {
    Session s = getHibernateSession();

    return (CourseStudent) s.createCriteria(CourseStudent.class)
      .add(Restrictions.eq("course", course))
      .add(Restrictions.eq("student", student))
      .uniqueResult();
  }

  /**
   * Adds a course student to the database.
   * 
   * @param course The course
   * @param student The student
   * @param courseEnrolmentType Student enrolment type
   * @param participationType Student participation type
   * @param enrolmentDate The enrolment date
   * @param optionality 
   * 
   * @return The created course student
   */
  public CourseStudent createCourseStudent(Course course, Student student, CourseEnrolmentType courseEnrolmentType,
      CourseParticipationType participationType, Date enrolmentDate, Boolean lodging, CourseOptionality optionality) {
    Session s = getHibernateSession();

    CourseStudent courseStudent = new CourseStudent();
    courseStudent.setCourseEnrolmentType(courseEnrolmentType);
    courseStudent.setEnrolmentTime(enrolmentDate);
    courseStudent.setParticipationType(participationType);
    courseStudent.setLodging(lodging);
    courseStudent.setOptionality(optionality);
    courseStudent.setStudent(student);
    s.saveOrUpdate(courseStudent);
    
    course.addCourseStudent(courseStudent);
    s.saveOrUpdate(course);

    return courseStudent;
  }
  
  /**
   * Updates a course student to the database.
   * 
   * @param courseStudent The course student to be updated
   * @param courseEnrolmentType Student enrolment type
   * @param participationType Student participation type
   * @param enrolmentDate Student enrolment date
   * @param optionality 
   */
  public CourseStudent updateCourseStudent(CourseStudent courseStudent, Student student, 
      CourseEnrolmentType courseEnrolmentType, CourseParticipationType participationType, 
      Date enrolmentDate, Boolean lodging, CourseOptionality optionality) {
    Session s = getHibernateSession();

    courseStudent.setStudent(student);
    courseStudent.setCourseEnrolmentType(courseEnrolmentType);
    courseStudent.setEnrolmentTime(enrolmentDate);
    courseStudent.setParticipationType(participationType);
    courseStudent.setLodging(lodging);
    courseStudent.setOptionality(optionality);

    s.saveOrUpdate(courseStudent);

    return courseStudent;
  }

  public void archiveCourseStudent(CourseStudent courseStudent) {
    Session s = getHibernateSession();
    courseStudent.setArchived(Boolean.TRUE);
    s.saveOrUpdate(courseStudent);
  }

  public void unarchiveCourseStudent(CourseStudent courseStudent) {
    Session s = getHibernateSession();
    courseStudent.setArchived(Boolean.FALSE);
    s.saveOrUpdate(courseStudent);
  }

  /**
   * Returns a list of the students in the given course.
   * 
   * @param course The course
   * 
   * @return A list of the students in the given course
   */
  @SuppressWarnings("unchecked")
  public List<CourseStudent> listCourseStudentsByCourse(Course course) {
    Session s = getHibernateSession();
    return s.createCriteria(CourseStudent.class)
      .add(Restrictions.eq("course", course))
      .add(Restrictions.eq("archived", Boolean.FALSE)).list();
  }

  @SuppressWarnings("unchecked")
  public List<CourseStudent> listCourseStudentsByStudent(Student student) {
    Session s = getHibernateSession();
    return s.createCriteria(CourseStudent.class)
      .add(Restrictions.eq("student", student))
      .add(Restrictions.eq("archived", Boolean.FALSE))
      .list();
  }

  /**
   * Deletes the given course student from the database.
   * 
   * @param courseStudent The course student to be deleted
   */
  public void deleteCourseStudent(CourseStudent courseStudent) {
    Session s = getHibernateSession();
    
    Course course = courseStudent.getCourse();
    if (course != null) {
      course.removeCourseStudent(courseStudent);
      s.saveOrUpdate(course);
    }
    
    s.delete(courseStudent);
  }

}
