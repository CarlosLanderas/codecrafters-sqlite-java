package sqlite.query;

import java.io.IOException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import sqlite.database.Database;
import sqlite.domain.TableRow;
import sqlite.reader.TableRowReader;

public class QueryCount implements Query {

  private final Pattern selectPattern = Pattern.compile("SELECT COUNT\\((.*)\\) FROM (.*)",
      Pattern.CASE_INSENSITIVE);
  private final Database database;

  public QueryCount(Database database) {
    this.database = database;
  }

  public List<TableRow> execute(String query) throws IOException {
    int counter = 0;
    var tableName = getTable(query);
    var table = database.getSchema().findTable(tableName);
    if (table.isEmpty()) {
      throw new RuntimeException("sqlite.domain.Table not found:" + query);
    }

    var reader = new TableRowReader(database, table.get());
    while (reader.hasNext()) {
      reader.next();
      counter++;
    }

    return List.of(new TableRow(List.of(counter), 0));
  }

  public boolean isCountQuery(String query) { //Temporal
    return selectPattern.matcher(query).matches();
  }

  private String getTable(String query) {
    Matcher m = selectPattern.matcher(query);
    if (!m.matches()) {
      throw new RuntimeException("Error parsing query: " + query);
    }

    return m.group(2);
  }
}
