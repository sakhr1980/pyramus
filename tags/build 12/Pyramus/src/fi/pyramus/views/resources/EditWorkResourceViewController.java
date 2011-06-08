package fi.pyramus.views.resources;

import java.util.Iterator;
import java.util.Locale;

import org.apache.commons.lang.math.NumberUtils;

import fi.pyramus.PageRequestContext;
import fi.pyramus.I18N.Messages;
import fi.pyramus.breadcrumbs.Breadcrumbable;
import fi.pyramus.dao.DAOFactory;
import fi.pyramus.dao.ResourceDAO;
import fi.pyramus.domainmodel.base.Tag;
import fi.pyramus.domainmodel.resources.Resource;
import fi.pyramus.UserRole;
import fi.pyramus.views.PyramusViewController;

public class EditWorkResourceViewController implements PyramusViewController, Breadcrumbable {

  public void process(PageRequestContext pageRequestContext) {
    ResourceDAO resourceDAO = DAOFactory.getInstance().getResourceDAO();

    Long resourceId = NumberUtils.createLong(pageRequestContext.getRequest().getParameter("resource"));
    Resource resource = resourceDAO.findResourceById(resourceId);

    StringBuilder tagsBuilder = new StringBuilder();
    Iterator<Tag> tagIterator = resource.getTags().iterator();
    while (tagIterator.hasNext()) {
      Tag tag = tagIterator.next();
      tagsBuilder.append(tag.getText());
      if (tagIterator.hasNext())
        tagsBuilder.append(' ');
    }
    
    pageRequestContext.getRequest().setAttribute("tags", tagsBuilder.toString());
    pageRequestContext.getRequest().setAttribute("categories", resourceDAO.listResourceCategories());
    pageRequestContext.getRequest().setAttribute("resource", resourceDAO.findWorkResourceById(resource.getId()));
    pageRequestContext.setIncludeJSP("/templates/resources/editworkresource.jsp");
  }

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
    return Messages.getInstance().getText(locale, "resources.editWorkResource.pageTitle");
  }

}