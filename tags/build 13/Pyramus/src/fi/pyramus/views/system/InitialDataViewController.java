package fi.pyramus.views.system;

import java.util.Arrays;

import org.apache.commons.lang.StringUtils;

import fi.pyramus.PageRequestContext;
import fi.pyramus.PyramusRuntimeException;
import fi.pyramus.dao.BaseDAO;
import fi.pyramus.dao.DAOFactory;
import fi.pyramus.UserRole;
import fi.pyramus.util.DataImporter;
import fi.pyramus.views.PyramusViewController;

public class InitialDataViewController implements PyramusViewController {

  public void process(PageRequestContext requestContext) {
    BaseDAO baseDAO = DAOFactory.getInstance().getBaseDAO();

    if (baseDAO.isPyramusInitialized()) 
      throw new PyramusRuntimeException(new Exception("Pyramus is already initialized."));
    
    DataImporter dataImporter = new DataImporter();
    String classes = requestContext.getRequest().getParameter("classes");
    if (StringUtils.isEmpty(classes)) {
      dataImporter.importDataFromFile(System.getProperty("appdirectory") + "initialdata.xml", null);
    } else {
      dataImporter.importDataFromFile(System.getProperty("appdirectory") + "initialdata.xml", Arrays.asList(classes.split(",")));
    }
    
    requestContext.setRedirectURL(requestContext.getRequest().getContextPath() + "/system/reindexhibernateobjects.page");
  }

  public UserRole[] getAllowedRoles() {
    return new UserRole[] { UserRole.EVERYONE };
  }

}