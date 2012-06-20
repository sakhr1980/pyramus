package fi.pyramus.domainmodel.base;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@StaticMetamodel(PhoneNumber.class)
public abstract class PhoneNumber_ {

	public static volatile SingularAttribute<PhoneNumber, Long> id;
	public static volatile SingularAttribute<PhoneNumber, ContactInfo> contactInfo;
	public static volatile SingularAttribute<PhoneNumber, Boolean> defaultNumber;
	public static volatile SingularAttribute<PhoneNumber, ContactType> contactType;
	public static volatile SingularAttribute<PhoneNumber, String> number;
	public static volatile SingularAttribute<PhoneNumber, Long> version;

}

