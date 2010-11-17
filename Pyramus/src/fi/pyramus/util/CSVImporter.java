package fi.pyramus.util;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import au.com.bytecode.opencsv.CSVReader;
import fi.pyramus.PyramusRuntimeException;
import fi.pyramus.dao.DAOFactory;
import fi.pyramus.dao.SystemDAO;
import fi.pyramus.util.dataimport.DataImportContext;
import fi.pyramus.util.dataimport.DataImportStrategyProvider;
import fi.pyramus.util.dataimport.EntityHandlingStrategy;

public class CSVImporter {

  private String[] headerFields;

  @SuppressWarnings("rawtypes")
  public List<Object> importDataFromStream(InputStream stream, Class entityClass, Long loggedUserId) {
    List<Object> list = new ArrayList<Object>();
    CSVReader reader = new CSVReader(new InputStreamReader(stream));
    
    try {
      String [] firstLine = reader.readNext();
      this.headerFields = firstLine;
      String [] nextLine;
      SystemDAO systemDAO = DAOFactory.getInstance().getSystemDAO();
      EntityHandlingStrategy entityHandler = DataImportStrategyProvider.instance().getEntityHandler(entityClass);

      if (firstLine != null) {
        while ((nextLine = reader.readNext()) != null) {
            // nextLine[] is an array of values from the line
            System.out.println(nextLine[0] + nextLine[1] + "etc...");
            
          DataImportContext context = new DataImportContext(systemDAO, loggedUserId);
          context.setFields(firstLine);
          context.setFieldValues(nextLine);
          entityHandler.initializeContext(context);
          
  
          Object value = null;
          String fieldName = null;
          
          for (int i = 0; i < firstLine.length; i++) {
            fieldName = firstLine[i];
            value = nextLine[i];

            entityHandler.handleValue(fieldName, value, context);
          }
          
          entityHandler.saveEntities(context, systemDAO);
          
          Object[] entities2 = context.getEntities(); 
          for (int i = 0; i < entities2.length; i++) {
            if (entities2[i].getClass().equals(entityClass)) {
              list.add(entities2[i]);
            }
          }
        }
      }
    } catch (Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
      throw new PyramusRuntimeException(e);
    }

    return list;
  }

  public String[] getHeaderFields() {
    return headerFields;
  }  
}