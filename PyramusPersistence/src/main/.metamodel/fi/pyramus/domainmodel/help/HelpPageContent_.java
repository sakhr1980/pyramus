package fi.pyramus.domainmodel.help;

import fi.pyramus.domainmodel.users.User;
import java.util.Date;
import java.util.Locale;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@StaticMetamodel(HelpPageContent.class)
public abstract class HelpPageContent_ {

	public static volatile SingularAttribute<HelpPageContent, String> content;
	public static volatile SingularAttribute<HelpPageContent, Long> id;
	public static volatile SingularAttribute<HelpPageContent, Date> lastModified;
	public static volatile SingularAttribute<HelpPageContent, Date> created;
	public static volatile SingularAttribute<HelpPageContent, HelpPage> page;
	public static volatile SingularAttribute<HelpPageContent, Locale> locale;
	public static volatile SingularAttribute<HelpPageContent, User> lastModifier;
	public static volatile SingularAttribute<HelpPageContent, User> creator;

}

