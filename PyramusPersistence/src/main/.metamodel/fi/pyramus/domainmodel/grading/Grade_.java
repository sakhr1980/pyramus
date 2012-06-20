package fi.pyramus.domainmodel.grading;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@StaticMetamodel(Grade.class)
public abstract class Grade_ {

	public static volatile SingularAttribute<Grade, Long> id;
	public static volatile SingularAttribute<Grade, String> qualification;
	public static volatile SingularAttribute<Grade, Boolean> archived;
	public static volatile SingularAttribute<Grade, String> description;
	public static volatile SingularAttribute<Grade, Double> GPA;
	public static volatile SingularAttribute<Grade, String> name;
	public static volatile SingularAttribute<Grade, Boolean> passingGrade;
	public static volatile SingularAttribute<Grade, GradingScale> gradingScale;
	public static volatile SingularAttribute<Grade, Long> version;

}

