package fi.pyramus.views.system;

import java.util.Date;

import org.apache.commons.lang.StringUtils;

import fi.pyramus.PageRequestContext;
import fi.pyramus.dao.BaseDAO;
import fi.pyramus.dao.CourseDAO;
import fi.pyramus.dao.DAOFactory;
import fi.pyramus.dao.ModuleDAO;
import fi.pyramus.dao.ProjectDAO;
import fi.pyramus.dao.ResourceDAO;
import fi.pyramus.dao.StudentDAO;
import fi.pyramus.dao.UserDAO;
import fi.pyramus.domainmodel.base.EducationalTimeUnit;
import fi.pyramus.domainmodel.courses.CourseState;
import fi.pyramus.domainmodel.resources.ResourceCategory;
import fi.pyramus.domainmodel.students.AbstractStudent;
import fi.pyramus.UserRole;
import fi.pyramus.domainmodel.users.User;
import fi.pyramus.persistence.usertypes.Sex;
import fi.pyramus.views.PyramusViewController;

public class DebugDataViewController implements PyramusViewController {

  public void process(PageRequestContext requestContext) {
    UserDAO userDAO = DAOFactory.getInstance().getUserDAO();
    StudentDAO studentDAO = DAOFactory.getInstance().getStudentDAO();
    BaseDAO baseDAO = DAOFactory.getInstance().getBaseDAO();
    CourseDAO courseDAO = DAOFactory.getInstance().getCourseDAO();
    ModuleDAO moduleDAO = DAOFactory.getInstance().getModuleDAO();
    ProjectDAO projectDAO = DAOFactory.getInstance().getProjectDAO();
    ResourceDAO resourceDAO = DAOFactory.getInstance().getResourceDAO();

    String type = requestContext.getRequest().getParameter("type");
    int count = Integer.parseInt(requestContext.getRequest().getParameter("count"));
    int start = 1;
    String s = requestContext.getRequest().getParameter("start");
    if (!StringUtils.isBlank(s)) {
      start = Integer.parseInt(s);
    }

    User user = userDAO.getUser(requestContext.getLoggedUserId());
    
    if ("module".equals(type)) {
      for (int i = start; i < (start + count); i++) {
        EducationalTimeUnit etu = baseDAO.findEducationalTimeUnitById(new Long(1));
        moduleDAO.createModule("Moduli " + i, null, null, new Double(10), etu, "Kuvaustekstiä modulille " + i, null, user);
      }
    }
    else if ("course".equals(type)) {
      for (int i = start; i < (start + count); i++) {
        EducationalTimeUnit etu = baseDAO.findEducationalTimeUnitById(new Long(1));
        CourseState courseState = courseDAO.getCourseState(new Long(1));
        courseDAO.createCourse(moduleDAO.getModule(new Long(1)), "Kurssi " + i, "", courseState, null, null, null, null, new Double(10), etu, null, null, null, null, null, "Kuvaustekstiä kurssille " + i, null, null, user);
      }
    }
    else if ("resource".equals(type)) {
      for (int i = start; i < (start + count); i++) {
        ResourceCategory resourceCategory = resourceDAO.findResourceCategoryById(new Long(1));
        resourceDAO.createMaterialResource("Materiaaliresurssi " + i, resourceCategory, new Double(500));
      }
    }
    else if ("project".equals(type)) {
      for (int i = start; i < (start + count); i++) {
        EducationalTimeUnit etu = baseDAO.findEducationalTimeUnitById(new Long(1));
        projectDAO.createProject("Projekti " + i, "Kuvaustekstiä projektille " + i, new Double(10), etu, user);
      }
    }
    else if ("student".equals(type)) {
      for (int i = start; i < (start + count); i++) {
        AbstractStudent abstractStudent = studentDAO.createAbstractStudent(new Date(), "030310-123R", Sex.MALE, null);
        studentDAO.createStudent(abstractStudent, "Etunimi " + i, "Sukunimi " + i, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, Boolean.FALSE);
      }
    }
  }

  public UserRole[] getAllowedRoles() {
    return new UserRole[] { UserRole.ADMINISTRATOR };
  }

}
