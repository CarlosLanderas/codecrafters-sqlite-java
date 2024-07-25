package sqlite.domain;

import java.nio.ByteBuffer;
import java.security.InvalidParameterException;

import sqlite.buffer.VarInt;

public record Cell(long leftChild, long rowId, byte[] payload, PageType type) {

    public static Cell parse(PageType type, ByteBuffer buf) {

        long payloadLength = 0;
        long rowId = 0;
        byte[] payloadBytes = null;

        switch (type) { 
            case LeafTable:
                payloadLength = VarInt.parse(buf).value();
                rowId = VarInt.parse(buf).value();

                payloadBytes = new byte[(int) payloadLength];
                buf.get(payloadBytes);

                return new Cell(0, rowId, payloadBytes, PageType.LeafTable);

            case InteriorTable:
                var leftChild = buf.getInt();
                rowId = VarInt.parse(buf).value();

                return new Cell(leftChild, rowId, null, PageType.InteriorTable);

            case LeafIndex:
                payloadLength = VarInt.parse(buf).value();
                payloadBytes = new byte[(int) payloadLength];

                buf.get(payloadBytes);

                return new Cell(0, 0, payloadBytes, PageType.LeafIndex);
            case InteriorIndex:
                var leftchild = buf.getInt();
                payloadLength = VarInt.parse(buf).value();

                payloadBytes = new byte[(int) payloadLength];
                buf.get(payloadBytes);

                return new Cell(leftchild, 0, payloadBytes, PageType.InteriorIndex);

            default:
                throw new InvalidParameterException("Unsupported page pageType");
        }
    }
}




