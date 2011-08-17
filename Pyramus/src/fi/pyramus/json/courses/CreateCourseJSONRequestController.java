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
import org.apache.commons.lang.math.NumberUtils;

import fi.pyramus.JSONRequestContext;
import fi.pyramus.UserRole;
import fi.pyramus.dao.BaseDAO;
import fi.pyramus.dao.CourseDAO;
import fi.pyramus.dao.DAOFactory;
import fi.pyramus.dao.ModuleDAO;
import fi.pyramus.dao.ResourceDAO;
import fi.pyramus.dao.StudentDAO;
import fi.pyramus.dao.UserDAO;
import fi.pyramus.domainmodel.base.CourseEducationType;
import fi.pyramus.domainmodel.base.EducationSubtype;
import fi.pyramus.domainmodel.base.EducationType;
import fi.pyramus.domainmodel.base.EducationalTimeUnit;
import fi.pyramus.domainmodel.base.Subject;
import fi.pyramus.domainmodel.base.Tag;
import fi.pyramus.domainmodel.courses.Course;
import fi.pyramus.domainmodel.courses.CourseComponent;
import fi.pyramus.domainmodel.courses.CourseDescriptionCategory;
import fi.pyramus.domainmodel.courses.CourseEnrolmentType;
import fi.pyramus.domainmodel.courses.CourseParticipationType;
import fi.pyramus.domainmodel.courses.CourseState;
import fi.pyramus.domainmodel.courses.CourseUserRole;
import fi.pyramus.domainmodel.modules.Module;
import fi.pyramus.domainmodel.resources.Resource;
import fi.pyramus.domainmodel.resources.ResourceType;
import fi.pyramus.domainmodel.students.Student;
import fi.pyramus.domainmodel.users.User;
import fi.pyramus.json.JSONRequestController;
import fi.pyramus.persistence.usertypes.CourseOptionality;
import fi.pyramus.persistence.usertypes.MonetaryAmount;

public class CreateCourseJSONRequestController implements JSONRequestController {

