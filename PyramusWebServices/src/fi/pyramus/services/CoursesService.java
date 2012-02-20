package fi.pyramus.services;

import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import fi.pyramus.dao.DAOFactory;
import fi.pyramus.dao.base.CourseBaseVariableDAO;
import fi.pyramus.dao.base.CourseEducationSubtypeDAO;
import fi.pyramus.dao.base.CourseEducationTypeDAO;
import fi.pyramus.dao.base.DefaultsDAO;
import fi.pyramus.dao.base.EducationSubtypeDAO;
import fi.pyramus.dao.base.EducationTypeDAO;
import fi.pyramus.dao.base.EducationalTimeUnitDAO;
import fi.pyramus.dao.base.SubjectDAO;
import fi.pyramus.dao.courses.CourseComponentDAO;
import fi.pyramus.dao.courses.CourseDAO;
import fi.pyramus.dao.courses.CourseDescriptionCategoryDAO;
import fi.pyramus.dao.courses.CourseDescriptionDAO;
import fi.pyramus.dao.courses.CourseEnrolmentTypeDAO;
import fi.pyramus.dao.courses.CourseParticipationTypeDAO;
import fi.pyramus.dao.courses.CourseStateDAO;
import fi.pyramus.dao.courses.CourseStudentDAO;
import fi.pyramus.dao.courses.CourseUserDAO;
import fi.pyramus.dao.courses.CourseUserRoleDAO;
import fi.pyramus.dao.modules.ModuleDAO;
import fi.pyramus.dao.students.StudentDAO;
import fi.pyramus.dao.users.UserDAO;
import fi.pyramus.domainmodel.base.CourseBase;
import fi.pyramus.domainmodel.base.CourseEducationSubtype;
import fi.pyramus.domainmodel.base.CourseEducationType;
import fi.pyramus.domainmodel.base.Defaults;
import fi.pyramus.domainmodel.base.EducationSubtype;
import fi.pyramus.domainmodel.base.EducationType;
import fi.pyramus.domainmodel.base.EducationalLength;
import fi.pyramus.domainmodel.base.EducationalTimeUnit;
import fi.pyramus.domainmodel.base.Subject;
import fi.pyramus.domainmodel.courses.Course;
import fi.pyramus.domainmodel.courses.CourseComponent;
import fi.pyramus.domainmodel.courses.CourseDescriptionCategory;
import fi.pyramus.domainmodel.courses.CourseEnrolmentType;
import fi.pyramus.domainmodel.courses.CourseParticipationType;
import fi.pyramus.domainmodel.courses.CourseState;
import fi.pyramus.domainmodel.courses.CourseStudent;
import fi.pyramus.domainmodel.courses.CourseUser;
import fi.pyramus.domainmodel.courses.CourseUserRole;
import fi.pyramus.domainmodel.modules.Module;
import fi.pyramus.domainmodel.modules.ModuleComponent;
import fi.pyramus.domainmodel.students.Student;
import fi.pyramus.domainmodel.users.User;
import fi.pyramus.persistence.search.SearchResult;
import fi.pyramus.persistence.search.SearchTimeFilterMode;
import fi.pyramus.persistence.usertypes.CourseOptionality;
import fi.pyramus.services.entities.EntityFactoryVault;
import fi.pyramus.services.entities.courses.CourseComponentEntity;
import fi.pyramus.services.entities.courses.CourseDescriptionCategoryEntity;
import fi.pyramus.services.entities.courses.CourseDescriptionEntity;
import fi.pyramus.services.entities.courses.CourseEducationSubtypeEntity;
import fi.pyramus.services.entities.courses.CourseEducationTypeEntity;
import fi.pyramus.services.entities.courses.CourseEnrolmentTypeEntity;
import fi.pyramus.services.entities.courses.CourseEntity;
import fi.pyramus.services.entities.courses.CourseEntitySearchResult;
import fi.pyramus.services.entities.courses.CourseParticipationTypeEntity;
import fi.pyramus.services.entities.courses.CourseStudentEntity;
import fi.pyramus.services.entities.courses.CourseUserEntity;

public class CoursesService extends PyramusService {

