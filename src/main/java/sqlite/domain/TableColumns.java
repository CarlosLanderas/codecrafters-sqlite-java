package sqlite.domain;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import sqlite.parser.SqlParser;

public class TableColumns {

  private final List<String> columns;

  public TableColumns(Table table) {
    columns = SqlParser.parseColumnNames(table.sql());
  }

  public Collection<String> getColumns() {
    return columns;
  }

  public int index(String columnName) {
    for (var i = 0; i < columns.size(); i++) {
      var col = columns.get(i);
      if (col != null && col.equalsIgnoreCase(columnName)) {
        return i;
      }
    }

    return -1;
  }

  public List<Integer> indexes(Collection<String> columnNames) {
    var colIndex = new ArrayList<Integer>();
    for (var colName : columnNames) {
      if (columnNames.contains(colName)) {
        colIndex.add(index(colName));
      }
    }

    return colIndex;
  }
}
