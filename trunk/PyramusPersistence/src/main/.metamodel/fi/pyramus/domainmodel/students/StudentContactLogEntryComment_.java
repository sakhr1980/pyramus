package fi.pyramus.domainmodel.students;

import java.util.Date;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@StaticMetamodel(StudentContactLogEntryComment.class)
public abstract class StudentContactLogEntryComment_ {

	public static volatile SingularAttribute<StudentContactLogEntryComment, Long> id;
	public static volatile SingularAttribute<StudentContactLogEntryComment, String> text;
	public static volatile SingularAttribute<StudentContactLogEntryComment, String> creatorName;
	public static volatile SingularAttribute<StudentContactLogEntryComment, Boolean> archived;
	public static volatile SingularAttribute<StudentContactLogEntryComment, Date> commentDate;
	public static volatile SingularAttribute<StudentContactLogEntryComment, StudentContactLogEntry> entry;
	public static volatile SingularAttribute<StudentContactLogEntryComment, Long> version;

}