  public CourseEntity createCourse(Long moduleId, String name, String nameExtension, Long subjectId,
      Integer courseNumber, Date beginDate, Date endDate, Double courseLength, Long courseLengthTimeUnitId,
      String description, Long creatingUserId) {

    UserDAO userDAO = DAOFactory.getInstance().getUserDAO();
    CourseDAO courseDAO = DAOFactory.getInstance().getCourseDAO();
    ModuleDAO moduleDAO = DAOFactory.getInstance().getModuleDAO();
    CourseComponentDAO componentDAO = DAOFactory.getInstance().getCourseComponentDAO();
    CourseDescriptionDAO descriptionDAO = DAOFactory.getInstance().getCourseDescriptionDAO();
    CourseEducationTypeDAO educationTypeDAO = DAOFactory.getInstance().getCourseEducationTypeDAO();
    CourseEducationSubtypeDAO educationSubtypeDAO = DAOFactory.getInstance().getCourseEducationSubtypeDAO();
    EducationalTimeUnitDAO educationalTimeUnitDAO = DAOFactory.getInstance().getEducationalTimeUnitDAO();
    SubjectDAO subjectDAO = DAOFactory.getInstance().getSubjectDAO();
    DefaultsDAO defaultsDAO = DAOFactory.getInstance().getDefaultsDAO();

    Module module = moduleId == null ? null : moduleDAO.findById(moduleId);
    Subject subject = subjectId == null ? null : subjectDAO.findById(subjectId);
    EducationalTimeUnit courseLengthTimeUnit = courseLengthTimeUnitId == null ? null : educationalTimeUnitDAO.findById(courseLengthTimeUnitId);
    User creatingUser = userDAO.findById(creatingUserId);

    // If the course is based on a module, replace all null values with those from the module

    if (module != null) {
      name = name == null ? module.getName() : name;
      subject = subject == null ? module.getSubject() : subject;
      courseNumber = courseNumber == null ? module.getCourseNumber() : courseNumber;
      if (courseLength == null && module.getCourseLength() != null) {
        courseLength = module.getCourseLength().getUnits();
        courseLengthTimeUnit = module.getCourseLength().getUnit();
      }
      description = description == null ? module.getDescription() : description;
    }
    
    CourseState state = defaultsDAO.getDefaults().getInitialCourseState();

    // Course creation

    Course course = courseDAO.create(module, name, nameExtension, state, subject, courseNumber, beginDate, endDate,
        courseLength, courseLengthTimeUnit, null, null, null, null, null, description, null, null, creatingUser);
    
    validateEntity(course);

    // Components, education types, and education subtypes from the possible module

    if (module != null) {

      // Course Description copying from module to course
      descriptionDAO.copy(module, course);
      
      // Components

      List<ModuleComponent> moduleComponents = module.getModuleComponents();
      if (moduleComponents != null) {
        for (ModuleComponent moduleComponent : moduleComponents) {
          EducationalLength educationalLength = moduleComponent.getLength();
          CourseComponent courseComponent = componentDAO.create(
              course,
              educationalLength == null ? null : educationalLength.getUnits(),
              educationalLength == null ? null : educationalLength.getUnit(),
              moduleComponent.getName(),
              moduleComponent.getDescription());
          validateEntity(courseComponent);
        }
      }

      // Education types

      List<CourseEducationType> typesInModule = module.getCourseEducationTypes();
      if (typesInModule != null) {
        for (CourseEducationType typeInModule : typesInModule) {
          CourseEducationType typeInCourse = educationTypeDAO.create(course, typeInModule.getEducationType());
          validateEntity(typeInCourse);

          // Education subtypes

          List<CourseEducationSubtype> subTypesInModule = typeInModule.getCourseEducationSubtypes();
          if (subTypesInModule != null) {
            for (CourseEducationSubtype subtypeInModule : subTypesInModule) {
              CourseEducationSubtype courseEducationSubtype = educationSubtypeDAO.create(typeInCourse, subtypeInModule.getEducationSubtype());
              validateEntity(courseEducationSubtype);
            }
          }
        }
      }
    }

    return EntityFactoryVault.buildFromDomainObject(course);
  }

