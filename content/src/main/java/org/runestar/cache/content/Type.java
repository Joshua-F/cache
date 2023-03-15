package org.runestar.cache.content;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public enum Type {
    AREA(16, 'R'),
    BOOLEAN(1, '1'),
    CATEGORY(41, 'y'),
    CHAR(42, 'z'),
    COMPONENT(9, 'I'),
    COORD(22, 'c'),
    DBROW(74, 'Ð'),
    ENUM(26, 'g'),
    FONTMETRICS(25, 'f'),
    GRAPHIC(23, 'd'),
    INT(0, 'i'),
    INTERFACE(97, 'a'),
    INV(39, 'v'),
    LOC(30, 'l'),
    LOCSHAPE(8, 'H'),
    MAPAREA(21, '`'),
    MAPELEMENT(59, 'µ'),
    MODEL(31, 'm'),
    NAMEDOBJ(13, 'O'),
    ENTITYOVERLAY('-'),
    NPC(32, 'n'),
    NPC_UID(38, 'u'),
    OBJ(33, 'o'),
    OVERLAYINTERFACE(99, 'L'),
    PARAM,
    PLAYER_UID(34, 'p'),
    SEQ(6, 'A'),
    SPOTANIM(37, 't'),
    STAT(17, 'S'),
    STRING(36, 's'),
    STRUCT(73, 'J'),
    SYNTH(14, 'P'),
    TOPLEVELINTERFACE(98, 'F'),
    TYPE,
    ;

    private static final Map<Integer, Type> ID_TO_TYPE = Arrays.stream(values())
            .filter(type -> type.getId() != -1)
            .collect(Collectors.toMap(Type::getId, Function.identity()));

    private static final Map<Byte, Type> VALUES = Arrays.stream(values())
            .filter(type -> type.getDesc() != 0)
            .collect(Collectors.toMap(Type::getDesc, Function.identity()));

    private final int id;
    private final byte desc;
    private final String literal;

    Type() {
        this('\0');
    }

    Type(char desc) {
        this(-1, desc);
    }

    Type(int id, char desc) {
        this.id = id;
        this.desc = toCp1252Byte(desc);
        literal = name().toLowerCase();
    }

    public int getId() {
        return id;
    }

    public byte getDesc() {
        return desc;
    }

    public String getLiteral() {
        return literal;
    }

    public static Type ofAuto(int id) {
        Type value = ID_TO_TYPE.get(id);
        if (value == null) {
            throw new IllegalStateException(id + " does not map to a type.");
        }
        return value;
    }

    public static Type ofAuto(byte desc) {
        Type value = VALUES.get(desc);
        if (value == null) {
            throw new IllegalStateException();
        }
        return value;
    }

    private static byte toCp1252Byte(char ch) {
        final Charset CP1252 = Charset.forName("windows-1252");
        CharBuffer buffer = CharBuffer.allocate(1).append(ch).flip();
        ByteBuffer encoded = CP1252.encode(buffer);
        return encoded.get();
    }
}