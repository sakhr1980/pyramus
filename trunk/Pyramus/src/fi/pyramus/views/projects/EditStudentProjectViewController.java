package fi.pyramus.views.projects;

import java.util.Iterator;
import java.util.Locale;

import org.apache.commons.lang.math.NumberUtils;

import fi.pyramus.PageRequestContext;
import fi.pyramus.I18N.Messages;
import fi.pyramus.breadcrumbs.Breadcrumbable;
import fi.pyramus.dao.BaseDAO;
import fi.pyramus.dao.DAOFactory;
import fi.pyramus.dao.ProjectDAO;
import fi.pyramus.dao.UserDAO;
import fi.pyramus.domainmodel.base.Tag;
import fi.pyramus.domainmodel.projects.StudentProject;
import fi.pyramus.UserRole;
import fi.pyramus.views.PyramusViewController;

/**
 * The controller responsible of the Edit Student Project view of the application.
 */
public class EditStudentProjectViewController implements PyramusViewController, Breadcrumbable {
  
  /**
   * Processes the page request by including the corresponding JSP page to the response.
   * 
   * @param pageRequestContext Page request context
   */
  public void process(PageRequestContext pageRequestContext) {
    UserDAO userDAO = DAOFactory.getInstance().getUserDAO();
    BaseDAO baseDAO = DAOFactory.getInstance().getBaseDAO();
    ProjectDAO projectDAO = DAOFactory.getInstance().getProjectDAO();

    Long studentProjectId = NumberUtils.createLong(pageRequestContext.getRequest().getParameter("studentproject"));
    StudentProject studentProject = projectDAO.getStudentProject(studentProjectId);
    
    StringBuilder tagsBuilder = new StringBuilder();
    Iterator<Tag> tagIterator = studentProject.getTags().iterator();
    while (tagIterator.hasNext()) {
      Tag tag = tagIterator.next();
      tagsBuilder.append(tag.getText());
      if (tagIterator.hasNext())
        tagsBuilder.append(' ');
    }
    
    pageRequestContext.getRequest().setAttribute("tags", tagsBuilder.toString());
    pageRequestContext.getRequest().setAttribute("studentProject", studentProject);
    pageRequestContext.getRequest().setAttribute("optionalStudiesLengthTimeUnits", baseDAO.listEducationalTimeUnits());
    pageRequestContext.getRequest().setAttribute("academicTerms", baseDAO.listAcademicTerms());
    pageRequestContext.getRequest().setAttribute("users", userDAO.listUsers());

    pageRequestContext.setIncludeJSP("/templates/projects/editstudentproject.jsp");
  }

  /**
   * Returns the roles allowed to access this page. Editing student projects is available for users with
   * {@link Role#MANAGER} or {@link Role#ADMINISTRATOR} privileges.
   * 
   * @return The roles allowed to access this page
   */
  public UserRole[] getAllowedRoles() {
    return new UserRole[] { UserRole.MANAGER, UserRole.ADMINISTRATOR };
  }

  /**
   * Returns the localized name of this page. Used e.g. for breadcrumb navigation.
   * 
   * @param locale The locale to be used for the name
   * 
   * @return The localized name of this page
   */
  public String getName(Locale locale) {
    return Messages.getInstance().getText(locale, "projects.editStudentProject.pageTitle");
  }

}
