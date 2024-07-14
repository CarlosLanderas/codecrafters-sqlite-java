package sqlite.domain;

import java.util.Collection;

public record Page(
    int base,
    PageHeader header,
    Collection<Cell> cells
) {

  public Collection<Cell> getCells() {
    return this.cells;
  }
}





