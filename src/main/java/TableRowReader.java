import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

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
    var row = new TableRow(Record.parse(cell).values());
    cellPosition++;

    return row;
  }
}
