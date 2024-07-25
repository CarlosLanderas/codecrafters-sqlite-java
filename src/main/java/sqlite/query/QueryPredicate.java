package sqlite.query;

import java.util.Collection;
import sqlite.domain.Table;
import sqlite.domain.TableColumns;
import sqlite.domain.TableRow;

public class QueryPredicate {

  private final Table table;
  private final TableColumns tableColumns;

  public QueryPredicate(Table table) {
    this.table = table;
    this.tableColumns = new TableColumns(table);
  }

  public Collection<TableRow> filter(Collection<TableRow> rows, SelectQueryPredicate predicate) {
    var columns = tableColumns.indexes(predicate.columns());

    if (validWhere(predicate)) {
      rows = rows.stream().filter(r -> where(r, tableColumns, predicate)).toList();
    }

    if(!columns.isEmpty()) {
      rows = rows.stream().map(r -> r.filterColumns(columns)).toList();
    }

    return rows;
  }

  private boolean where(TableRow row, TableColumns columns, SelectQueryPredicate predicate) {
    var colIndex = columns.index(predicate.filterColumn());
    return row.get(colIndex).equals(predicate.filterValue());
  }

  private boolean validWhere(SelectQueryPredicate predicate) {
    return predicate.filterColumn() != null && predicate.filterValue() != null;
  }
}
