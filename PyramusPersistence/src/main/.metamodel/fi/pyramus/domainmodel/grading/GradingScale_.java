package fi.pyramus.domainmodel.grading;

import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@StaticMetamodel(GradingScale.class)
public abstract class GradingScale_ {

	public static volatile SingularAttribute<GradingScale, Long> id;
	public static volatile SingularAttribute<GradingScale, Boolean> archived;
	public static volatile SingularAttribute<GradingScale, String> description;
	public static volatile SingularAttribute<GradingScale, String> name;
	public static volatile ListAttribute<GradingScale, Grade> grades;
	public static volatile SingularAttribute<GradingScale, Long> version;

}

