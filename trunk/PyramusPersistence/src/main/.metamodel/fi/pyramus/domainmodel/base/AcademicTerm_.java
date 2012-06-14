package fi.pyramus.domainmodel.base;

import java.util.Date;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@StaticMetamodel(AcademicTerm.class)
public abstract class AcademicTerm_ {

	public static volatile SingularAttribute<AcademicTerm, Long> id;
	public static volatile SingularAttribute<AcademicTerm, Date> startDate;
	public static volatile SingularAttribute<AcademicTerm, Boolean> archived;
	public static volatile SingularAttribute<AcademicTerm, String> name;
	public static volatile SingularAttribute<AcademicTerm, Date> endDate;
	public static volatile SingularAttribute<AcademicTerm, Long> version;

}

