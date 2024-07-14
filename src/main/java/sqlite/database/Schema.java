package sqlite.database;

import java.util.List;
import java.util.Optional;
import sqlite.domain.Table;

public record Schema(List<Table> tables) {
  public Optional<Table> findTable(String tableName) {
    return tables
        .stream()
        .filter(t -> t.name().equalsIgnoreCase(tableName))
        .findFirst();
  }
}
