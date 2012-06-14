package fi.pyramus.domainmodel.base;

import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@StaticMetamodel(EducationType.class)
public abstract class EducationType_ {

	public static volatile ListAttribute<EducationType, EducationSubtype> subtypes;
	public static volatile SingularAttribute<EducationType, Long> id;
	public static volatile SingularAttribute<EducationType, Boolean> archived;
	public static volatile SingularAttribute<EducationType, String> name;
	public static volatile SingularAttribute<EducationType, String> code;
	public static volatile SingularAttribute<EducationType, Long> version;

}

