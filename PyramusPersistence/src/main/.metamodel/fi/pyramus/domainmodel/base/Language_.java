package fi.pyramus.domainmodel.base;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@StaticMetamodel(Language.class)
public abstract class Language_ {

	public static volatile SingularAttribute<Language, Long> id;
	public static volatile SingularAttribute<Language, Boolean> archived;
	public static volatile SingularAttribute<Language, String> name;
	public static volatile SingularAttribute<Language, String> code;
	public static volatile SingularAttribute<Language, Long> version;

}

