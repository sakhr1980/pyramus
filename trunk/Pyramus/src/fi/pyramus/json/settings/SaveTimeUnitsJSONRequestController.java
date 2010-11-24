package fi.pyramus.json.settings;

import fi.pyramus.ErrorLevel;
import fi.pyramus.JSONRequestContext;
import fi.pyramus.PyramusRuntimeException;
import fi.pyramus.StatusCode;
import fi.pyramus.dao.BaseDAO;
import fi.pyramus.dao.DAOFactory;
import fi.pyramus.domainmodel.base.EducationalTimeUnit;
import fi.pyramus.UserRole;
import fi.pyramus.json.JSONRequestController;

public class SaveTimeUnitsJSONRequestController implements JSONRequestController {

  public void process(JSONRequestContext jsonRequestContext) {
    BaseDAO baseDAO = DAOFactory.getInstance().getBaseDAO();
    
    EducationalTimeUnit baseTimeUnit = null;

    int rowCount = jsonRequestContext.getInteger("timeUnitsTable.rowCount").intValue();
    for (int i = 0; i < rowCount; i++) {
      EducationalTimeUnit timeUnit;
      
      String colPrefix = "timeUnitsTable." + i;
      Long timeUnitId = jsonRequestContext.getLong(colPrefix + ".timeUnitId");
      
      Boolean baseUnit = "1".equals(jsonRequestContext.getString(colPrefix + ".baseUnit"));
      Double baseUnits = jsonRequestContext.getDouble(colPrefix + ".baseUnits");
      String name = jsonRequestContext.getRequest().getParameter(colPrefix + ".name");
      
      if (baseUnit) {
        baseUnits = new Double(1);
      }
        
      if (timeUnitId == -1) {
        timeUnit = baseDAO.createEducationalTimeUnit(baseUnits, name); 
      } else {
        timeUnit = baseDAO.findEducationalTimeUnitById(timeUnitId);
        baseDAO.updateEducationalTimeUnit(timeUnit, baseUnits, name);
      }
      
      if (baseUnit) {
        if (baseTimeUnit != null)
          throw new PyramusRuntimeException(ErrorLevel.ERROR, StatusCode.UNDEFINED, "Two or more baseTimeUnits defined");
          
        baseTimeUnit = timeUnit;
      }
    }
    
    if (baseTimeUnit != null) {
      if (!baseTimeUnit.equals(baseDAO.getDefaults().getBaseTimeUnit())) {
        baseDAO.updateDefaultBaseTimeUnit(baseTimeUnit);
      }
        
    }
    
    jsonRequestContext.setRedirectURL(jsonRequestContext.getReferer(true));
  }

  public UserRole[] getAllowedRoles() {
    return new UserRole[] { UserRole.MANAGER, UserRole.ADMINISTRATOR };
  }

}
