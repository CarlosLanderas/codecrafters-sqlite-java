package sqlite.reader;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import sqlite.database.Database;
import sqlite.domain.Cell;
import sqlite.domain.PageReader;
import sqlite.domain.Table;
import sqlite.domain.TableRow;

public class TableRowReader implements Iterator<TableRow> {

  private final Table table;
  private final List<Cell> cells;
  private int cellPosition;

  public TableRowReader(Database db, Table table) throws IOException {
    this.table = table;
    this.cells = PageReader.read(
            (int) table.rootPage(),
            db.getHeader(),
            db.getBuffer())
        .getCells()
        .stream()
        .toList();
  }

  @Override
  public boolean hasNext() {
    return cells.size() > cellPosition;
  }

  @Override
  public TableRow next() {
    var cell = cells.get(cellPosition);
    var row = new TableRow(sqlite.domain.Record.parse(cell).values());
    cellPosition++;

    return row;
  }
}