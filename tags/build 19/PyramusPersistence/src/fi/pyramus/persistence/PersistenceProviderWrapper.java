package fi.pyramus.persistence;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.persistence.EntityManagerFactory;
import javax.persistence.SharedCacheMode;
import javax.persistence.ValidationMode;
import javax.persistence.spi.ClassTransformer;
import javax.persistence.spi.PersistenceUnitInfo;
import javax.persistence.spi.PersistenceUnitTransactionType;
import javax.sql.DataSource;

import org.hibernate.ejb.HibernatePersistence;


public class PersistenceProviderWrapper extends org.hibernate.ejb.HibernatePersistence {
 
  public PersistenceProviderWrapper() {
    super();
  }
  
  @Override
  @SuppressWarnings("rawtypes")
  public EntityManagerFactory createContainerEntityManagerFactory(PersistenceUnitInfo info, Map properties) {
    PersistenceUnitInfo infoDelegate = new PersistenceUnitInfoDelegate(info, DomainModelExtensionVault.getInstance().getEntityNames());
    return super.createContainerEntityManagerFactory(infoDelegate, properties);
  }
  
  @Override
  @SuppressWarnings({ "deprecation", "rawtypes" })
  public EntityManagerFactory createEntityManagerFactory(Map properties) {
    return super.createEntityManagerFactory(properties);
  }
  
  @Override
  @SuppressWarnings("rawtypes")
  public EntityManagerFactory createEntityManagerFactory(String persistenceUnitName, Map properties) {
    return super.createEntityManagerFactory(persistenceUnitName, properties);
  }
  
  private class PersistenceUnitInfoDelegate implements PersistenceUnitInfo {

    public PersistenceUnitInfoDelegate(PersistenceUnitInfo original, Set<String> domainModelExtensions) {
      this.original = original;
      this.domainModelExtensions = domainModelExtensions;
    }
    
    @Override
    public void addTransformer(ClassTransformer arg0) {
      original.addTransformer(arg0);
    }

    @Override
    public boolean excludeUnlistedClasses() {
      return original.excludeUnlistedClasses();
    }

    @Override
    public ClassLoader getClassLoader() {
      return original.getClassLoader();
    }

    @Override
    public List<URL> getJarFileUrls() {
      return original.getJarFileUrls();
    }

    @Override
    public DataSource getJtaDataSource() {
      return original.getJtaDataSource();
    }

    @Override
    public List<String> getManagedClassNames() {
      List<String> managedClassNames = new ArrayList<String>(original.getManagedClassNames());
      managedClassNames.addAll(domainModelExtensions);
      return managedClassNames;
    }

    @Override
    public List<String> getMappingFileNames() {
      return original.getMappingFileNames();
    }

    @Override
    public ClassLoader getNewTempClassLoader() {
      return original.getNewTempClassLoader();
    }

    @Override
    public DataSource getNonJtaDataSource() {
      return original.getNonJtaDataSource();
    }

    @Override
    public String getPersistenceProviderClassName() {
      return HibernatePersistence.class.getName();
    }

    @Override
    public String getPersistenceUnitName() {
      return original.getPersistenceUnitName();
    }

    @Override
    public URL getPersistenceUnitRootUrl() {
      return original.getPersistenceUnitRootUrl();
    }

    @Override
    public String getPersistenceXMLSchemaVersion() {
      return original.getPersistenceXMLSchemaVersion();
    }

    @Override
    public Properties getProperties() {
      return original.getProperties();
    }

    @Override
    public SharedCacheMode getSharedCacheMode() {
      return original.getSharedCacheMode();
    }

    @Override
    public PersistenceUnitTransactionType getTransactionType() {
      return original.getTransactionType();
    }

    @Override
    public ValidationMode getValidationMode() {
      return original.getValidationMode();
    }
   
    private PersistenceUnitInfo original;
    private Set<String> domainModelExtensions;
  }
}
