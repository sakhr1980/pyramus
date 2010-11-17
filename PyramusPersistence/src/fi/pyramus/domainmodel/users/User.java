package fi.pyramus.domainmodel.users;

import java.util.List;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.PersistenceException;
import javax.persistence.TableGenerator;
import javax.persistence.Transient;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;
import org.hibernate.search.annotations.DocumentId;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Index;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.IndexedEmbedded;
import org.hibernate.search.annotations.Store;
import org.hibernate.validator.constraints.NotEmpty;

import fi.pyramus.domainmodel.base.ContactInfo;
import fi.pyramus.domainmodel.base.Tag;
import fi.pyramus.persistence.usertypes.RoleUserType;

@Entity
@TypeDefs ({
  @TypeDef (name="Role", typeClass=RoleUserType.class)
})
@Indexed
@Cache (usage = CacheConcurrencyStrategy.READ_WRITE)
public class User {

  public Long getId() {
    return id;
  }
  public String getFirstName() {
    return firstName;
  }

  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  public String getLastName() {
    return lastName;
  }

  public void setLastName(String lastName) {
    this.lastName = lastName;
  }
  
  public String getAuthProvider() {
    return authProvider;
  }
  
  public void setAuthProvider(String authProvider) {
    this.authProvider = authProvider;
  }
  
  public String getExternalId() {
    return externalId;
  }
  
  public void setExternalId(String externalId) {
    this.externalId = externalId;
  }
  
  @Transient 
  public String getFullName() {
    return getFirstName() + ' ' + getLastName();
  }

  public void setRole(Role role) {
    this.role = role;
  }

  public Role getRole() {
    return role;
  }
  
  public void setVariables(List<UserVariable> variables) {
    this.variables = variables;
  }

  public List<UserVariable> getVariables() {
    return variables;
  }

  public Set<Tag> getTags() {
    return tags;
  }
  
  public void setTags(Set<Tag> tags) {
    this.tags = tags;
  }
  
  public void addTag(Tag tag) {
    if (tags.contains(tag)) {
      tags.add(tag);
    } else {
      throw new PersistenceException("Entity already has this tag");
    }
  }
  
  public void removeTag(Tag tag) {
    if (tags.contains(tag)) {
      tags.add(tag);
    } else {
      throw new PersistenceException("Entity does not have this tag");
    }
  }
  
  @Transient
  public Map<String, String> getVariablesAsStringMap() {
    Map<String, String> result = new HashMap<String, String>();
    for (UserVariable userVariable : variables) {
      result.put(userVariable.getKey().getVariableKey(), userVariable.getValue());
    }
    return result;
  } 

  public void setContactInfo(ContactInfo contactInfo) {
    this.contactInfo = contactInfo;
  }
  public ContactInfo getContactInfo() {
    return contactInfo;
  }

  @SuppressWarnings("unused")
  private void setVersion(Long version) {
    this.version = version;
  }

  public Long getVersion() {
    return version;
  }

  @Id
  @GeneratedValue(strategy=GenerationType.TABLE, generator="User")  
  @TableGenerator(name="User", allocationSize=1)
  @DocumentId
  private Long id;
  
  @OneToOne (fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
  @JoinColumn (name="contactInfo")
  @IndexedEmbedded
  private ContactInfo contactInfo = new ContactInfo();

  @NotNull
  @Column (nullable = false)
  @NotEmpty
  @Field(index=Index.TOKENIZED)
  private String firstName;
  
  @NotNull
  @Column (nullable = false)
  @NotEmpty
  @Field(index=Index.TOKENIZED)
  private String lastName;

  @NotNull
  @Column (nullable = false)
  @NotEmpty
  private String externalId;
  
  @NotNull
  @Column (nullable = false)
  @NotEmpty
  private String authProvider;  

  @NotNull
  @Column (nullable = false)
  @Type (type="Role")  
  @Field (index = Index.TOKENIZED, store = Store.NO)
  // TODO Some way to disallow Role.EVERYONE
  private Role role;
  
  @OneToMany (cascade = CascadeType.ALL, orphanRemoval = true)
  @JoinColumn (name="user")
  private List<UserVariable> variables = new ArrayList<UserVariable>();

  @ManyToMany (fetch = FetchType.LAZY)
  @JoinTable (name="__UserTags", joinColumns=@JoinColumn(name="user"), inverseJoinColumns=@JoinColumn(name="tag"))
  @IndexedEmbedded 
  private Set<Tag> tags = new HashSet<Tag>();
  
  @Version
  @Column(nullable = false)
  private Long version;
}
