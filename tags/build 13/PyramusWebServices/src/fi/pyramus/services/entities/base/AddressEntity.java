package fi.pyramus.services.entities.base;


public class AddressEntity {

  public AddressEntity(Long id, String country, String city, String postalCode, String streetAddress) {
    this.id = id;
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
  
  private Long id;
  private String country;
  private String city;
  private String postalCode;
  private String streetAddress;
}
