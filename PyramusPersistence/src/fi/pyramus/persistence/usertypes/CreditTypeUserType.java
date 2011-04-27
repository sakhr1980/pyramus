package fi.pyramus.persistence.usertypes;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.usertype.UserType;

public class CreditTypeUserType implements UserType {

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

  public Object nullSafeGet(ResultSet rs, String[] names, Object owner) throws HibernateException, SQLException {
    CreditType creditType = null;
    
    switch (rs.getInt(names[0])) {
      case 1:
        creditType = CreditType.CourseAssessment;
      break;
      case 2:
        creditType = CreditType.TransferCredit;
      break;
    }
    
    if (rs.wasNull())
      return null;

    return creditType;
  }

  public void nullSafeSet(PreparedStatement st, Object value, int index) throws HibernateException, SQLException {
    if (value == null) {
      st.setNull(index, Hibernate.INTEGER.sqlType());
    } else {
      switch ((CreditType) value) {
        case CourseAssessment:
          st.setInt(index, 1);
        break;
        case TransferCredit:
          st.setInt(index, 2);
        break;
      }
    }
  }

  public Object replace(Object original, Object target, Object owner) throws HibernateException {
    return original;
  }

  @SuppressWarnings("rawtypes")
  public Class returnedClass() {
    return CreditType.class;
  }

  public int[] sqlTypes() {
    return new int[]{Hibernate.INTEGER.sqlType()};
  }

}
