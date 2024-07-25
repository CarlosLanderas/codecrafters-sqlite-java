package sqlite.query;

import java.util.Collection;

public record SelectQueryPredicate(
    String tableName,
    Collection<String> columns,
    String filterColumn,
    String filterValue) {
}