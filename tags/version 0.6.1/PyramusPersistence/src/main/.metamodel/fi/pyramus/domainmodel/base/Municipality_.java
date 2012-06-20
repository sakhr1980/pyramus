package fi.pyramus.domainmodel.base;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@StaticMetamodel(Municipality.class)
public abstract class Municipality_ {

	public static volatile SingularAttribute<Municipality, Long> id;
	public static volatile SingularAttribute<Municipality, Boolean> archived;
	public static volatile SingularAttribute<Municipality, String> name;
	public static volatile SingularAttribute<Municipality, String> code;
	public static volatile SingularAttribute<Municipality, Long> version;

}

