package fi.pyramus.domainmodel.base;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@StaticMetamodel(ContactURLType.class)
public abstract class ContactURLType_ {

	public static volatile SingularAttribute<ContactURLType, Long> id;
	public static volatile SingularAttribute<ContactURLType, Boolean> archived;
	public static volatile SingularAttribute<ContactURLType, String> name;
	public static volatile SingularAttribute<ContactURLType, Long> version;

}

