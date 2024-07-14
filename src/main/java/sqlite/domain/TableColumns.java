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
      if (columns.get(i).equalsIgnoreCase(columnName)) {
        return i;
      }
    }

    return -1;
  }

  public List<Integer> indexes(Collection<String> columnNames) {
    return columns.stream()
        .filter(columnNames::contains)
        .map(this::index)
        .toList();
  }
}
