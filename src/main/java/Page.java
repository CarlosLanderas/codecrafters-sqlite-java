import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class Page {

  final PageHeader header;
  private final int base;

  public Page(PageHeader header, int base) {
    this.header = header;
    this.base = base;
  }
  public enum PageType {
    InteriorIndex,
    InteriorTable,
    LeafIndex,
    LeafTable
  }

  public record PageHeader(int pageType, int numberCells, int cellContentStart) {}
}





