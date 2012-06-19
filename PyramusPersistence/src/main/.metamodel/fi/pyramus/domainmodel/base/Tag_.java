package fi.pyramus.domainmodel.base;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@StaticMetamodel(Tag.class)
public abstract class Tag_ {

	public static volatile SingularAttribute<Tag, Long> id;
	public static volatile SingularAttribute<Tag, String> text;
	public static volatile SingularAttribute<Tag, Long> version;

}

