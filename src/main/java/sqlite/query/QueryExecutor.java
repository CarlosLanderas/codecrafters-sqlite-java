package sqlite.query;
import sqlite.database.Database;

public class QueryExecutor {
    private final QueryCount queryCount;
    private final SelectQuery selectQuery;

    public QueryExecutor(Database db) {
      queryCount = new QueryCount(db);
      selectQuery =  new SelectQuery(db);
    }
    public Query get(String query) {
      if (queryCount.isCountQuery(query)) {
        return queryCount;
      }

      if (selectQuery.isSelectQuery(query)) {
        return selectQuery;
      }

      throw new RuntimeException("Unsupported query: " + query);
    }
}
