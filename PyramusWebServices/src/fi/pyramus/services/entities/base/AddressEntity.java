package fi.pyramus.services.entities.base;


public class AddressEntity {

  public AddressEntity(Long id, Boolean defaultAddress, Long contactTypeId, String country, String city, String postalCode, String streetAddress) {
    this.id = id;
    this.defaultAddress = defaultAddress;
    this.contactTypeId = contactTypeId;
    this.country = country;
    this.city = city;
    this.postalCode = postalCode;
    this.streetAddress = streetAddress;
  }
  
  public Long getId() {
    return id;
  }
  
  public String getCountry() {
    return country;
  }
  
  public String getCity() {
    return city;
  }
  
  public String getPostalCode() {
    return postalCode;
  }
  
  public String getStreetAddress() {
    return streetAddress;
  }
  
  public Boolean getDefaultAddress() {
    return defaultAddress;
  }

  public Long getContactTypeId() {
    return contactTypeId;
  }

  private Long id;
  private Boolean defaultAddress;
  private Long contactTypeId;
  private String country;
  private String city;
  private String postalCode;
  private String streetAddress;
}
