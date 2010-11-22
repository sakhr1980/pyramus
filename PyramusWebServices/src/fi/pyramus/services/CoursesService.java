package fi.pyramus.services;

import java.util.Date;
import java.util.List;

import fi.pyramus.dao.BaseDAO;
import fi.pyramus.dao.CourseDAO;
import fi.pyramus.dao.DAOFactory;
import fi.pyramus.dao.ModuleDAO;
import fi.pyramus.dao.StudentDAO;
import fi.pyramus.dao.UserDAO;
import fi.pyramus.domainmodel.base.CourseEducationSubtype;
import fi.pyramus.domainmodel.base.CourseEducationType;
import fi.pyramus.domainmodel.base.EducationSubtype;
import fi.pyramus.domainmodel.base.EducationType;
import fi.pyramus.domainmodel.base.EducationalLength;
import fi.pyramus.domainmodel.base.EducationalTimeUnit;
import fi.pyramus.domainmodel.base.Subject;
import fi.pyramus.domainmodel.courses.Course;
import fi.pyramus.domainmodel.courses.CourseComponent;
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
import fi.pyramus.services.entities.EntityFactoryVault;
import fi.pyramus.services.entities.courses.CourseComponentEntity;
import fi.pyramus.services.entities.courses.CourseEducationSubtypeEntity;
import fi.pyramus.services.entities.courses.CourseEducationTypeEntity;
import fi.pyramus.services.entities.courses.CourseEnrolmentTypeEntity;
import fi.pyramus.services.entities.courses.CourseEntity;
import fi.pyramus.services.entities.courses.CourseParticipationTypeEntity;
import fi.pyramus.services.entities.courses.CourseStudentEntity;
import fi.pyramus.services.entities.courses.CourseUserEntity;

public class CoursesService extends PyramusService {