  public void updateCourse(Long courseId, String name, String nameExtension, Long subjectId, Integer courseNumber,
      Date beginDate, Date endDate, Double courseLength, Long courseLengthTimeUnitId, String description,
      Long modifyingUserId) {

    UserDAO userDAO = DAOFactory.getInstance().getUserDAO();
    CourseDAO courseDAO = DAOFactory.getInstance().getCourseDAO();
    EducationalTimeUnitDAO educationalTimeUnitDAO = DAOFactory.getInstance().getEducationalTimeUnitDAO();
    SubjectDAO subjectDAO = DAOFactory.getInstance().getSubjectDAO();

    Course course = courseDAO.findById(courseId);
    Subject subject = subjectId == null ? null : subjectDAO.findById(subjectId);
    EducationalTimeUnit courseLengthTimeUnit = courseLengthTimeUnitId == null ? null : educationalTimeUnitDAO.findById(courseLengthTimeUnitId);
    User modifyingUser = userDAO.findById(modifyingUserId);

    courseDAO.update(course, name, nameExtension, course.getState(), subject, courseNumber, beginDate, endDate, courseLength,
        courseLengthTimeUnit, course.getDistanceTeachingDays(), course.getLocalTeachingDays(), course.getTeachingHours(), 
        course.getPlanningHours(), course.getAssessingHours(), description, course.getMaxParticipantCount(), course.getEnrolmentTimeEnd(), modifyingUser);
    validateEntity(course);
  }

  public CourseComponentEntity createCourseComponent(Long courseId, Double componentLength,
      Long componentLengthTimeUnitId, String name, String description) {

    CourseDAO courseDAO = DAOFactory.getInstance().getCourseDAO();
    CourseComponentDAO componentDAO = DAOFactory.getInstance().getCourseComponentDAO();
    EducationalTimeUnitDAO educationalTimeUnitDAO = DAOFactory.getInstance().getEducationalTimeUnitDAO();

    Course course = courseDAO.findById(courseId);
    EducationalTimeUnit componentLengthTimeUnit = componentLengthTimeUnitId == null ? null : educationalTimeUnitDAO.findById(componentLengthTimeUnitId);

    CourseComponent courseComponent = componentDAO.create(course, componentLength,
            componentLengthTimeUnit, name, description);
    
    validateEntity(courseComponent);
    
    return EntityFactoryVault.buildFromDomainObject(courseComponent);
  }

  public void updateCourseComponent(Long courseComponentId, Double componentLength, Long componentLengthTimeUnitId,
      String name, String description) {
    
    CourseComponentDAO componentDAO = DAOFactory.getInstance().getCourseComponentDAO();
    EducationalTimeUnitDAO educationalTimeUnitDAO = DAOFactory.getInstance().getEducationalTimeUnitDAO();

    CourseComponent courseComponent = componentDAO.findById(courseComponentId);
    EducationalTimeUnit componentLengthTimeUnit = componentLengthTimeUnitId == null ? null : educationalTimeUnitDAO.findById(componentLengthTimeUnitId);

    componentDAO.update(courseComponent, componentLength, componentLengthTimeUnit, name, description);
    validateEntity(courseComponent);
  }

  public CourseComponentEntity getCourseComponentById(Long courseComponentId) {
    CourseComponentDAO componentDAO = DAOFactory.getInstance().getCourseComponentDAO();
    return EntityFactoryVault.buildFromDomainObject(componentDAO.findById(courseComponentId));
  }

  public CourseEducationTypeEntity[] listCourseEducationTypes(Long courseId) {
    CourseDAO courseDAO = DAOFactory.getInstance().getCourseDAO();
    Course course = courseDAO.findById(courseId);
    return (CourseEducationTypeEntity[]) EntityFactoryVault.buildFromDomainObjects(course.getCourseEducationTypes());
  }

  public CourseEducationTypeEntity getCourseEducationTypeById(Long courseEducationTypeId) {
    CourseEducationTypeDAO educationTypeDAO = DAOFactory.getInstance().getCourseEducationTypeDAO();
    return EntityFactoryVault.buildFromDomainObject(educationTypeDAO.findById(courseEducationTypeId));
  }

