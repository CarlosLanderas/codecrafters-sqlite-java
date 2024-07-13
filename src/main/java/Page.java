import java.util.Collection;

record Page(
    int base,
    PageHeader header,
    Collection<Cell> cells
) {

  public Collection<Cell> getCells() {
    return this.cells;
  }
}





