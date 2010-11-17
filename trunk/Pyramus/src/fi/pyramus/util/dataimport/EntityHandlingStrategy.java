package fi.pyramus.util.dataimport;

import fi.pyramus.dao.SystemDAO;

public interface EntityHandlingStrategy {

  void handleValue(String fieldName, Object value, DataImportContext context) 
      throws SecurityException, IllegalArgumentException, NoSuchFieldException, IllegalAccessException;
  
  void saveEntities(DataImportContext context, SystemDAO systemDAO);
  
  void initializeContext(DataImportContext context);
}
