package fi.pyramus.views.system;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import fi.pyramus.PageRequestContext;
import fi.pyramus.UserRole;
import fi.pyramus.dao.DAOFactory;
import fi.pyramus.dao.SystemDAO;
import fi.pyramus.domainmodel.system.Setting;
import fi.pyramus.domainmodel.system.SettingKey;
import fi.pyramus.views.PyramusFormViewController;

/**
 * The controller responsible of the system settings view of the application.
 */
public class SystemSettingsViewController extends PyramusFormViewController {

  @Override
  public void processForm(PageRequestContext requestContext) {
    SystemDAO systemDAO = DAOFactory.getInstance().getSystemDAO();
    
    Map<String, String> settings = new HashMap<String, String>();
    List<SettingKey> settingKeys = systemDAO.listSettingKeys();
    for (SettingKey settingKey : settingKeys) {
      Setting setting = systemDAO.findSettingByKey(settingKey);
      if (setting != null)
        settings.put(settingKey.getName(), setting.getValue());
    }
    
    requestContext.getRequest().setAttribute("settingKeys", settingKeys);
    requestContext.getRequest().setAttribute("settings", settings);
    
    requestContext.setIncludeJSP("/templates/system/systemsettings.jsp");
  }
  
  @Override
  public void processSend(PageRequestContext requestContext) {
    SystemDAO systemDAO = DAOFactory.getInstance().getSystemDAO();
    
    Long rowCount = requestContext.getLong("settingsTable.rowCount");
    for (int i = 0; i < rowCount; i++) {
      String colPrefix = "settingsTable." + i;
      
      String key = requestContext.getString(colPrefix + ".key");
      String value = requestContext.getString(colPrefix + ".value");
      boolean hasValue = !StringUtils.isBlank(value);
      
      SettingKey settingKey = systemDAO.findSettingKeyByName(key);
      Setting setting = systemDAO.findSettingByKey(settingKey);
      if (setting != null) {
        if (!hasValue)
          systemDAO.deleteSetting(setting);
        else
          systemDAO.updateSetting(setting, settingKey, value);
      } else {
        if (hasValue)
          systemDAO.createSetting(settingKey, value);
      }
    }
    
    requestContext.setRedirectURL(requestContext.getRequest().getContextPath() + "/system/systemsettings.page");
  }
  
 
  /**
   * Returns the roles allowed to access this page.
   * 
   * @return The roles allowed to access this page
   */
  public UserRole[] getAllowedRoles() {
    return new UserRole[] { UserRole.ADMINISTRATOR };
  }

}