  public CourseEducationTypeEntity addCourseEducationType(Long courseId, Long educationTypeId) {
    CourseDAO courseDAO = DAOFactory.getInstance().getCourseDAO();
    CourseEducationTypeDAO courseEducationTypeDAO = DAOFactory.getInstance().getCourseEducationTypeDAO();
    EducationTypeDAO educationTypeDAO = DAOFactory.getInstance().getEducationTypeDAO();    

    Course course = courseDAO.findById(courseId);
    EducationType educationType = educationTypeDAO.findById(educationTypeId);
    CourseEducationType courseEducationType = courseEducationTypeDAO.create(course, educationType);
    validateEntity(courseEducationType);
    return EntityFactoryVault.buildFromDomainObject(courseEducationType);
  }

  public CourseEducationSubtypeEntity addCourseEducationSubtype(Long courseEducationTypeId, Long educationSubtypeId) {
    CourseEducationTypeDAO educationTypeDAO = DAOFactory.getInstance().getCourseEducationTypeDAO();
    CourseEducationSubtypeDAO courseEducationSubtypeDAO = DAOFactory.getInstance().getCourseEducationSubtypeDAO();
    EducationSubtypeDAO educationSubtypeDAO = DAOFactory.getInstance().getEducationSubtypeDAO();    

    CourseEducationType courseEducationType = educationTypeDAO.findById(courseEducationTypeId);
    EducationSubtype educationSubtype = educationSubtypeDAO.findById(educationSubtypeId);
    CourseEducationSubtype courseEducationSubtype = courseEducationSubtypeDAO.create(courseEducationType,
            educationSubtype);
    validateEntity(courseEducationSubtype);
    return EntityFactoryVault.buildFromDomainObject(courseEducationSubtype);
  }

  public CourseEntity[] listCourses() {
    CourseDAO courseDAO = DAOFactory.getInstance().getCourseDAO();
    return (CourseEntity[]) EntityFactoryVault.buildFromDomainObjects(courseDAO.listUnarchived());
  }

  public CourseStudentEntity addCourseStudent(Long courseId, Long studentId, Long courseEnrolmentTypeId,
      Long participationTypeId, Date enrolmentDate, Boolean lodging, String optionality) {
    
    StudentDAO studentDAO = DAOFactory.getInstance().getStudentDAO();
    CourseDAO courseDAO = DAOFactory.getInstance().getCourseDAO();
    CourseParticipationTypeDAO participationTypeDAO = DAOFactory.getInstance().getCourseParticipationTypeDAO();
    CourseEnrolmentTypeDAO enrolmentTypeDAO = DAOFactory.getInstance().getCourseEnrolmentTypeDAO();
    CourseStudentDAO courseStudentDAO = DAOFactory.getInstance().getCourseStudentDAO();
    DefaultsDAO defaultsDAO = DAOFactory.getInstance().getDefaultsDAO();
    Defaults defaults = defaultsDAO.getDefaults();

    Course course = courseDAO.findById(courseId);
    Student student = studentDAO.findById(studentId);
    CourseEnrolmentType courseEnrolmentType = courseEnrolmentTypeId == null ? defaults.getInitialCourseEnrolmentType() : enrolmentTypeDAO.findById(courseEnrolmentTypeId);
    CourseParticipationType participationType = participationTypeId == null ? defaults.getInitialCourseParticipationType() : participationTypeDAO.findById(participationTypeId);
    CourseOptionality cOptionality = null; 
    if (!StringUtils.isBlank(optionality))
      cOptionality = CourseOptionality.valueOf(optionality);

    CourseStudent courseStudent = courseStudentDAO.create(course, student, courseEnrolmentType,
            participationType, enrolmentDate, lodging, cOptionality);

    validateEntity(courseStudent);
    
    return EntityFactoryVault.buildFromDomainObject(courseStudent);
  }

