package sqlite.database;

import java.util.List;
import java.util.Optional;
import sqlite.domain.Table;

public record Schema(List<Table> tables, List<Table> indexes) {
  public Optional<Table> findTable(String tableName) {
    return tables
        .stream()
        .filter(t -> t.name().equalsIgnoreCase(tableName))
        .findFirst();
  }

  public Optional<Table> findIndex(String indexName) {
    return indexes
        .stream()
        .filter(i -> i.name().equalsIgnoreCase(indexName))
        .findFirst();
  }
}
