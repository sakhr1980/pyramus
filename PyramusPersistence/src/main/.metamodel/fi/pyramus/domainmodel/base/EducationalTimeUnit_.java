package fi.pyramus.domainmodel.base;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@StaticMetamodel(EducationalTimeUnit.class)
public abstract class EducationalTimeUnit_ {

	public static volatile SingularAttribute<EducationalTimeUnit, Long> id;
	public static volatile SingularAttribute<EducationalTimeUnit, Boolean> archived;
	public static volatile SingularAttribute<EducationalTimeUnit, String> name;
	public static volatile SingularAttribute<EducationalTimeUnit, Double> baseUnits;
	public static volatile SingularAttribute<EducationalTimeUnit, Long> version;

}

