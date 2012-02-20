package fi.pyramus.persistence.usertypes;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.type.IntegerType;
import org.hibernate.usertype.UserType;

/**
 * UserType to handle StudentContactLogEntryTypes 
 */
public class StudentContactLogEntryUserType implements UserType {

  public Object assemble(Serializable cached, Object owner) throws HibernateException {
    return cached;
  }

  public Object deepCopy(Object value) throws HibernateException {
    return value;
  }

  public Serializable disassemble(Object value) throws HibernateException {
    return (Serializable) value;
  }

  public boolean equals(Object x, Object y) throws HibernateException {
    return x == null && y == null ? true : x == null || y == null ? false : x.equals(y);
  }

  public int hashCode(Object x) throws HibernateException {
    return x.hashCode();
  }

  public boolean isMutable() {
    return false;
  }

  public Object nullSafeGet(ResultSet rs, String[] names, SessionImplementor session, Object owner) throws HibernateException, SQLException {
    StudentContactLogEntryType type = StudentContactLogEntryType.getType(rs.getInt(names[0])); 
    if (rs.wasNull())
      return null;
    return type;
  }

  public void nullSafeSet(PreparedStatement st, Object value, int index, SessionImplementor session) throws HibernateException, SQLException {
    if (value == null) {
      st.setNull(index, IntegerType.INSTANCE.sqlType());
    } else {
      st.setLong(index, ((StudentContactLogEntryType) value).getValue());
    }
  }

  public Object replace(Object original, Object target, Object owner) throws HibernateException {
    return original;
  }

  @SuppressWarnings("rawtypes")
  public Class returnedClass() {
    return StudentContactLogEntryType.class;
  }

  public int[] sqlTypes() {
    return new int[]{ IntegerType.INSTANCE.sqlType()};
  }

}
