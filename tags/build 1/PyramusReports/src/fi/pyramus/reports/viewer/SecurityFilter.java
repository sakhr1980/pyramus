package fi.pyramus.reports.viewer;

import java.io.IOException;
import java.util.Date;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.apache.commons.lang.math.NumberUtils;

import fi.pyramus.dao.BaseDAO;
import fi.pyramus.dao.DAOFactory;
import fi.pyramus.dao.SystemDAO;
import fi.pyramus.domainmodel.base.MagicKey;
import fi.pyramus.domainmodel.system.Setting;
import fi.pyramus.domainmodel.system.SettingKey;

public class SecurityFilter implements Filter {
  
  public void init(FilterConfig fc) throws ServletException {
  }

  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
    SystemDAO systemDAO = DAOFactory.getInstance().getSystemDAO();
    BaseDAO baseDAO = DAOFactory.getInstance().getBaseDAO();

    /**
     * If expire time is not set it defaults to 60000 milliseconds (1 minute)
     */
    long expireMills = 60000;
    SettingKey settingKey = systemDAO.findSettingKeyByName("reports.magicKeyExpireMills");
    if (settingKey != null) {
      Setting setting = systemDAO.findSettingByKey(settingKey);
      if (setting != null && NumberUtils.isNumber(setting.getValue())) 
        expireMills = NumberUtils.createLong(setting.getValue());
    }
    
    Date expireThreshold = new Date(System.currentTimeMillis());
    expireThreshold.setTime(expireThreshold.getTime() - expireMills);
    String magicKeyName = request.getParameter("magicKey");
    
    MagicKey magicKey = baseDAO.findMagicKeyByName(magicKeyName);
    if (magicKey != null) {
      try {
        if (magicKey.getCreated().after(expireThreshold)) {
          chain.doFilter(request, response);
        } else {
          throw new ServletException("Session expired");
        }
      } finally {
        baseDAO.deleteMagicKey(magicKey);
      }
    } else {
      throw new ServletException("Permission denied");
    }
  }

  public void destroy() {
  }

}