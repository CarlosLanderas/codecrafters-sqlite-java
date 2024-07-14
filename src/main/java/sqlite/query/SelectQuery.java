package sqlite.query;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;
import sqlite.database.Database;
import sqlite.domain.TableColumns;
import sqlite.domain.TableRow;
import sqlite.reader.TableRowReader;

public class SelectQuery implements Query {

  private final Database db;

  private final Pattern selectPattern = Pattern.compile("\\bSELECT\\s+(.*)\\s+FROM\\s(.*)",
      Pattern.CASE_INSENSITIVE);

  public SelectQuery(Database db) {
    this.db = db;
  }

  public List<TableRow> execute(String query) throws IOException {

    var predicate = getPredicate(query);
    var table = db.getSchema().findTable(predicate.tableName());

    if (table.isEmpty()) {
      throw new RuntimeException("Table not found " + query);
    }

    var reader = new TableRowReader(db, table.get());

    var rows = new ArrayList<TableRow>();
    while (reader.hasNext()) {
      rows.add(reader.next());
    }

    var colIndexes = new TableColumns(table.get()).indexes(predicate.columns());
    var results = new ArrayList<TableRow>();

    results.addAll(rows.stream().map(r -> r.filterColumns(colIndexes)).toList());

    return results;
  }

  public boolean isSelectQuery(String query) {
    return selectPattern.matcher(query).matches();
  }

  private SelectQueryPredicate getPredicate(String query) {
    var m = selectPattern.matcher(query);

    if (!m.find()) {
      throw new RuntimeException("Error parsing query: " + query);

    }

    var cGroup = m.group(1);
    var tableGroup = m.group(2);

    var columns = cGroup.split(",");

    return new SelectQueryPredicate(tableGroup, Arrays.stream(columns).toList());
  }
}

record SelectQueryPredicate(String tableName, Collection<String> columns) {}
