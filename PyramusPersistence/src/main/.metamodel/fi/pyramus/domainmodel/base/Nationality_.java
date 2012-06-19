package fi.pyramus.domainmodel.base;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@StaticMetamodel(Nationality.class)
public abstract class Nationality_ {

	public static volatile SingularAttribute<Nationality, Long> id;
	public static volatile SingularAttribute<Nationality, Boolean> archived;
	public static volatile SingularAttribute<Nationality, String> name;
	public static volatile SingularAttribute<Nationality, String> code;
	public static volatile SingularAttribute<Nationality, Long> version;

}

