package sqlite.domain;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public record TableRow(List<Object> values) {

  public Object get(int index) {
    return values.get(index);
  }

  public TableRow filterColumns(Collection<Integer> filterColumns) {
    var values = new ArrayList<Object>();
    for(var colIndex : filterColumns) {
      values.add(get(colIndex));
    }

    return new TableRow(values);
  }

  @Override
  public String toString() {
    var sb = new StringBuilder();
    for (var v : values) {
      sb.append(" " + v.toString());
    }

    return sb.toString();
  }
}
