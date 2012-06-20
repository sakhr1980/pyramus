package fi.pyramus.domainmodel.reports;

import fi.pyramus.domainmodel.users.User;
import java.util.Date;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@StaticMetamodel(Report.class)
public abstract class Report_ {

	public static volatile SingularAttribute<Report, Long> id;
	public static volatile SingularAttribute<Report, ReportCategory> category;
	public static volatile SingularAttribute<Report, Date> lastModified;
	public static volatile SingularAttribute<Report, Date> created;
	public static volatile SingularAttribute<Report, String> name;
	public static volatile SingularAttribute<Report, User> lastModifier;
	public static volatile SingularAttribute<Report, String> data;
	public static volatile SingularAttribute<Report, Long> version;
	public static volatile SingularAttribute<Report, User> creator;

}

