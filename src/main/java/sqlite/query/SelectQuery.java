package sqlite.query;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;
import sqlite.database.Database;
import sqlite.domain.Table;
import sqlite.domain.TableRow;
import sqlite.reader.IndexRowReader;
import sqlite.reader.TableRowReader;

public class SelectQuery implements Query {

  private final Database db;

  private final Pattern selectPattern = Pattern.compile(
      "\\bSELECT\\s+(.*?)\\s+FROM\\s+(\\w+)\\s*(?:WHERE\\s+(\\w+)\\s*=\\s*'([^']*)')?",
      Pattern.CASE_INSENSITIVE);

  public SelectQuery(Database db) {
    this.db = db;
  }

  public Collection<TableRow> execute(String query) throws IOException {

    var predicate = getPredicate(query);
    var table = db.getSchema().findTable(predicate.tableName());

    if (table.isEmpty()) {
      throw new RuntimeException("Table not found " + query);
    }

    Iterator<TableRow> reader = getRowIterator(table.get(), predicate);

    var rows = new ArrayList<TableRow>();
    while (reader.hasNext()) {
      rows.add(reader.next());
    }

    return new QueryPredicate(table.get()).filter(rows, predicate);
  }

  public SelectQueryPredicate predicate(String sqlQuery) {
    return getPredicate(sqlQuery);
  }

  public boolean isSelectQuery(String query) {
    return selectPattern.matcher(query).matches();
  }

  private Iterator<TableRow> getRowIterator(Table table, SelectQueryPredicate predicate)
      throws IOException {
    if (db.getSchema().findIndex(table.name()).isPresent()) {
      var index = db.getSchema().findIndex(table.name()).get();
      return new IndexRowReader(db, index, predicate);
    }

    return new TableRowReader(db, table);
  }

  private SelectQueryPredicate getPredicate(String query) {
    var m = selectPattern.matcher(query);

    if (!m.find()) {
      throw new RuntimeException("Error parsing query: " + query);

    }
    var columnsGroup = m.group(1);
    var tableGroup = m.group(2);
    var filterColumn = m.group(3);
    var filterValue = m.group(4);

    var sanitizedColumns = columnsGroup.split("\\s*,\\s*");

    List<String> columns = (sanitizedColumns.length == 1 && sanitizedColumns[0].equals("*")) ?
        Collections.emptyList() :
        Arrays.stream(sanitizedColumns).toList();

    return new SelectQueryPredicate(tableGroup, columns, filterColumn, filterValue);
  }
}

