package fi.pyramus.persistence.search.filters;

import org.apache.lucene.index.Term;
import org.apache.lucene.search.Filter;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.QueryWrapperFilter;
import org.apache.lucene.search.TermQuery;
import org.hibernate.search.annotations.Factory;

public class ArchivedProjectFilterFactory {

  @Factory 
  public Filter getFilter() { 
    Term term = new Term("archived", "false"); 
    Query query = new TermQuery(term); 
    Filter filter = new QueryWrapperFilter(query); 
    return filter; 
  } 
}
