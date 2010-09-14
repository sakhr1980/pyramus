package fi.pyramus.updater.core;
import org.hibernate.dialect.Dialect;
import org.hibernate.mapping.Table;

public class CreateTableUpdateOperation implements UpdateOperation {

  public CreateTableUpdateOperation(Table table) {
    this.table = table;
  }
  
  @Override
  public String toSQL(Dialect dialect, String defaultCatalog, String defaultSchema) {
    return table.sqlCreateString(dialect, null, defaultCatalog, defaultSchema);
  }
   
  private Table table;
}
