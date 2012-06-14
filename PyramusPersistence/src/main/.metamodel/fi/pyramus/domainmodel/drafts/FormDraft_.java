package fi.pyramus.domainmodel.drafts;

import fi.pyramus.domainmodel.users.User;
import java.util.Date;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@StaticMetamodel(FormDraft.class)
public abstract class FormDraft_ {

	public static volatile SingularAttribute<FormDraft, Long> id;
	public static volatile SingularAttribute<FormDraft, Date> created;
	public static volatile SingularAttribute<FormDraft, String> data;
	public static volatile SingularAttribute<FormDraft, String> url;
	public static volatile SingularAttribute<FormDraft, Long> version;
	public static volatile SingularAttribute<FormDraft, Date> modified;
	public static volatile SingularAttribute<FormDraft, User> creator;

}

