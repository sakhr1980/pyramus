package fi.pyramus.json.modules;

import java.util.Arrays;
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
import fi.pyramus.dao.ModuleDAO;
import fi.pyramus.dao.UserDAO;
import fi.pyramus.domainmodel.base.CourseEducationSubtype;
import fi.pyramus.domainmodel.base.CourseEducationType;
import fi.pyramus.domainmodel.base.EducationSubtype;
import fi.pyramus.domainmodel.base.EducationType;
import fi.pyramus.domainmodel.base.EducationalTimeUnit;
import fi.pyramus.domainmodel.base.Subject;
import fi.pyramus.domainmodel.base.Tag;
import fi.pyramus.domainmodel.courses.CourseDescription;
import fi.pyramus.domainmodel.courses.CourseDescriptionCategory;
import fi.pyramus.domainmodel.modules.Module;
import fi.pyramus.domainmodel.users.User;
import fi.pyramus.json.JSONRequestController;

/**
 * The controller responsible of modifying an existing module. 
 * 
 * @see fi.pyramus.views.modules.EditModuleViewController
 */
public class EditModuleJSONRequestController implements JSONRequestController {

  public void process(JSONRequestContext requestContext) {
    UserDAO userDAO = DAOFactory.getInstance().getUserDAO();
    BaseDAO baseDAO = DAOFactory.getInstance().getBaseDAO();
    CourseDAO courseDAO = DAOFactory.getInstance().getCourseDAO();
    ModuleDAO moduleDAO = DAOFactory.getInstance().getModuleDAO();

    Long moduleId = requestContext.getLong("moduleId");
    
    Module module = moduleDAO.getModule(moduleId);

    Long version = requestContext.getLong("version");
    if (!module.getVersion().equals(version))
      throw new StaleObjectStateException(Module.class.getName(), module.getId());
    
    // Education types and subtypes submitted from the web page

    Map<Long, Vector<Long>> chosenEducationTypes = new HashMap<Long, Vector<Long>>();
    Enumeration<String> parameterNames = requestContext.getRequest().getParameterNames();
    while (parameterNames.hasMoreElements()) {
      String name = (String) parameterNames.nextElement();
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

    // Course Descriptions
    
    List<CourseDescriptionCategory> descriptionCategories = courseDAO.listCourseDescriptionCategories();
    Set<CourseDescription> nonExistingDescriptions = new HashSet<CourseDescription>();
    
    for (CourseDescriptionCategory cat: descriptionCategories) {
      String varName = "courseDescription." + cat.getId().toString();
      Long descriptionCatId = requestContext.getLong(varName + ".catId");
      String descriptionText = requestContext.getString(varName + ".text");

      CourseDescription oldDesc = courseDAO.findCourseDescriptionByCourseAndCategory(module, cat);

      if ((descriptionCatId != null) && (descriptionCatId.intValue() != -1)) {
        // Description has been submitted from form 
        if (oldDesc != null)
          courseDAO.updateCourseDescription(oldDesc, module, cat, descriptionText);
        else
          courseDAO.createCourseDescription(module, cat, descriptionText);
      } else {
        // Description wasn't submitted from form, if it exists, it's marked for deletion 
        if (oldDesc != null)
          nonExistingDescriptions.add(oldDesc);
      }
    }
    
    // Delete non existing descriptions
    for (CourseDescription desc: nonExistingDescriptions) {
      courseDAO.deleteCourseDescription(desc);
    }
    
    // Remove education types and subtypes

    List<CourseEducationType> courseEducationTypes = module.getCourseEducationTypes();
    for (int i = courseEducationTypes.size() - 1; i >= 0; i--) {
      CourseEducationType courseEducationType = courseEducationTypes.get(i);
      if (!chosenEducationTypes.containsKey(courseEducationType.getEducationType().getId())) {
        courseDAO.removeCourseEducationType(courseEducationType);
      }
      else {
        Vector<Long> v = chosenEducationTypes.get(courseEducationType.getEducationType().getId());
        List<CourseEducationSubtype> courseEducationSubtypes = courseEducationType.getCourseEducationSubtypes();
        for (int j = courseEducationSubtypes.size() - 1; j >= 0; j--) {
          CourseEducationSubtype moduleEducationSubtype = courseEducationSubtypes.get(j);
          if (!v.contains(moduleEducationSubtype.getEducationSubtype().getId())) {
            courseEducationType.removeSubtype(moduleEducationSubtype);
          }
        }
      }
    }

    // Add education types and subtypes

    for (Long educationTypeId : chosenEducationTypes.keySet()) {
      EducationType educationType = baseDAO.getEducationType(educationTypeId);
      CourseEducationType courseEducationType;
      if (!module.contains(educationType)) {
        courseEducationType = courseDAO.addCourseEducationType(module, educationType);
      }
      else {
        courseEducationType = module.getCourseEducationTypeByEducationTypeId(educationTypeId);
      }
      for (Long educationSubtypeId : chosenEducationTypes.get(educationTypeId)) {
        EducationSubtype educationSubtype = educationType.getEducationSubtypeById(educationSubtypeId);
        if (!courseEducationType.contains(educationSubtype)) {
          courseDAO.addCourseEducationSubtype(courseEducationType, educationSubtype);
        }
      }
    }

    // Module components

    int rowCount = requestContext.getInteger("componentsTable.rowCount");
    for (int i = 0; i < rowCount; i++) {
      String colPrefix = "componentsTable." + i;
      String componentName = requestContext.getString(colPrefix + ".name");
      Double componentLength = requestContext.getDouble(colPrefix + ".length");
      String componentDescription = requestContext.getString(colPrefix + ".description");
      Long componentId = requestContext.getLong(colPrefix + ".componentId");

      // TODO Component length; should be just hours but it currently depends on the default time unit - ok?  
      EducationalTimeUnit componentTimeUnit = baseDAO.getDefaults().getBaseTimeUnit();

      if (componentId == -1) {
        componentId = moduleDAO.createModuleComponent(module, componentLength, componentTimeUnit, componentName,
            componentDescription).getId();
      }
      else {
        moduleDAO.updateModuleComponent(moduleDAO.getModuleComponent(componentId), componentLength, componentTimeUnit,
            componentName, componentDescription);
      }
    }

    // Module basic information

    Long subjectId = requestContext.getLong("subject");
    Subject subject = baseDAO.getSubject(subjectId);
    Integer courseNumber = requestContext.getInteger("courseNumber");
    String name = requestContext.getString("name");
    String description = requestContext.getString("description");
    User loggedUser = userDAO.getUser(requestContext.getLoggedUserId());
    Double moduleLength = requestContext.getDouble("moduleLength");
    Long moduleLengthTimeUnitId = requestContext.getLong("moduleLengthTimeUnit");
    Long maxParticipantCount = requestContext.getLong("maxParticipantCount");
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
    
    EducationalTimeUnit moduleLengthTimeUnit = baseDAO.findEducationalTimeUnitById(moduleLengthTimeUnitId);

    
    moduleDAO.updateModule(module, name, subject, courseNumber, moduleLength, moduleLengthTimeUnit, description, maxParticipantCount, loggedUser);

    // Tags

    moduleDAO.setModuleTags(module, tagEntities);
    
    requestContext.setRedirectURL(requestContext.getReferer(true));
  }

  public UserRole[] getAllowedRoles() {
    return new UserRole[] { UserRole.MANAGER, UserRole.ADMINISTRATOR };
  }

}
