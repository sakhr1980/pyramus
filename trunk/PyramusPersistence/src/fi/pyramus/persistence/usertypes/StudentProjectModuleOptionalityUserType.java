package fi.pyramus.persistence.usertypes;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.usertype.UserType;

public class StudentProjectModuleOptionalityUserType implements UserType {

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
    if (x == null || y == null)
      return false;
    return x.equals(y);
  }

  public int hashCode(Object x) throws HibernateException {
    return x.hashCode();
  }

  public boolean isMutable() {
    return false;
  }

  public Object nullSafeGet(ResultSet rs, String[] names, Object owner) throws HibernateException, SQLException {
    StudentProjectModuleOptionality optionality = StudentProjectModuleOptionality.getOptionality(rs.getInt(names[0]));
    if (rs.wasNull()) {
      return null;
    }
    return optionality;
  }

  public void nullSafeSet(PreparedStatement st, Object value, int index) throws HibernateException, SQLException {
    if (value == null) {
      st.setNull(index, Hibernate.INTEGER.sqlType());
    }
    else {
      st.setLong(index, ((StudentProjectModuleOptionality) value).getValue());
    }
  }

  public Object replace(Object original, Object target, Object owner) throws HibernateException {
    return original;
  }

  @SuppressWarnings("rawtypes")
  public Class returnedClass() {
    return StudentProjectModuleOptionality.class;
  }

  public int[] sqlTypes() {
    return new int[] { Hibernate.INTEGER.sqlType() };
  }

}