  public void updateCourseStudent(Long courseStudentId, Long courseEnrolmentTypeId, Long participationTypeId,
      Date enrolmentDate, Boolean lodging, String optionality) {

    CourseStudentDAO courseStudentDAO = DAOFactory.getInstance().getCourseStudentDAO();
    CourseParticipationTypeDAO participationTypeDAO = DAOFactory.getInstance().getCourseParticipationTypeDAO();
    CourseEnrolmentTypeDAO enrolmentTypeDAO = DAOFactory.getInstance().getCourseEnrolmentTypeDAO();

    CourseStudent courseStudent = courseStudentDAO.findById(courseStudentId);
    CourseEnrolmentType courseEnrolmentType = courseEnrolmentTypeId == null ? null : enrolmentTypeDAO.findById(courseEnrolmentTypeId);
    CourseParticipationType participationType = participationTypeId == null ? null : participationTypeDAO.findById(participationTypeId);
    CourseOptionality cOptionality = null; 
    if (!StringUtils.isBlank(optionality))
      cOptionality = CourseOptionality.valueOf(optionality);
    
    // TODO: student-parameter (?)
    courseStudentDAO.update(courseStudent, courseStudent.getStudent(), courseEnrolmentType, participationType, enrolmentDate, lodging, cOptionality);
    validateEntity(courseStudent);
  }

  public CourseStudentEntity getCourseStudentById(Long courseStudentId) {
    CourseStudentDAO courseStudentDAO = DAOFactory.getInstance().getCourseStudentDAO();
    return EntityFactoryVault.buildFromDomainObject(courseStudentDAO.findById(courseStudentId));
  }

  public CourseStudentEntity[] listCourseStudentsByCourse(Long courseId) {
    CourseDAO courseDAO = DAOFactory.getInstance().getCourseDAO();
    Course course = courseDAO.findById(courseId);
    CourseStudentDAO courseStudentDAO = DAOFactory.getInstance().getCourseStudentDAO();
    return (CourseStudentEntity[]) EntityFactoryVault.buildFromDomainObjects(courseStudentDAO.listByCourse(course));
  }

  public CourseStudentEntity[] listCourseStudentsByStudent(Long studentId) {
    StudentDAO studentDAO = DAOFactory.getInstance().getStudentDAO();
    CourseStudentDAO courseStudentDAO = DAOFactory.getInstance().getCourseStudentDAO();

    Student student = studentDAO.findById(studentId);
    return (CourseStudentEntity[]) EntityFactoryVault.buildFromDomainObjects(courseStudentDAO.listByStudent(student));
  }

  public CourseEntity getCourseById(Long courseId) {
    CourseDAO courseDAO = DAOFactory.getInstance().getCourseDAO();
    return EntityFactoryVault.buildFromDomainObject(courseDAO.findById(courseId));
  }

  public CourseEnrolmentTypeEntity createCourseEnrolmentType(String name) {
    CourseEnrolmentTypeDAO courseEnrolmentTypeDAO = DAOFactory.getInstance().getCourseEnrolmentTypeDAO();
    CourseEnrolmentType courseEnrolmentType = courseEnrolmentTypeDAO.create(name);
    validateEntity(courseEnrolmentType);
    return EntityFactoryVault.buildFromDomainObject(courseEnrolmentType);
  }

  public CourseEnrolmentTypeEntity getCourseEnrolmentTypeById(Long courseEnrolmentTypeId) {
    CourseEnrolmentTypeDAO enrolmentTypeDAO = DAOFactory.getInstance().getCourseEnrolmentTypeDAO();
    return EntityFactoryVault.buildFromDomainObject(enrolmentTypeDAO.findById(courseEnrolmentTypeId));
  }

  public CourseParticipationTypeEntity createCourseParticipationType(String name) {
    CourseParticipationTypeDAO participationTypeDAO = DAOFactory.getInstance().getCourseParticipationTypeDAO();
    CourseParticipationType courseParticipationType = participationTypeDAO.create(name);
    validateEntity(courseParticipationType);
    return EntityFactoryVault.buildFromDomainObject(courseParticipationType);
  }

  public CourseUserEntity createCourseUser(Long courseId, Long userId, Long courseUserRoleId) {
    UserDAO userDAO = DAOFactory.getInstance().getUserDAO();
    CourseDAO courseDAO = DAOFactory.getInstance().getCourseDAO();
    CourseUserRoleDAO userRoleDAO = DAOFactory.getInstance().getCourseUserRoleDAO();
    CourseUserDAO courseUserDAO = DAOFactory.getInstance().getCourseUserDAO();

    Course course = courseDAO.findById(courseId);
    User user = userDAO.findById(userId);
    CourseUserRole role = userRoleDAO.findById(courseUserRoleId);
    
    CourseUser courseUser = courseUserDAO.create(course, user, role);
    
    validateEntity(courseUser);

    return EntityFactoryVault.buildFromDomainObject(courseUser);
  }

