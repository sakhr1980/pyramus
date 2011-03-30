package fi.pyramus.persistence.search.filters;

import java.util.List;

import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Filter;
import org.apache.lucene.search.QueryWrapperFilter;
import org.apache.lucene.search.TermQuery;
import org.hibernate.search.annotations.Factory;
import org.hibernate.search.annotations.Key;
import org.hibernate.search.filter.FilterKey;
import org.hibernate.search.filter.StandardFilterKey;

public class StudentIdFilterFactory {

  public void setStudentIds(List<Long> studentIds) {
    this.studentIds = studentIds;
  }

  @Factory
  public Filter getFilter() {
    BooleanQuery booleanQuery = new BooleanQuery();
    booleanQuery.setMinimumNumberShouldMatch(1);
    
    for (int i = 0; i < studentIds.size(); i++) {
      TermQuery termq = new TermQuery(new Term("students.id", studentIds.get(i).toString()));
      booleanQuery.add(termq, BooleanClause.Occur.SHOULD);
    }
    
    QueryWrapperFilter queryWrapperFilter = new QueryWrapperFilter(booleanQuery);

    return queryWrapperFilter;
  }

  @Key
  public FilterKey getKey() {
    StandardFilterKey key = new StandardFilterKey();
    key.addParameter(this.studentIds);
    return key;
  }

  private List<Long> studentIds;
}
