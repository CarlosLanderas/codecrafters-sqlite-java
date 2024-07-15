package sqlite.domain;

import java.util.Collection;
import java.util.List;

public record Page(
    int base,
    PageHeader header,
    List<Cell> cells
) {

  public List<Cell> getCells() {
    return this.cells;
  }
}