  public CourseParticipationTypeEntity getCourseParticipationTypeById(Long courseParticipationTypeId) {
    CourseParticipationTypeDAO participationTypeDAO = DAOFactory.getInstance().getCourseParticipationTypeDAO();
    return EntityFactoryVault.buildFromDomainObject(participationTypeDAO.findById(courseParticipationTypeId));
  }

  public CourseComponentEntity[] listCourseComponents(Long courseId) {
    CourseDAO courseDAO = DAOFactory.getInstance().getCourseDAO();
    CourseComponentDAO componentDAO = DAOFactory.getInstance().getCourseComponentDAO();
    return (CourseComponentEntity[]) EntityFactoryVault.buildFromDomainObjects(componentDAO.listByCourse(courseDAO.findById(courseId)));
  }

  public CourseEnrolmentTypeEntity[] listCourseEnrolmentTypes() {
    CourseEnrolmentTypeDAO courseEnrolmentTypeDAO = DAOFactory.getInstance().getCourseEnrolmentTypeDAO();
    return (CourseEnrolmentTypeEntity[]) EntityFactoryVault.buildFromDomainObjects(courseEnrolmentTypeDAO.listAll());
  }

  public CourseParticipationTypeEntity[] listCourseParticipationTypes() {
    CourseParticipationTypeDAO participationTypeDAO = DAOFactory.getInstance().getCourseParticipationTypeDAO();
   
    List<CourseParticipationType> courseParticipationTypes = participationTypeDAO.listUnarchived();
    Collections.sort(courseParticipationTypes, new Comparator<CourseParticipationType>() {
      public int compare(CourseParticipationType o1, CourseParticipationType o2) {
        return o1.getIndexColumn() == null ? -1 : o2.getIndexColumn() == null ? 1 : o1.getIndexColumn().compareTo(o2.getIndexColumn());
      }
    });
    
    return (CourseParticipationTypeEntity[]) EntityFactoryVault.buildFromDomainObjects(courseParticipationTypes);
  }

  public CourseEntity[] listCoursesByCourseVariable(String key, String value) {
    CourseDAO courseDAO = DAOFactory.getInstance().getCourseDAO();
    return (CourseEntity[]) EntityFactoryVault.buildFromDomainObjects(courseDAO.listByCourseVariable(key, value));
  }

  public String getCourseVariable(Long courseId, String key) {
    CourseDAO courseDAO = DAOFactory.getInstance().getCourseDAO();
    CourseBaseVariableDAO variableDAO = DAOFactory.getInstance().getCourseBaseVariableDAO();
    return variableDAO.findByCourseAndVariableKey(courseDAO.findById(courseId), key);
  }

  public void setCourseVariable(Long courseId, String key, String value) {
    CourseDAO courseDAO = DAOFactory.getInstance().getCourseDAO();
    CourseBaseVariableDAO variableDAO = DAOFactory.getInstance().getCourseBaseVariableDAO();
    variableDAO.setCourseVariable(courseDAO.findById(courseId), key, value);
  }

  public void archiveCourse(Long courseId) {
    CourseDAO courseDAO = DAOFactory.getInstance().getCourseDAO();
    courseDAO.archive(courseDAO.findById(courseId));
  }

  public void unarchiveCourse(Long courseId) {
    CourseDAO courseDAO = DAOFactory.getInstance().getCourseDAO();
    courseDAO.unarchive(courseDAO.findById(courseId));
  }

  public void archiveCourseStudent(Long courseId, Long studentId) {
    StudentDAO studentDAO = DAOFactory.getInstance().getStudentDAO();
    CourseDAO courseDAO = DAOFactory.getInstance().getCourseDAO();
    CourseStudentDAO courseStudentDAO = DAOFactory.getInstance().getCourseStudentDAO();

    Course course = courseDAO.findById(courseId);
    Student student = studentDAO.findById(studentId);
    CourseStudent courseStudent = courseStudentDAO.findByCourseAndStudent(course, student);
    courseStudentDAO.archive(courseStudent);
  }

