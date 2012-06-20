package fi.pyramus.plugin.simple.domainmodel.users;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@StaticMetamodel(SimpleAuth.class)
public abstract class SimpleAuth_ {

	public static volatile SingularAttribute<SimpleAuth, Long> id;
	public static volatile SingularAttribute<SimpleAuth, String> username;
	public static volatile SingularAttribute<SimpleAuth, String> password;

}

