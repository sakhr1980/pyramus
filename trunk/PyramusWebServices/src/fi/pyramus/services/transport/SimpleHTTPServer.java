package fi.pyramus.services.transport;

import org.apache.axis2.AxisFault;
import org.apache.axis2.context.ConfigurationContext;
import org.apache.axis2.description.Parameter;
import org.apache.axis2.description.TransportInDescription;

import fi.pyramus.dao.DAOFactory;
import fi.pyramus.dao.SystemDAO;
import fi.pyramus.domainmodel.system.Setting;
import fi.pyramus.domainmodel.system.SettingKey;

public class SimpleHTTPServer extends org.apache.axis2.transport.http.SimpleHTTPServer {

  private String getSetting(String key) {
    SystemDAO systemDAO = DAOFactory.getInstance().getSystemDAO();
    
    SettingKey settingKey = systemDAO.findSettingKeyByName(key);  
    if (settingKey != null) {
      Setting setting = systemDAO.findSettingByKey(settingKey);
      if (setting != null)
        return setting.getValue();
    }
  
    return null;
  }
  
  @Override
  public void init(ConfigurationContext axisConf, TransportInDescription transprtIn) throws AxisFault {
    String method = getSetting("reports.method");
    String host = getSetting("reports.host");
    String port = getSetting("reports.port");
    
    if (method != null && !"".equals(method) && !"".equals(host) && !"".equals(host) && port != null && !"".equals(port)) {
      String hostAddress = new StringBuilder()
        .append(method)
        .append("://")
        .append(host)
        .append(':')
        .append(port).toString();
      
      Parameter hostParameter = transprtIn.getParameter(HOST_ADDRESS);
      if (hostParameter != null) {
        hostParameter.setValue(hostAddress);
      } else {
        transprtIn.addParameter(new Parameter(HOST_ADDRESS, hostAddress));
      }
      
      Parameter portParameter = transprtIn.getParameter(PARAM_PORT);
      if (portParameter != null) {
        portParameter.setValue(port);
      } else {
        transprtIn.addParameter(new Parameter(PARAM_PORT, port));
      }
    }
    
    super.init(axisConf, transprtIn);
  }  
}