  public void process(JSONRequestContext requestContext) {
    UserDAO userDAO = DAOFactory.getInstance().getUserDAO();
    StudentDAO studentDAO = DAOFactory.getInstance().getStudentDAO();
    BaseDAO baseDAO = DAOFactory.getInstance().getBaseDAO();
    CourseDAO courseDAO = DAOFactory.getInstance().getCourseDAO();
    ModuleDAO moduleDAO = DAOFactory.getInstance().getModuleDAO();
    ResourceDAO resourceDAO = DAOFactory.getInstance().getResourceDAO();

    // Course basic information

    String name = requestContext.getString("name");
    String nameExtension = requestContext.getString("nameExtension");
    Long courseStateId = requestContext.getLong("state");
    CourseState courseState = courseDAO.getCourseState(courseStateId);
    String description = requestContext.getString("description");
    Module module = moduleDAO.getModule(requestContext.getLong("module"));
    Subject subject = baseDAO.getSubject(requestContext.getLong("subject"));
    Integer courseNumber = requestContext.getInteger("courseNumber");
    Date beginDate = requestContext.getDate("beginDate");
    Date endDate = requestContext.getDate("endDate");
    Date enrolmentTimeEnd = requestContext.getDate("enrolmentTimeEnd");
    Double courseLength = requestContext.getDouble("courseLength");
    Long courseLengthTimeUnitId = requestContext.getLong("courseLengthTimeUnit");
    Long maxParticipantCount = requestContext.getLong("maxParticipantCount");
    EducationalTimeUnit courseLengthTimeUnit = baseDAO.findEducationalTimeUnitById(courseLengthTimeUnitId);
    Double distanceTeachingDays = requestContext.getDouble("distanceTeachingDays");
    Double localTeachingDays = requestContext.getDouble("localTeachingDays");
    Double teachingHours = requestContext.getDouble("teachingHours");
    Double planningHours = requestContext.getDouble("planningHours");
    Double assessingHours = requestContext.getDouble("assessingHours");
    User loggedUser = userDAO.getUser(requestContext.getLoggedUserId());
    String tagsText = requestContext.getString("tags");
    
    Set<Tag> tagEntities = new HashSet<Tag>();
    if (!StringUtils.isBlank(tagsText)) {
      List<String> tags = Arrays.asList(tagsText.split("[\\ ,]"));
      for (String tag : tags) {
        if (!StringUtils.isBlank(tag)) {
          Tag tagEntity = baseDAO.findTagByText(tag.trim());
          if (tagEntity == null)
            tagEntity = baseDAO.createTag(tag);
          tagEntities.add(tagEntity);
        }
      }
    }
    
    Course course = courseDAO.createCourse(module, name, nameExtension, courseState, subject, courseNumber, beginDate, endDate,
        courseLength, courseLengthTimeUnit, distanceTeachingDays, localTeachingDays, teachingHours, planningHours, assessingHours, 
        description, maxParticipantCount, enrolmentTimeEnd, loggedUser);

    // Tags
    
    courseDAO.setCourseTags(course, tagEntities);
    
    // Course Descriptions
    
    List<CourseDescriptionCategory> descriptionCategories = courseDAO.listCourseDescriptionCategories();
    
    for (CourseDescriptionCategory cat: descriptionCategories) {
      String varName = "courseDescription." + cat.getId().toString();
      Long descriptionCatId = requestContext.getLong(varName + ".catId");
      String descriptionText = requestContext.getString(varName + ".text");

      if ((descriptionCatId != null) && (descriptionCatId.intValue() != -1)) {
        // Description has been submitted from form 
        courseDAO.createCourseDescription(course, cat, descriptionText);
      }
    }
    
    // Personnel

    int rowCount = requestContext.getInteger("personnelTable.rowCount");
    for (int i = 0; i < rowCount; i++) {
      String colPrefix = "personnelTable." + i;
      Long userId = requestContext.getLong(colPrefix + ".userId");
      Long roleId = requestContext.getLong(colPrefix + ".roleId");
      User user = userDAO.getUser(userId);
      CourseUserRole role = courseDAO.getCourseUserRole(roleId);
      courseDAO.createCourseUser(course, user, role);
    }
    
    // Course components

    rowCount = requestContext.getInteger("components.componentCount").intValue();
    for (int i = 0; i < rowCount; i++) {
      String colPrefix = "components.component." + i;
      String componentName = requestContext.getString(colPrefix + ".0.name");
      Double componentLength = requestContext.getDouble(colPrefix + ".0.length");
      String componentDescription = requestContext.getString(colPrefix + ".0.description");

      // TODO Component length; should be just hours but it currently depends on the default time unit - ok?  
      EducationalTimeUnit componentTimeUnit = baseDAO.getDefaults().getBaseTimeUnit();

      CourseComponent courseComponent = courseDAO.createCourseComponent(course, componentLength, componentTimeUnit, componentName,
          componentDescription);
                        
      Long resourceCategoryCount = requestContext.getLong(colPrefix + ".resourceCategoryCount");
      for (int categoryIndex = 0; categoryIndex < resourceCategoryCount; categoryIndex++) {
        String resourcesPrefix = colPrefix + "." + categoryIndex + ".resources";
        Long resourcesCount = requestContext.getLong(resourcesPrefix + ".rowCount");
        
        for (int j = 0; j < resourcesCount; j++) {
          String resourcePrefix = resourcesPrefix + "." + j;
          
          Long resourceId = requestContext.getLong(resourcePrefix + ".resourceId");
          Resource resource = resourceDAO.findResourceById(resourceId);
          Double usagePercent;
          
          if (resource.getResourceType() == ResourceType.MATERIAL_RESOURCE) {
            usagePercent = requestContext.getDouble(resourcePrefix + ".quantity") * 100;
          } else {
            usagePercent = requestContext.getDouble(resourcePrefix + ".usage");
          }
          
          courseDAO.createCourseComponentResource(courseComponent, resource, usagePercent);
        }
      }
    }

    // Education types and subtypes submitted from the web page

    Map<Long, Vector<Long>> chosenEducationTypes = new HashMap<Long, Vector<Long>>();
    Enumeration<String> parameterNames = requestContext.getRequest().getParameterNames();
    while (parameterNames.hasMoreElements()) {
      name = (String) parameterNames.nextElement();
      if (name.startsWith("educationType.")) {
        String[] nameElements = name.split("\\.");
        Long educationTypeId = new Long(nameElements[1]);
        Long educationSubtypeId = new Long(nameElements[2]);
        Vector<Long> v = chosenEducationTypes.containsKey(educationTypeId) ? chosenEducationTypes.get(educationTypeId) : new Vector<Long>();
        v.add(educationSubtypeId);
        if (!chosenEducationTypes.containsKey(educationTypeId)) {
          chosenEducationTypes.put(educationTypeId, v);
        }
      }
    }
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

    // Basic course resources

    int basicResourcesTableRowCount = requestContext.getInteger("basicResourcesTable.rowCount");
    for (int i = 0; i < basicResourcesTableRowCount; i++) {
      String colPrefix = "basicResourcesTable." + i;
      Double hours = requestContext.getDouble(colPrefix + ".hours");
      if (hours == null) {
        hours = 0.0;
      }
      MonetaryAmount hourlyCost = new MonetaryAmount(requestContext.getDouble(colPrefix + ".hourlyCost"));
      Integer units = requestContext.getInteger(colPrefix + ".units");
      if (units == null) {
        units = 0;
      }
      MonetaryAmount unitCost = new MonetaryAmount(requestContext.getDouble(colPrefix + ".unitCost"));
      Long resourceId = requestContext.getLong(colPrefix + ".resourceId");
      Resource resource = resourceDAO.findResourceById(resourceId);
      courseDAO.createBasicCourseResource(course, resource, hours, hourlyCost, units, unitCost);
    }

    // Student course resources

    int studentResourcesTableRowCount = requestContext.getInteger("studentResourcesTable.rowCount");
    for (int i = 0; i < studentResourcesTableRowCount; i++) {
      String colPrefix = "studentResourcesTable." + i;
      Double hours = requestContext.getDouble(colPrefix + ".hours");
      if (hours == null) {
        hours = 0.0;
      }
      MonetaryAmount hourlyCost = new MonetaryAmount(requestContext.getDouble(colPrefix + ".hourlyCost"));
      MonetaryAmount unitCost = new MonetaryAmount(requestContext.getDouble(colPrefix + ".unitCost"));
      Long resourceId = NumberUtils.createLong(requestContext.getRequest().getParameter(colPrefix + ".resourceId"));
      Resource resource = resourceDAO.findResourceById(resourceId);
      courseDAO.createStudentCourseResource(course, resource, hours, hourlyCost, unitCost);
    }

    // Grade course resources

    int gradeResourcesTableRowCount = requestContext.getInteger("gradeResourcesTable.rowCount");
    for (int i = 0; i < gradeResourcesTableRowCount; i++) {
      String colPrefix = "gradeResourcesTable." + i;
      Double hours = requestContext.getDouble(colPrefix + ".hours");
      if (hours == null) {
        hours = 0.0;
      }
      MonetaryAmount hourlyCost = new MonetaryAmount(requestContext.getDouble(colPrefix + ".hourlyCost"));
      MonetaryAmount unitCost = new MonetaryAmount(requestContext.getDouble(colPrefix + ".unitCost"));
      Long resourceId = requestContext.getLong(colPrefix + ".resourceId");
      Resource resource = resourceDAO.findResourceById(resourceId);
      courseDAO.createGradeCourseResource(course, resource, hours, hourlyCost, unitCost);
    }

    // Other costs

    int otherCostsTableRowCount = requestContext.getInteger("otherCostsTable.rowCount");
    for (int i = 0; i < otherCostsTableRowCount; i++) {
      String colPrefix = "otherCostsTable." + i;
      name = requestContext.getString(colPrefix + ".name");
      MonetaryAmount cost = new MonetaryAmount(requestContext.getDouble(colPrefix + ".cost"));
      courseDAO.createOtherCost(course, name, cost);
    }

    // Students

    int studentsTableRowCount = requestContext.getInteger("studentsTable.rowCount");
    for (int i = 0; i < studentsTableRowCount; i++) {
      String colPrefix = "studentsTable." + i;

      Long studentId = requestContext.getLong(colPrefix + ".studentId");
      Date enrolmentDate = requestContext.getDate(colPrefix + ".enrolmentDate");
      Long enrolmentTypeId = requestContext.getLong(colPrefix + ".enrolmentType");
      Long participationTypeId = requestContext.getLong(colPrefix + ".participationType");
      Boolean lodging = requestContext.getBoolean(colPrefix + ".lodging");

      Student student = studentDAO.getStudent(studentId);
      CourseEnrolmentType enrolmentType = enrolmentTypeId != null ? courseDAO.getCourseEnrolmentType(enrolmentTypeId) : null;
      CourseParticipationType participationType = participationTypeId != null ? courseDAO.getCourseParticipationType(participationTypeId) : null;
      CourseOptionality optionality = (CourseOptionality) requestContext.getEnum(colPrefix + ".optionality", CourseOptionality.class);
      courseDAO.createCourseStudent(course, student, enrolmentType, participationType, enrolmentDate, lodging, optionality);
    }
    
    String redirectURL = requestContext.getRequest().getContextPath() + "/courses/editcourse.page?course=" + course.getId();
    String refererAnchor = requestContext.getRefererAnchor();
    
    if (!StringUtils.isBlank(refererAnchor))
      redirectURL += "#" + refererAnchor;

    requestContext.setRedirectURL(redirectURL);
  }

  public UserRole[] getAllowedRoles() {
    return new UserRole[] { UserRole.MANAGER, UserRole.ADMINISTRATOR };
  }

}