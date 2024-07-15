package sqlite.reader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import sqlite.database.Database;
import sqlite.domain.Cell;
import sqlite.domain.PageReader;
import sqlite.domain.PageType;
import sqlite.domain.Table;
import sqlite.domain.TableRow;
import sqlite.domain.Record;

public class TableRowReader implements Iterator<TableRow> {

  private final Table table;
  private List<Cell> leafTables = new ArrayList<>();
  private List<Long> pageNumbers = new ArrayList<>();
  private final Database db;

  public TableRowReader(Database db, Table table) throws IOException {
    this.table = table;
    this.db = db;
    initialize();
  }

  private void initialize() throws IOException {

    pageNumbers.add(table.rootPage());

    while (!pageNumbers.isEmpty()) {
      var page = PageReader.read(Math.toIntExact(pageNumbers.removeLast()), db.getHeader(),
          db.getBuffer());

      switch (page.header().pageType()) {
        case PageType.LeafTable -> leafTables.addAll(page.cells());
        case PageType.InteriorTable -> pageNumbers.addAll(
            page.cells().stream().map(Cell::leftChild).toList()
        );
        default -> throw new RuntimeException("Not implemented");
      }

      if(page.header().rightMostPointer() != 0) {
        pageNumbers.add((long)page.header().rightMostPointer());
      }
    }
  }

  @Override
  public boolean hasNext() {
    return !leafTables.isEmpty();
  }

  @Override
  public TableRow next() {
    var cell = leafTables.removeLast();
    try {
      return readCell(cell);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private TableRow readCell(Cell cell) throws IOException {
    var record = Record.parse(cell);
    return new TableRow(record.values(), record.rowId());
  }
}
