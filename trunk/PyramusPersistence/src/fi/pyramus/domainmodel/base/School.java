package fi.pyramus.domainmodel.base;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.TableGenerator;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.search.annotations.DocumentId;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.FullTextFilterDef;
import org.hibernate.search.annotations.FullTextFilterDefs;
import org.hibernate.search.annotations.Index;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.IndexedEmbedded;
import org.hibernate.validator.constraints.NotEmpty;

import fi.pyramus.persistence.search.filters.ArchivedEntityFilterFactory;

@Entity
@Indexed
@Cache (usage = CacheConcurrencyStrategy.READ_WRITE)
@FullTextFilterDefs (
  @FullTextFilterDef (
     name="ArchivedSchool",
     impl=ArchivedEntityFilterFactory.class
  )
)
public class School implements ArchivableEntity {
  
  /**
   * Returns internal unique id.
   * 
   * @return Internal unique id
   */
  public Long getId() {
    return id;
  }

  /**
   * Sets user friendly name for this school.
   * 
   * @param name User friendly name for this school
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   * Returns user friendly name of this school.
   * 
   * @return User friendly name of this school
   */
  public String getName() {
    return name;
  }

  public List<SchoolVariable> getVariables() {
    return variables;
  }
  
  public void setVariables(List<SchoolVariable> variables) {
    this.variables = variables;
  }

  @Transient
  public Map<String, String> getVariablesAsStringMap() {
    Map<String, String> result = new HashMap<String, String>();
    for (SchoolVariable schoolVariable : variables) {
      result.put(schoolVariable.getKey().getVariableKey(), schoolVariable.getValue());
    }
    return result;
  } 

  public void setCode(String code) {
    this.code = code;
  }

  public String getCode() {
    return code;
  }

  public void setArchived(Boolean archived) {
    this.archived = archived;
  }

  public Boolean getArchived() {
    return archived;
  }

  public void setContactInfo(ContactInfo contactInfo) {
    this.contactInfo = contactInfo;
  }

  public ContactInfo getContactInfo() {
    return contactInfo;
  }

  @Id 
  @GeneratedValue(strategy=GenerationType.TABLE, generator="School")  
  @TableGenerator(name="School", allocationSize=1)
  @DocumentId
  private Long id;
  
  @NotNull
  @NotEmpty
  @Column (nullable = false)
  @Field (index=Index.TOKENIZED)
  private String code;
  
  @NotNull
  @NotEmpty
  @Column (nullable = false)
  @Field (index=Index.TOKENIZED)
  private String name;
  
  @OneToOne (fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
  @JoinColumn (name="contactInfo")
  @IndexedEmbedded
  private ContactInfo contactInfo = new ContactInfo();

  @OneToMany (cascade = CascadeType.ALL, orphanRemoval = true)
  @JoinColumn (name="school")
  private List<SchoolVariable> variables = new ArrayList<SchoolVariable>();

  @NotNull
  @Column (nullable = false)
  @Field (index = Index.TOKENIZED)
  private Boolean archived = Boolean.FALSE;

}