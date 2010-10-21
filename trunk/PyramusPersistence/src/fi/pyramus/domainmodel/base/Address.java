package fi.pyramus.domainmodel.base;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.TableGenerator;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;

import org.hibernate.search.annotations.DocumentId;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Index;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.Store;

@Entity
@Indexed
public class Address {

  public Long getId() {
    return id;
  }

  public String getPostalCode() {
    return postalCode;
  }

  public String getStreetAddress() {
    return streetAddress;
  }

  public void setPostalCode(String postalCode) {
    this.postalCode = postalCode;
  }

  public void setStreetAddress(String streetAddress) {
    this.streetAddress = streetAddress;
  }
  
  public String getCountry() {
    return country;
  }
  
  public void setCountry(String country) {
    this.country = country;
  }
  
  public String getCity() {
    return city;
  }
  
  public void setCity(String city) {
    this.city = city;
  }
  
  public void setName(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }

  public void setContactType(ContactType contactType) {
    this.contactType = contactType;
  }

  public ContactType getContactType() {
    return contactType;
  }

  public void setDefaultAddress(Boolean defaultAddress) {
    this.defaultAddress = defaultAddress;
  }

  public Boolean getDefaultAddress() {
    return defaultAddress;
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
  @GeneratedValue(strategy=GenerationType.TABLE, generator="Address")  
  @TableGenerator(name="Address", allocationSize=1)
  @DocumentId
  private Long id;
  
  @NotNull
  @Column(nullable = false)
  @Field (index=Index.TOKENIZED)
  private Boolean defaultAddress = Boolean.FALSE;

  @ManyToOne
  @JoinColumn (name = "contactType")
  private ContactType contactType;
  
  @Field (index = Index.TOKENIZED, store = Store.NO)
  private String name;
  
  @Field (index = Index.TOKENIZED, store = Store.NO)
  private String streetAddress;

  @Field (index = Index.TOKENIZED, store = Store.NO)
  private String postalCode;

  @Field (index = Index.TOKENIZED, store = Store.NO)
  private String city;
  
  @Field (index = Index.TOKENIZED, store = Store.NO)
  private String country;

  @ManyToOne
  @JoinColumn(name="contactInfo")
  private ContactInfo contactInfo;

  @Version
  @NotNull
  @Column(nullable = false)
  private Long version;
}
