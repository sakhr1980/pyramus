package fi.pyramus.updater.core;
import org.hibernate.dialect.Dialect;

public interface UpdateOperation {
  
  public String toSQL(Dialect dialect, String defaultCatalog, String defaultSchema);
  
}
