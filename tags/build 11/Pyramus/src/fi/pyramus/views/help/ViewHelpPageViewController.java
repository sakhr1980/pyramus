package fi.pyramus.views.help;

import java.util.Locale;

import fi.pyramus.PageRequestContext;
import fi.pyramus.I18N.Messages;
import fi.pyramus.breadcrumbs.Breadcrumbable;
import fi.pyramus.dao.DAOFactory;
import fi.pyramus.dao.HelpDAO;
import fi.pyramus.domainmodel.help.HelpItemTitle;
import fi.pyramus.domainmodel.help.HelpPage;
import fi.pyramus.domainmodel.help.HelpPageContent;
import fi.pyramus.UserRole;
import fi.pyramus.views.PyramusViewController;

public class ViewHelpPageViewController implements PyramusViewController, Breadcrumbable {

  public void process(PageRequestContext requestContext) {
    HelpDAO helpDAO = DAOFactory.getInstance().getHelpDAO();

    Long pageId = requestContext.getLong("page");
    
    HelpPage helpPage = helpDAO.findHelpPageById(pageId);
    
    HelpItemTitle itemTitle = helpPage.getTitleByLocale(requestContext.getRequest().getLocale());
    if (itemTitle == null)
      itemTitle = helpPage.getTitles().get(0);
    
    HelpPageContent pageContent = helpPage.getContentByLocale(requestContext.getRequest().getLocale());
    if (pageContent == null)
      pageContent = helpPage.getContents().get(0);
    

    requestContext.getRequest().setAttribute("title", itemTitle.getTitle());
    requestContext.getRequest().setAttribute("content", pageContent.getContent());
    
    requestContext.setIncludeJSP("/templates/help/viewhelppage.jsp");
  }

  public UserRole[] getAllowedRoles() {
    return new UserRole[] { UserRole.EVERYONE };
  }

  /**
   * Returns the localized name of this page. Used e.g. for breadcrumb navigation.
   * 
   * @param locale The locale to be used for the name
   * 
   * @return The localized name of this page
   */
  public String getName(Locale locale) {
    return Messages.getInstance().getText(locale, "help.viewHelpPage.pageTitle");
  }

}
