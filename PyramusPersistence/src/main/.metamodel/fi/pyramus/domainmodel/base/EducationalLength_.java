package fi.pyramus.domainmodel.base;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@StaticMetamodel(EducationalLength.class)
public abstract class EducationalLength_ {

	public static volatile SingularAttribute<EducationalLength, Long> id;
	public static volatile SingularAttribute<EducationalLength, EducationalTimeUnit> unit;
	public static volatile SingularAttribute<EducationalLength, Double> units;
	public static volatile SingularAttribute<EducationalLength, Long> version;

}

