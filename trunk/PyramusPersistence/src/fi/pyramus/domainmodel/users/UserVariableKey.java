package fi.pyramus.domainmodel.users;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.TableGenerator;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Index;
import org.hibernate.search.annotations.Store;
import org.hibernate.validator.constraints.NotEmpty;

import fi.pyramus.persistence.usertypes.VariableType;
import fi.pyramus.persistence.usertypes.VariableTypeUserType;

@Entity
@TypeDefs ({
  @TypeDef (name="VariableType", typeClass=VariableTypeUserType.class)
})
public class UserVariableKey {

  public Long getId() {
    return id;
  }
  
  public void setVariableKey(String variableKey) {
    this.variableKey = variableKey;
  }
  
  public String getVariableKey() {
    return variableKey;
  }

  public void setVariableName(String variableName) {
    this.variableName = variableName;
  }

  public String getVariableName() {
    return variableName;
  }

  public void setVariableType(VariableType variableType) {
    this.variableType = variableType;
  }

  public VariableType getVariableType() {
    return variableType;
  }
  
  public Boolean getUserEditable() {
    return userEditable;
  }
  
  public void setUserEditable(Boolean userEditable) {
    this.userEditable = userEditable;
  }
  
  @SuppressWarnings("unused")
  private void setVersion(Long version) {
    this.version = version;
  }

  public Long getVersion() {
    return version;
  }

  @Id 
  @GeneratedValue(strategy=GenerationType.TABLE, generator="UserVariableKey")  
  @TableGenerator(name="UserVariableKey", allocationSize=1)
  private Long id;
  
  @NotNull
  @Column (nullable = false)
  private Boolean userEditable = Boolean.FALSE;

  @NotNull
  @Column (nullable = false)
  @NotEmpty
  private String variableKey;
  
  @NotNull
  @Column (nullable = false)
  @NotEmpty
  private String variableName;

  @Column 
  @Type (type="VariableType")  
  @Field (index = Index.UN_TOKENIZED, store = Store.NO)
  private VariableType variableType;

  @Version
  @NotNull
  @Column(nullable = false)
  private Long version;
}
