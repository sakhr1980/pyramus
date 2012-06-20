package fi.pyramus.domainmodel.base;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@StaticMetamodel(SchoolField.class)
public abstract class SchoolField_ {

	public static volatile SingularAttribute<SchoolField, Long> id;
	public static volatile SingularAttribute<SchoolField, Boolean> archived;
	public static volatile SingularAttribute<SchoolField, String> name;

}

