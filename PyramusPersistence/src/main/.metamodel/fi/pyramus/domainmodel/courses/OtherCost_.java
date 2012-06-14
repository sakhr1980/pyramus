package fi.pyramus.domainmodel.courses;

import fi.pyramus.persistence.usertypes.MonetaryAmount;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@StaticMetamodel(OtherCost.class)
public abstract class OtherCost_ {

	public static volatile SingularAttribute<OtherCost, Course> course;
	public static volatile SingularAttribute<OtherCost, Long> id;
	public static volatile SingularAttribute<OtherCost, String> name;
	public static volatile SingularAttribute<OtherCost, MonetaryAmount> cost;
	public static volatile SingularAttribute<OtherCost, Long> version;

}

