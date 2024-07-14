package sqlite.domain;

import java.nio.ByteBuffer;
import java.security.InvalidParameterException;
import sqlite.buffer.VarInt;

public record Cell(long leftChild, long rowId, byte[] payload) {

  public static Cell parse(PageType type, ByteBuffer buf) {

    switch (type) { // TODO: Implement other page types
      case LeafTable:
        var payloadLength = VarInt.parse(buf).value();
        var rowId = VarInt.parse(buf).value();

        var payloadBytes = new byte[(int)payloadLength];
        buf.get(payloadBytes);

        return new Cell(0, rowId, payloadBytes);

      default: throw new InvalidParameterException("Unsupported page pageType");
    }
  }
}