  public void unarchiveCourseStudent(Long courseId, Long studentId) {
    StudentDAO studentDAO = DAOFactory.getInstance().getStudentDAO();
    CourseDAO courseDAO = DAOFactory.getInstance().getCourseDAO();
    CourseStudentDAO courseStudentDAO = DAOFactory.getInstance().getCourseStudentDAO();

    Course course = courseDAO.findById(courseId);
    Student student = studentDAO.findById(studentId);
    CourseStudent courseStudent = courseStudentDAO.findByCourseAndStudent(course, student);
    courseDAO.unarchive(courseStudent);
  }

  public CourseStudentEntity getCourseStudentByCourseIdAndStudentId(Long courseId, Long studentId) {
    CourseDAO courseDAO = DAOFactory.getInstance().getCourseDAO();
    StudentDAO studentDAO = DAOFactory.getInstance().getStudentDAO();
    CourseStudentDAO courseStudentDAO = DAOFactory.getInstance().getCourseStudentDAO();

    Course course = courseDAO.findById(courseId);
    Student student = studentDAO.findById(studentId);
    CourseStudent courseStudent = courseStudentDAO.findByCourseAndStudent(course, student);

    return EntityFactoryVault.buildFromDomainObject(courseStudent);
  }
  
  public CourseEntitySearchResult searchCourses(Integer resultsPerPage, Integer page, String name, String tags, String nameExtension, String description, Long courseStateId, Long subjectId, String timeFilterMode, Date timeframeStart, Date timeframeEnd) {
    CourseDAO courseDAO = DAOFactory.getInstance().getCourseDAO();
    CourseStateDAO courseStateDAO = DAOFactory.getInstance().getCourseStateDAO();
    SubjectDAO subjectDAO = DAOFactory.getInstance().getSubjectDAO();

    CourseState courseState = courseStateId != null ? courseStateDAO.findById(courseStateId) : null;
    Subject subject = subjectId != null ? subjectDAO.findById(subjectId) : null;
    SearchTimeFilterMode tFilterMode = timeFilterMode != null ? SearchTimeFilterMode.valueOf(timeFilterMode) : null;
    
    SearchResult<Course> searchResult = courseDAO.searchCourses(resultsPerPage, page, name, tags, nameExtension, description, courseState, subject, tFilterMode, timeframeStart, timeframeEnd, true);
    
    return new CourseEntitySearchResult(searchResult.getPage(), searchResult.getPages(), searchResult.getTotalHitCount(), (CourseEntity[]) EntityFactoryVault.buildFromDomainObjects(searchResult.getResults()));
  }
  
  public CourseDescriptionCategoryEntity[] listCourseDescriptionCategories() {
    CourseDescriptionCategoryDAO descriptionCategoryDAO = DAOFactory.getInstance().getCourseDescriptionCategoryDAO();
    return (CourseDescriptionCategoryEntity[]) EntityFactoryVault.buildFromDomainObjects(descriptionCategoryDAO.listUnarchived());
  }

  public CourseDescriptionEntity[] listCourseDescriptionsByCourseId(Long courseId) {
    CourseDAO courseDAO = DAOFactory.getInstance().getCourseDAO();
    CourseBase courseBase = courseDAO.findById(courseId);
    CourseDescriptionDAO descriptionDAO = DAOFactory.getInstance().getCourseDescriptionDAO();
    return (CourseDescriptionEntity[]) EntityFactoryVault.buildFromDomainObjects(descriptionDAO.listByCourseBase(courseBase));
  }

  public CourseDescriptionEntity getCourseDescriptionByCourseIdAndCategoryId(Long courseId, Long categoryId) {
    CourseDAO courseDAO = DAOFactory.getInstance().getCourseDAO();
    CourseDescriptionDAO descriptionDAO = DAOFactory.getInstance().getCourseDescriptionDAO();
    
    CourseBase courseBase = courseDAO.findById(courseId);
    CourseDescriptionCategoryDAO descriptionCategoryDAO = DAOFactory.getInstance().getCourseDescriptionCategoryDAO();
    CourseDescriptionCategory category = descriptionCategoryDAO.findById(categoryId);
    
    return (CourseDescriptionEntity) EntityFactoryVault.buildFromDomainObject(descriptionDAO.findByCourseAndCategory(courseBase, category));
  }
  
}
