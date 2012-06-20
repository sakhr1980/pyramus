package fi.pyramus.domainmodel.changelog;

import fi.pyramus.domainmodel.users.User;
import java.util.Date;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@StaticMetamodel(ChangeLogEntry.class)
public abstract class ChangeLogEntry_ {

	public static volatile SingularAttribute<ChangeLogEntry, Long> id;
	public static volatile SingularAttribute<ChangeLogEntry, Date> time;
	public static volatile SingularAttribute<ChangeLogEntry, ChangeLogEntryEntity> entity;
	public static volatile SingularAttribute<ChangeLogEntry, String> entityId;
	public static volatile SingularAttribute<ChangeLogEntry, ChangeLogEntryType> type;
	public static volatile SingularAttribute<ChangeLogEntry, User> user;

}

