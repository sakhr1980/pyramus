package fi.pyramus.updater.core;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.mapping.Column;
import org.hibernate.mapping.ForeignKey;
import org.hibernate.mapping.Table;

public class UpgradeBatch {
  
  public UpgradeBatch() {
  }
  
  public void addCreateTable(Table table) {
    operations.add(new CreateTableUpdateOperation(table));
  }
  
  public void addCreateForeignKey(ForeignKey foreignKey) {
    operations.add(new CreateForeignKeyUpdateOperation(foreignKey));
  }
  
  public void addAddColumn(Table table, Column column) {
    operations.add(new AddColumnUpdateOperation(table, column));
  }
  
  public void addChangeColumn(Table table, String oldName, Column column) {
    operations.add(new ChangeColumnUpdateOperation(table, oldName, column));
  }
  
  public void addDropColumn(Table table, String columnName) {
    operations.add(new DropColumnUpdateOperation(table, columnName));
  }
 
  public void addDropForeignKey(Table table, String foreignKeyName) {
    operations.add(new DropForeignKeyUpdateOperation(table, foreignKeyName));
  }
  
  public void addDropTable(String tableName) {
    operations.add(new DropTableUpdateOperation(tableName));
  }
  
  public List<UpdateOperation> getOperations() {
    return operations;
  }

  private List<UpdateOperation> operations = new ArrayList<UpdateOperation>();
}
