package fi.pyramus.json.courses;

import java.util.Arrays;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import org.apache.commons.lang.StringUtils;
import org.hibernate.StaleObjectStateException;

import fi.pyramus.JSONRequestContext;
import fi.pyramus.UserRole;
import fi.pyramus.dao.BaseDAO;
import fi.pyramus.dao.CourseDAO;
import fi.pyramus.dao.DAOFactory;
import fi.pyramus.dao.ResourceDAO;
import fi.pyramus.dao.StudentDAO;
import fi.pyramus.dao.UserDAO;
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
import fi.pyramus.domainmodel.resources.Resource;
import fi.pyramus.domainmodel.resources.ResourceType;
import fi.pyramus.domainmodel.students.Student;
import fi.pyramus.domainmodel.users.User;
import fi.pyramus.json.JSONRequestController;
import fi.pyramus.persistence.usertypes.MonetaryAmount;

/**
 * The controller responsible of modifying an existing course. 
 * 
 * @see fi.pyramus.views.modules.EditCourseViewController
 */
public class EditCourseJSONRequestController implements JSONRequestController {

  /**
   * Processes the request to edit a course.
   * 
   * @param requestContext The JSON request context
   */
  public void process(JSONRequestContext requestContext) {
    UserDAO userDAO = DAOFactory.getInstance().getUserDAO();
    StudentDAO studentDAO = DAOFactory.getInstance().getStudentDAO();
    BaseDAO baseDAO = DAOFactory.getInstance().getBaseDAO();
    CourseDAO courseDAO = DAOFactory.getInstance().getCourseDAO();
    ResourceDAO resourceDAO = DAOFactory.getInstance().getResourceDAO();

    // Course basic information

    Long courseId = requestContext.getLong("course");
    Course course = courseDAO.getCourse(courseId);
    String name = requestContext.getString("name");
    String nameExtension = requestContext.getString("nameExtension");
    Long courseStateId = requestContext.getLong("state");
    CourseState courseState = courseStateId == null ? course.getState() : courseDAO.getCourseState(courseStateId);
    String description = requestContext.getString("description");
    Subject subject = baseDAO.getSubject(requestContext.getLong("subject"));
    Integer courseNumber = requestContext.getInteger("courseNumber");
    Date beginDate = requestContext.getDate("beginDate");
    Date endDate = requestContext.getDate("endDate");
    Double courseLength = requestContext.getDouble("courseLength");
    EducationalTimeUnit courseLengthTimeUnit = baseDAO.findEducationalTimeUnitById(requestContext
        .getLong("courseLengthTimeUnit"));
    Double distanceTeachingDays = requestContext.getDouble("distanceTeachingDays");
    Double localTeachingDays = requestContext.getDouble("localTeachingDays");
    Double teachingHours = requestContext.getDouble("teachingHours");
    Double planningHours = requestContext.getDouble("planningHours");
    Double assessingHours = requestContext.getDouble("assessingHours");
    String tagsText = requestContext.getString("tags");
    
    Long version = requestContext.getLong("version");
    if (!course.getVersion().equals(version))
      throw new StaleObjectStateException(Course.class.getName(), course.getId());
    
    Set<Tag> tagEntities = new HashSet<Tag>();
    if (!StringUtils.isBlank(tagsText)) {
      List<String> tags = Arrays.asList(tagsText.split("[\\ ,]"));
      for (String tag : tags) {
        Tag tagEntity = baseDAO.findTagByText(tag.trim());
        if (tagEntity == null)
          tagEntity = baseDAO.createTag(tag);
        tagEntities.add(tagEntity);
      }
    }
    
    User user = userDAO.getUser(requestContext.getLoggedUserId());

    courseDAO.updateCourse(course, name, nameExtension, courseState, subject, courseNumber, beginDate, endDate,
        courseLength, courseLengthTimeUnit, distanceTeachingDays, localTeachingDays, teachingHours, planningHours, assessingHours, 
        description, user);
    
    // Tags

    courseDAO.setCourseTags(course, tagEntities);
    
    // Education types and subtypes submitted from the web page

    Map<Long, Vector<Long>> chosenEducationTypes = new HashMap<Long, Vector<Long>>();
    Enumeration<String> parameterNames = requestContext.getRequest().getParameterNames();
    while (parameterNames.hasMoreElements()) {
      name = (String) parameterNames.nextElement();
      if (name.startsWith("educationType.")) {
        String[] nameElements = name.split("\\.");
        Long educationTypeId = new Long(nameElements[1]);
        Long educationSubtypeId = new Long(nameElements[2]);
        Vector<Long> v = chosenEducationTypes.containsKey(educationTypeId) ? chosenEducationTypes.get(educationTypeId)
            : new Vector<Long>();
        v.add(educationSubtypeId);
        if (!chosenEducationTypes.containsKey(educationTypeId)) {
          chosenEducationTypes.put(educationTypeId, v);
        }
      }
    }

    // Remove education types and subtypes

    List<CourseEducationType> courseEducationTypes = course.getCourseEducationTypes();
    for (int i = courseEducationTypes.size() - 1; i >= 0; i--) {
      CourseEducationType courseEducationType = courseEducationTypes.get(i);
      if (!chosenEducationTypes.containsKey(courseEducationType.getEducationType().getId())) {
        courseDAO.removeCourseEducationType(courseEducationType);
      } else {
        Vector<Long> v = chosenEducationTypes.get(courseEducationType.getEducationType().getId());
        List<CourseEducationSubtype> courseEducationSubtypes = courseEducationType.getCourseEducationSubtypes();
        for (int j = courseEducationSubtypes.size() - 1; j >= 0; j--) {
          CourseEducationSubtype courseEducationSubtype = courseEducationSubtypes.get(j);
          if (!v.contains(courseEducationSubtype.getEducationSubtype().getId())) {
            courseEducationType.removeSubtype(courseEducationSubtype);
          }
        }
      }
    }

    // Add education types and subtypes

    for (Long educationTypeId : chosenEducationTypes.keySet()) {
      EducationType educationType = baseDAO.getEducationType(educationTypeId);
      CourseEducationType courseEducationType;
      if (!course.contains(educationType)) {
        courseEducationType = courseDAO.addCourseEducationType(course, educationType);
      }
      else {
        courseEducationType = course.getCourseEducationTypeByEducationTypeId(educationTypeId);
      }
      for (Long educationSubtypeId : chosenEducationTypes.get(educationTypeId)) {
        EducationSubtype educationSubtype = educationType.getEducationSubtypeById(educationSubtypeId);
        if (!courseEducationType.contains(educationSubtype)) {
          courseDAO.addCourseEducationSubtype(courseEducationType, educationSubtype);
        }
      }
    }

    // Personnel

    Set<Long> existingIds = new HashSet<Long>();
    int rowCount = requestContext.getInteger("personnelTable.rowCount").intValue();
    for (int i = 0; i < rowCount; i++) {
      String colPrefix = "personnelTable." + i;
      Long courseUserId = requestContext.getLong(colPrefix + ".courseUserId");
      Long userId = requestContext.getLong(colPrefix + ".userId");
      Long roleId = requestContext.getLong(colPrefix + ".roleId");
      user = userDAO.getUser(userId);
      CourseUserRole role = courseDAO.getCourseUserRole(roleId);
      if (courseUserId == -1) {
        courseUserId = courseDAO.createCourseUser(course, user, role).getId();
      }
      else {
        courseDAO.updateCourseUser(courseDAO.getCourseUser(courseUserId), user, role);
      }
      existingIds.add(courseUserId);
    }
    List<CourseUser> courseUsers = courseDAO.listCourseUsers(courseDAO.getCourse(courseId));
    for (CourseUser courseUser : courseUsers) {
      if (!existingIds.contains(courseUser.getId())) {
        courseDAO.deleteCourseUser(courseUser);
      }
    }

    // Course components

    rowCount = requestContext.getInteger("components.componentCount").intValue();
    for (int i = 0; i < rowCount; i++) {
      String colPrefix = "components.component." + i;
      Long componentId = requestContext.getLong(colPrefix + ".0.componentId");
      String componentName = requestContext.getString(colPrefix + ".0.name");
      Double componentLength = requestContext.getDouble(colPrefix + ".0.length");
      String componentDescription = requestContext.getString(colPrefix + ".0.description");

      // TODO Component length; should be just hours but it currently depends on the default time unit - ok?  
      EducationalTimeUnit componentTimeUnit = baseDAO.getDefaults().getBaseTimeUnit();

      CourseComponent courseComponent;
      
      if (componentId == -1) {
        courseComponent = courseDAO.createCourseComponent(course, componentLength, componentTimeUnit, componentName,
            componentDescription);
      } else {
        courseComponent = courseDAO.updateCourseComponent(courseDAO.getCourseComponent(componentId), componentLength, componentTimeUnit,
            componentName, componentDescription);
      }
                        
      Long resourceCategoryCount = requestContext.getLong(colPrefix + ".resourceCategoryCount");
      for (int categoryIndex = 0; categoryIndex < resourceCategoryCount; categoryIndex++) {
        String resourcesPrefix = colPrefix + "." + categoryIndex + ".resources";
        Long resourcesCount = requestContext.getLong(resourcesPrefix + ".rowCount");
        
        for (int j = 0; j < resourcesCount; j++) {
          String resourcePrefix = resourcesPrefix + "." + j;
          
          Long id = requestContext.getLong(resourcePrefix + ".id");
          Long resourceId = requestContext.getLong(resourcePrefix + ".resourceId");
          Resource resource = resourceDAO.findResourceById(resourceId);
          Double usagePercent;
          
          if (resource.getResourceType() == ResourceType.MATERIAL_RESOURCE) {
            usagePercent = requestContext.getDouble(resourcePrefix + ".quantity") * 100;
          } else {
            usagePercent = requestContext.getDouble(resourcePrefix + ".usage");
          }
          
          if (id == -1) {
            courseDAO.createCourseComponentResource(courseComponent, resource, usagePercent);
          } else {
            CourseComponentResource courseComponentResource = courseDAO.findComponentResourceById(id);            
            courseDAO.updateCourseComponentResourceUsagePercent(courseComponentResource, usagePercent);
          }
        }
      }
    }
    
    // Basic course resources

    existingIds = new HashSet<Long>();
    rowCount = requestContext.getInteger("basicResourcesTable.rowCount").intValue();
    for (int i = 0; i < rowCount; i++) {
      String colPrefix = "basicResourcesTable." + i;
      Double hours = requestContext.getDouble(colPrefix + ".hours");
      if (hours == null) {
        hours = 0.0;
      }
      MonetaryAmount hourlyCost = new MonetaryAmount(requestContext.getDouble(colPrefix + ".hourlyCost"));
      Integer units = requestContext.getInteger(colPrefix + ".units");
      MonetaryAmount unitCost = new MonetaryAmount(requestContext.getDouble(colPrefix + ".unitCost"));
      Long resourceId = requestContext.getLong(colPrefix + ".resourceId");
      Resource resource = resourceDAO.findResourceById(resourceId);
      Long basicResourceId = requestContext.getLong(colPrefix + ".basicResourceId");
      if (basicResourceId == -1) {
        basicResourceId = courseDAO.createBasicCourseResource(course, resource, hours, hourlyCost, units, unitCost)
            .getId();
      }
      else {
        courseDAO.updateBasicCourseResource(courseDAO.getBasicCourseResource(basicResourceId), hours, hourlyCost,
            units, unitCost);
      }
      existingIds.add(basicResourceId);
    }
    List<BasicCourseResource> basicCourseResources = courseDAO.listBasicCourseResources(courseId);
    for (BasicCourseResource basicCourseResource : basicCourseResources) {
      if (!existingIds.contains(basicCourseResource.getId())) {
        courseDAO.deleteBasicCourseResource(basicCourseResource);
      }
    }

    // Student course resources

    existingIds = new HashSet<Long>();
    rowCount = requestContext.getInteger("studentResourcesTable.rowCount").intValue();
    for (int i = 0; i < rowCount; i++) {
      String colPrefix = "studentResourcesTable." + i;
      Double hours = requestContext.getDouble(colPrefix + ".hours");
      if (hours == null) {
        hours = 0.0;
      }
      MonetaryAmount hourlyCost = new MonetaryAmount(requestContext.getDouble(colPrefix + ".hourlyCost"));
      MonetaryAmount unitCost = new MonetaryAmount(requestContext.getDouble(colPrefix + ".unitCost"));
      Long resourceId = requestContext.getLong(colPrefix + ".resourceId");
      Resource resource = resourceDAO.findResourceById(resourceId);
      Long studentResourceId = requestContext.getLong(colPrefix + ".studentResourceId");
      if (studentResourceId == -1) {
        studentResourceId = courseDAO.createStudentCourseResource(course, resource, hours, hourlyCost, unitCost)
            .getId();
      }
      else {
        courseDAO.updateStudentCourseResource(courseDAO.getStudentCourseResource(studentResourceId), hours, hourlyCost,
            unitCost);
      }
      existingIds.add(studentResourceId);
    }
    List<StudentCourseResource> studentCourseResources = courseDAO.listStudentCourseResources(courseId);
    for (StudentCourseResource studentCourseResource : studentCourseResources) {
      if (!existingIds.contains(studentCourseResource.getId())) {
        courseDAO.deleteStudentCourseResource(studentCourseResource);
      }
    }

    // Grade course resources

    existingIds = new HashSet<Long>();
    rowCount = requestContext.getInteger("gradeResourcesTable.rowCount").intValue();
    for (int i = 0; i < rowCount; i++) {
      String colPrefix = "gradeResourcesTable." + i;
      Double hours = requestContext.getDouble(colPrefix + ".hours");
      if (hours == null) {
        hours = 0.0;
      }
      MonetaryAmount hourlyCost = new MonetaryAmount(requestContext.getDouble(colPrefix + ".hourlyCost"));
      MonetaryAmount unitCost = new MonetaryAmount(requestContext.getDouble(colPrefix + ".unitCost"));
      Long resourceId = requestContext.getLong(colPrefix + ".resourceId");
      Resource resource = resourceDAO.findResourceById(resourceId);
      Long gradeResourceId = requestContext.getLong(colPrefix + ".gradeResourceId");
      if (gradeResourceId == -1) {
        gradeResourceId = courseDAO.createGradeCourseResource(course, resource, hours, hourlyCost, unitCost).getId();
      }
      else {
        courseDAO.updateGradeCourseResource(courseDAO.getGradeCourseResource(gradeResourceId), hours, hourlyCost,
            unitCost);
      }
      existingIds.add(gradeResourceId);
    }
    List<GradeCourseResource> gradeCourseResources = courseDAO.listGradeCourseResources(courseId);
    for (GradeCourseResource gradeCourseResource : gradeCourseResources) {
      if (!existingIds.contains(gradeCourseResource.getId())) {
        courseDAO.deleteGradeCourseResource(gradeCourseResource);
      }
    }

    // Other costs

    existingIds = new HashSet<Long>();
    rowCount = requestContext.getInteger("otherCostsTable.rowCount").intValue();
    for (int i = 0; i < rowCount; i++) {
      String colPrefix = "otherCostsTable." + i;
      name = requestContext.getString(colPrefix + ".name");
      MonetaryAmount cost = new MonetaryAmount(requestContext.getDouble(colPrefix + ".cost"));
      Long otherCostId = requestContext.getLong(colPrefix + ".otherCostId");
      if (otherCostId == -1) {
        otherCostId = courseDAO.createOtherCost(course, name, cost).getId();
      }
      else {
        courseDAO.updateOtherCost(courseDAO.getOtherCost(otherCostId), name, cost);
      }
      existingIds.add(otherCostId);
    }
    List<OtherCost> otherCosts = courseDAO.listOtherCosts(courseId);
    for (OtherCost otherCost : otherCosts) {
      if (!existingIds.contains(otherCost.getId())) {
        courseDAO.deleteOtherCost(otherCost);
      }
    }

    // Students

    existingIds = new HashSet<Long>();
    rowCount = requestContext.getInteger("studentsTable.rowCount");
    for (int i = 0; i < rowCount; i++) {
      String colPrefix = "studentsTable." + i;

      Long studentId = requestContext.getLong(colPrefix + ".studentId");
      Long courseStudentId = requestContext.getLong(colPrefix + ".courseStudentId");
      Date enrolmentDate = requestContext.getDate(colPrefix + ".enrolmentDate");
      Long enrolmentTypeId = requestContext.getLong(colPrefix + ".enrolmentType");
      Long participationTypeId = requestContext.getLong(colPrefix + ".participationType");
      Boolean lodging = requestContext.getBoolean(colPrefix + ".lodging");

      CourseEnrolmentType enrolmentType = courseDAO.getCourseEnrolmentType(enrolmentTypeId);
      CourseParticipationType participationType = courseDAO.getCourseParticipationType(participationTypeId);
      CourseStudent courseStudent;

      if (courseStudentId == -1) {
        /* New student */
        Student student = studentDAO.getStudent(studentId);
        courseStudent = courseDAO.addCourseStudent(course, student, enrolmentType, participationType, enrolmentDate, lodging);
      }
      else {
        /* Existing student */
        Student student = studentDAO.getStudent(studentId);
        courseStudent = courseDAO.getCourseStudent(courseStudentId);
        courseDAO.updateCourseStudent(courseStudent, student, enrolmentType, participationType, enrolmentDate, lodging);
      }
    }
    requestContext.setRedirectURL(requestContext.getReferer(true));
  }

  public UserRole[] getAllowedRoles() {
    return new UserRole[] { UserRole.MANAGER, UserRole.ADMINISTRATOR };
  }

}
