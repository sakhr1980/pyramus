package fi.pyramus.domainmodel.reports;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@StaticMetamodel(ReportCategory.class)
public abstract class ReportCategory_ {

	public static volatile SingularAttribute<ReportCategory, Long> id;
	public static volatile SingularAttribute<ReportCategory, String> name;
	public static volatile SingularAttribute<ReportCategory, Integer> indexColumn;
	public static volatile SingularAttribute<ReportCategory, Long> version;

}

