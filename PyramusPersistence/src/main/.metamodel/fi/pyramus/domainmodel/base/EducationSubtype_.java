package fi.pyramus.domainmodel.base;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@StaticMetamodel(EducationSubtype.class)
public abstract class EducationSubtype_ {

	public static volatile SingularAttribute<EducationSubtype, Long> id;
	public static volatile SingularAttribute<EducationSubtype, EducationType> educationType;
	public static volatile SingularAttribute<EducationSubtype, Boolean> archived;
	public static volatile SingularAttribute<EducationSubtype, String> name;
	public static volatile SingularAttribute<EducationSubtype, String> code;
	public static volatile SingularAttribute<EducationSubtype, Long> version;

}

