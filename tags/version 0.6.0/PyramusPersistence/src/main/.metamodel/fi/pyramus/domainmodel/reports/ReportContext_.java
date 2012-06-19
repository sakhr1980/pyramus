package fi.pyramus.domainmodel.reports;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@StaticMetamodel(ReportContext.class)
public abstract class ReportContext_ {

	public static volatile SingularAttribute<ReportContext, Long> id;
	public static volatile SingularAttribute<ReportContext, ReportContextType> context;
	public static volatile SingularAttribute<ReportContext, Report> report;

}