  public CourseEntity createCourse(Long moduleId, String name, String nameExtension, Long subjectId,
      Integer courseNumber, Date beginDate, Date endDate, Double courseLength, Long courseLengthTimeUnitId,
      String description, Long creatingUserId) {

    UserDAO userDAO = DAOFactory.getInstance().getUserDAO();
    BaseDAO baseDAO = DAOFactory.getInstance().getBaseDAO();
    CourseDAO courseDAO = DAOFactory.getInstance().getCourseDAO();
    ModuleDAO moduleDAO = DAOFactory.getInstance().getModuleDAO();

    Module module = moduleId == null ? null : moduleDAO.getModule(moduleId);
    Subject subject = subjectId == null ? null : baseDAO.getSubject(subjectId);
    EducationalTimeUnit courseLengthTimeUnit = courseLengthTimeUnitId == null ? null : baseDAO
        .findEducationalTimeUnitById(courseLengthTimeUnitId);
    User creatingUser = userDAO.getUser(creatingUserId);

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
    
    CourseState state = baseDAO.getDefaults().getInitialCourseState();

    // Course creation

    Course course = courseDAO.createCourse(module, name, nameExtension, state, subject, courseNumber, beginDate, endDate,
        courseLength, courseLengthTimeUnit, null, null, null, null, null, description, creatingUser);
    
    validateEntity(course);

    // Components, education types, and education subtypes from the possible module

    if (module != null) {

      // Components

      List<ModuleComponent> moduleComponents = module.getModuleComponents();
      if (moduleComponents != null) {
        for (ModuleComponent moduleComponent : moduleComponents) {
          EducationalLength educationalLength = moduleComponent.getLength();
          CourseComponent courseComponent = courseDAO.createCourseComponent(
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
          CourseEducationType typeInCourse = courseDAO.addCourseEducationType(course, typeInModule.getEducationType());
          validateEntity(typeInCourse);

          // Education subtypes

          List<CourseEducationSubtype> subTypesInModule = typeInModule.getCourseEducationSubtypes();
          if (subTypesInModule != null) {
            for (CourseEducationSubtype subtypeInModule : subTypesInModule) {
              CourseEducationSubtype courseEducationSubtype = courseDAO.addCourseEducationSubtype(typeInCourse, subtypeInModule.getEducationSubtype());
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
    BaseDAO baseDAO = DAOFactory.getInstance().getBaseDAO();
    CourseDAO courseDAO = DAOFactory.getInstance().getCourseDAO();

    Course course = courseDAO.getCourse(courseId);
    Subject subject = subjectId == null ? null : baseDAO.getSubject(subjectId);
    EducationalTimeUnit courseLengthTimeUnit = courseLengthTimeUnitId == null ? null : baseDAO
        .findEducationalTimeUnitById(courseLengthTimeUnitId);
    User modifyingUser = userDAO.getUser(modifyingUserId);

    courseDAO.updateCourse(course, name, nameExtension, course.getState(), subject, courseNumber, beginDate, endDate, courseLength,
        courseLengthTimeUnit, course.getDistanceTeachingDays(), course.getLocalTeachingDays(), course.getTeachingHours(), 
        course.getPlanningHours(), course.getAssessingHours(), description, modifyingUser);
    validateEntity(course);
  }

  public CourseComponentEntity createCourseComponent(Long courseId, Double componentLength,
      Long componentLengthTimeUnitId, String name, String description) {

    BaseDAO baseDAO = DAOFactory.getInstance().getBaseDAO();
    CourseDAO courseDAO = DAOFactory.getInstance().getCourseDAO();
    
    Course course = courseDAO.getCourse(courseId);
    EducationalTimeUnit componentLengthTimeUnit = componentLengthTimeUnitId == null ? null : baseDAO
        .findEducationalTimeUnitById(componentLengthTimeUnitId);

    CourseComponent courseComponent = courseDAO.createCourseComponent(course, componentLength,
            componentLengthTimeUnit, name, description);
    
    validateEntity(courseComponent);
    
    return EntityFactoryVault.buildFromDomainObject(courseComponent);
  }

  public void updateCourseComponent(Long courseComponentId, Double componentLength, Long componentLengthTimeUnitId,
      String name, String description) {
    
    BaseDAO baseDAO = DAOFactory.getInstance().getBaseDAO();
    CourseDAO courseDAO = DAOFactory.getInstance().getCourseDAO();

    CourseComponent courseComponent = courseDAO.getCourseComponent(courseComponentId);
    EducationalTimeUnit componentLengthTimeUnit = componentLengthTimeUnitId == null ? null : baseDAO
        .findEducationalTimeUnitById(componentLengthTimeUnitId);

    courseDAO.updateCourseComponent(courseComponent, componentLength, componentLengthTimeUnit, name, description);
    validateEntity(courseComponent);
  }

  public CourseComponentEntity getCourseComponentById(Long courseComponentId) {
    CourseDAO courseDAO = DAOFactory.getInstance().getCourseDAO();
    return EntityFactoryVault.buildFromDomainObject(courseDAO.getCourseComponent(courseComponentId));
  }

  public CourseEducationTypeEntity[] listCourseEducationTypes(Long courseId) {
    CourseDAO courseDAO = DAOFactory.getInstance().getCourseDAO();
    Course course = courseDAO.getCourse(courseId);
    return (CourseEducationTypeEntity[]) EntityFactoryVault.buildFromDomainObjects(course.getCourseEducationTypes());
  }

  public CourseEducationTypeEntity getCourseEducationTypeById(Long courseEducationTypeId) {
    CourseDAO courseDAO = DAOFactory.getInstance().getCourseDAO();
    return EntityFactoryVault.buildFromDomainObject(courseDAO.getCourseEducationType(courseEducationTypeId));
  }

  public CourseEducationTypeEntity addCourseEducationType(Long courseId, Long educationTypeId) {

    BaseDAO baseDAO = DAOFactory.getInstance().getBaseDAO();
    CourseDAO courseDAO = DAOFactory.getInstance().getCourseDAO();

    Course course = courseDAO.getCourse(courseId);
    EducationType educationType = baseDAO.getEducationType(educationTypeId);
    CourseEducationType courseEducationType = courseDAO.addCourseEducationType(course, educationType);
    validateEntity(courseEducationType);
    return EntityFactoryVault.buildFromDomainObject(courseEducationType);
  }

  public CourseEducationSubtypeEntity addCourseEducationSubtype(Long courseEducationTypeId, Long educationSubtypeId) {
    
    BaseDAO baseDAO = DAOFactory.getInstance().getBaseDAO();
    CourseDAO courseDAO = DAOFactory.getInstance().getCourseDAO();

    CourseEducationType courseEducationType = courseDAO.getCourseEducationType(courseEducationTypeId);
    EducationSubtype educationSubtype = baseDAO.getEducationSubtype(educationSubtypeId);
    CourseEducationSubtype courseEducationSubtype = courseDAO.addCourseEducationSubtype(courseEducationType,
            educationSubtype);
    validateEntity(courseEducationSubtype);
    return EntityFactoryVault.buildFromDomainObject(courseEducationSubtype);
  }

  public CourseEntity[] listCourses() {
    CourseDAO courseDAO = DAOFactory.getInstance().getCourseDAO();
    return (CourseEntity[]) EntityFactoryVault.buildFromDomainObjects(courseDAO.listCourses());
  }

  public CourseStudentEntity addCourseStudent(Long courseId, Long studentId, Long courseEnrolmentTypeId,
      Long participationTypeId, Date enrolmentDate, Boolean lodging) {
    
    StudentDAO studentDAO = DAOFactory.getInstance().getStudentDAO();
    CourseDAO courseDAO = DAOFactory.getInstance().getCourseDAO();

    Course course = courseDAO.getCourse(courseId);
    Student student = studentDAO.getStudent(studentId);
    CourseEnrolmentType courseEnrolmentType = courseEnrolmentTypeId == null ? null : courseDAO.getCourseEnrolmentType(courseEnrolmentTypeId);
    CourseParticipationType participationType = participationTypeId == null ? null : courseDAO.getCourseParticipationType(participationTypeId);

    CourseStudent courseStudent = courseDAO.addCourseStudent(course, student, courseEnrolmentType,
            participationType, enrolmentDate, lodging);

    validateEntity(courseStudent);
    
    return EntityFactoryVault.buildFromDomainObject(courseStudent);
  }

  public void updateCourseStudent(Long courseStudentId, Long courseEnrolmentTypeId, Long participationTypeId,
      Date enrolmentDate, Boolean lodging) {

    CourseDAO courseDAO = DAOFactory.getInstance().getCourseDAO();
    
    CourseStudent courseStudent = courseDAO.getCourseStudent(courseStudentId);
    CourseEnrolmentType courseEnrolmentType = courseEnrolmentTypeId == null ? null : courseDAO.getCourseEnrolmentType(courseEnrolmentTypeId);
    CourseParticipationType participationType = participationTypeId == null ? null : courseDAO.getCourseParticipationType(participationTypeId);

    // TODO: student-parameter (?)
    courseDAO.updateCourseStudent(courseStudent, courseStudent.getStudent(), courseEnrolmentType, participationType, enrolmentDate, lodging);
    validateEntity(courseStudent);
  }

  public CourseStudentEntity getCourseStudentById(Long courseStudentId) {
    CourseDAO courseDAO = DAOFactory.getInstance().getCourseDAO();
    return EntityFactoryVault.buildFromDomainObject(courseDAO.getCourseStudent(courseStudentId));
  }

  public CourseStudentEntity[] listCourseStudentsByCourse(Long courseId) {
    CourseDAO courseDAO = DAOFactory.getInstance().getCourseDAO();
    Course course = courseDAO.getCourse(courseId);
    return (CourseStudentEntity[]) EntityFactoryVault.buildFromDomainObjects(courseDAO.listCourseStudents(course));
  }

  public CourseStudentEntity[] listCourseStudentsByStudent(Long studentId) {
    CourseDAO courseDAO = DAOFactory.getInstance().getCourseDAO();
    StudentDAO studentDAO = DAOFactory.getInstance().getStudentDAO();

    Student student = studentDAO.getStudent(studentId);
    return (CourseStudentEntity[]) EntityFactoryVault.buildFromDomainObjects(courseDAO.listStudentCourses(student));
  }

  public CourseEntity getCourseById(Long courseId) {
    CourseDAO courseDAO = DAOFactory.getInstance().getCourseDAO();
    return EntityFactoryVault.buildFromDomainObject(courseDAO.getCourse(courseId));
  }

  public CourseEnrolmentTypeEntity createCourseEnrolmentType(String name) {
    CourseDAO courseDAO = DAOFactory.getInstance().getCourseDAO();
    CourseEnrolmentType courseEnrolmentType = courseDAO.createCourseEnrolmentType(name);
    validateEntity(courseEnrolmentType);
    return EntityFactoryVault.buildFromDomainObject(courseEnrolmentType);
  }

  public CourseEnrolmentTypeEntity getCourseEnrolmentTypeById(Long courseEnrolmentTypeId) {
    CourseDAO courseDAO = DAOFactory.getInstance().getCourseDAO();
    return EntityFactoryVault.buildFromDomainObject(courseDAO.getCourseEnrolmentType(courseEnrolmentTypeId));
  }

  public CourseParticipationTypeEntity createCourseParticipationType(String name) {
    CourseDAO courseDAO = DAOFactory.getInstance().getCourseDAO();
    CourseParticipationType courseParticipationType = courseDAO.createCourseParticipationType(name);
    validateEntity(courseParticipationType);
    return EntityFactoryVault.buildFromDomainObject(courseParticipationType);
  }

  public CourseUserEntity createCourseUser(Long courseId, Long userId, Long courseUserRoleId) {
    UserDAO userDAO = DAOFactory.getInstance().getUserDAO();
    CourseDAO courseDAO = DAOFactory.getInstance().getCourseDAO();

    Course course = courseDAO.getCourse(courseId);
    User user = userDAO.getUser(userId);
    CourseUserRole role = courseDAO.getCourseUserRole(courseUserRoleId);
    
    CourseUser courseUser = courseDAO.createCourseUser(course, user, role);
    
    validateEntity(courseUser);

    return EntityFactoryVault.buildFromDomainObject(courseUser);
  }

  public CourseParticipationTypeEntity getCourseParticipationTypeById(Long courseParticipationTypeId) {
    CourseDAO courseDAO = DAOFactory.getInstance().getCourseDAO();
    return EntityFactoryVault.buildFromDomainObject(courseDAO.getCourseParticipationType(courseParticipationTypeId));
  }

  public CourseComponentEntity[] listCourseComponents(Long courseId) {
    CourseDAO courseDAO = DAOFactory.getInstance().getCourseDAO();
    return (CourseComponentEntity[]) EntityFactoryVault.buildFromDomainObjects(courseDAO.listCourseComponents(courseDAO.getCourse(courseId)));
  }

  public CourseEnrolmentTypeEntity[] listCourseEnrolmentTypes() {
    CourseDAO courseDAO = DAOFactory.getInstance().getCourseDAO();
    return (CourseEnrolmentTypeEntity[]) EntityFactoryVault.buildFromDomainObjects(courseDAO.listCourseEnrolmentTypes());
  }

  public CourseParticipationTypeEntity[] listCourseParticipationTypes() {
    CourseDAO courseDAO = DAOFactory.getInstance().getCourseDAO();
    return (CourseParticipationTypeEntity[]) EntityFactoryVault.buildFromDomainObjects(courseDAO.listCourseParticipationTypes());
  }

  public CourseEntity[] listCoursesByCourseVariable(String key, String value) {
    CourseDAO courseDAO = DAOFactory.getInstance().getCourseDAO();
    return (CourseEntity[]) EntityFactoryVault.buildFromDomainObjects(courseDAO.listCoursesByCourseVariable(key, value));
  }

  public String getCourseVariable(Long courseId, String key) {
    CourseDAO courseDAO = DAOFactory.getInstance().getCourseDAO();
    return courseDAO.getCourseVariable(courseDAO.getCourse(courseId), key);
  }

  public void setCourseVariable(Long courseId, String key, String value) {
    CourseDAO courseDAO = DAOFactory.getInstance().getCourseDAO();
    courseDAO.setCourseVariable(courseDAO.getCourse(courseId), key, value);
  }

  public void archiveCourse(Long courseId) {
    CourseDAO courseDAO = DAOFactory.getInstance().getCourseDAO();
    courseDAO.archiveCourse(courseDAO.getCourse(courseId));
  }

  public void unarchiveCourse(Long courseId) {
    CourseDAO courseDAO = DAOFactory.getInstance().getCourseDAO();
    courseDAO.unarchiveCourse(courseDAO.getCourse(courseId));
  }

  public void archiveCourseStudent(Long courseId, Long studentId) {
    StudentDAO studentDAO = DAOFactory.getInstance().getStudentDAO();
    CourseDAO courseDAO = DAOFactory.getInstance().getCourseDAO();

    Course course = courseDAO.getCourse(courseId);
    Student student = studentDAO.getStudent(studentId);
    CourseStudent courseStudent = courseDAO.getCourseStudent(course, student);
    courseDAO.archiveCourseStudent(courseStudent);
  }

  public void unarchiveCourseStudent(Long courseId, Long studentId) {
    StudentDAO studentDAO = DAOFactory.getInstance().getStudentDAO();
    CourseDAO courseDAO = DAOFactory.getInstance().getCourseDAO();

    Course course = courseDAO.getCourse(courseId);
    Student student = studentDAO.getStudent(studentId);
    CourseStudent courseStudent = courseDAO.getCourseStudent(course, student);
    courseDAO.unarchiveCourseStudent(courseStudent);
  }

}
