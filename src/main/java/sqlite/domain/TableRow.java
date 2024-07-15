package sqlite.domain;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public record TableRow(List<Object> values, long rowId) {

  public Object get(int index) {
    return values.get(index);
  }

  public TableRow filterColumns(Collection<Integer> filterColumns) {
    var newValues = new ArrayList<>();
    for(var colIndex : filterColumns) {
      newValues.add(get(colIndex));
    }

    return new TableRow(newValues, this.rowId);
  }

  @Override
  public String toString() {
    var sb = new StringBuilder();
    for (var v : values) {
      sb.append("|" + v.toString());
    }

    return sb.toString();
  }
}
