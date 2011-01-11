package fi.pyramus.views.system;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;

import org.hibernate.stat.Statistics;

import fi.pyramus.PageRequestContext;
import fi.pyramus.UserRole;
import fi.pyramus.dao.DAOFactory;
import fi.pyramus.dao.SystemDAO;
import fi.pyramus.views.PyramusViewController;

public class HibernateStatisticsViewController implements PyramusViewController {

  public void process(PageRequestContext requestContext) {
    SystemDAO systemDAO = DAOFactory.getInstance().getSystemDAO();
    Statistics statistics = systemDAO.getHibernateStatistics();
    requestContext.getRequest().setAttribute("statistics", statistics); 
 
    CacheManager cacheManager = CacheManager.getInstance();
    String[] cacheNames = cacheManager.getCacheNames();
    
    
    Set<Map<String, Object>> cacheInfos = new HashSet<Map<String,Object>>();
    
    for (String cacheName : cacheNames) {
      Cache cache = cacheManager.getCache(cacheName);
      net.sf.ehcache.Statistics cacheStatistics = cache.getStatistics();
      
      Map<String, Object> cacheInfo = new HashMap<String, Object>();
      
      cacheInfo.put("name", cacheName); 
      cacheInfo.put("nameShort", getShortCacheName(cacheName)); 
      
      cacheInfo.put("elementsInCache", cache.getSize()); 
      cacheInfo.put("elementsInMemory", cache.getMemoryStoreSize()); 
      cacheInfo.put("elementsInDisk", cache.getDiskStoreSize()); 
      cacheInfo.put("hits", cacheStatistics.getCacheHits()); 
      cacheInfo.put("memoryHits", cacheStatistics.getInMemoryHits()); 
      cacheInfo.put("diskHits", cacheStatistics.getOnDiskHits()); 
      cacheInfo.put("misses", cacheStatistics.getCacheMisses()); 
      cacheInfo.put("evictionCount", cacheStatistics.getEvictionCount()); 
      
      cacheInfos.add(cacheInfo);
    }
    
    requestContext.getRequest().setAttribute("cacheInfos", cacheInfos); 

    requestContext.setIncludeJSP("/templates/system/hibernatestatistics.jsp");
  }

  public UserRole[] getAllowedRoles() {
    return new UserRole[] { UserRole.ADMINISTRATOR };
  }
  
  private String getShortCacheName(String cacheName) {
    int dotInd = cacheName.lastIndexOf('.');
    if (dotInd > 0) 
      return cacheName.substring(dotInd + 1);
    else
      return cacheName;
  } 
 
}
