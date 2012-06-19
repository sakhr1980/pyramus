package fi.pyramus.domainmodel.students;

import java.util.Date;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@StaticMetamodel(StudentContactLogEntry.class)
public abstract class StudentContactLogEntry_ {

	public static volatile SingularAttribute<StudentContactLogEntry, Long> id;
	public static volatile SingularAttribute<StudentContactLogEntry, String> text;
	public static volatile SingularAttribute<StudentContactLogEntry, String> creatorName;
	public static volatile SingularAttribute<StudentContactLogEntry, Boolean> archived;
	public static volatile SingularAttribute<StudentContactLogEntry, Student> student;
	public static volatile SingularAttribute<StudentContactLogEntry, Date> entryDate;
	public static volatile SingularAttribute<StudentContactLogEntry, StudentContactLogEntryType> type;
	public static volatile SingularAttribute<StudentContactLogEntry, Long> version;

}

