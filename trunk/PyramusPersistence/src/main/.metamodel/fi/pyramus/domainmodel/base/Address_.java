package fi.pyramus.domainmodel.base;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@StaticMetamodel(Address.class)
public abstract class Address_ {

	public static volatile SingularAttribute<Address, String> streetAddress;
	public static volatile SingularAttribute<Address, Long> id;
	public static volatile SingularAttribute<Address, String> postalCode;
	public static volatile SingularAttribute<Address, ContactInfo> contactInfo;
	public static volatile SingularAttribute<Address, String> name;
	public static volatile SingularAttribute<Address, Boolean> defaultAddress;
	public static volatile SingularAttribute<Address, ContactType> contactType;
	public static volatile SingularAttribute<Address, Long> version;
	public static volatile SingularAttribute<Address, String> country;
	public static volatile SingularAttribute<Address, String> city;

}

