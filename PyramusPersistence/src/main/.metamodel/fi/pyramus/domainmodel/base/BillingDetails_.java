package fi.pyramus.domainmodel.base;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@StaticMetamodel(BillingDetails.class)
public abstract class BillingDetails_ {

	public static volatile SingularAttribute<BillingDetails, String> region;
	public static volatile SingularAttribute<BillingDetails, String> streetAddress1;
	public static volatile SingularAttribute<BillingDetails, String> referenceNumber;
	public static volatile SingularAttribute<BillingDetails, String> streetAddress2;
	public static volatile SingularAttribute<BillingDetails, String> companyIdentifier;
	public static volatile SingularAttribute<BillingDetails, String> emailAddress;
	public static volatile SingularAttribute<BillingDetails, String> companyName;
	public static volatile SingularAttribute<BillingDetails, String> personName;
	public static volatile SingularAttribute<BillingDetails, String> city;
	public static volatile SingularAttribute<BillingDetails, String> country;
	public static volatile SingularAttribute<BillingDetails, Long> id;
	public static volatile SingularAttribute<BillingDetails, String> postalCode;
	public static volatile SingularAttribute<BillingDetails, String> phoneNumber;
	public static volatile SingularAttribute<BillingDetails, String> bic;
	public static volatile SingularAttribute<BillingDetails, String> iban;

}

