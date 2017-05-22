package com.runesuite.cache.extensions

import java.nio.ByteBuffer

fun Byte.toUnsigned(): Int {
    return java.lang.Byte.toUnsignedInt(this)
}

fun Short.toUnsigned(): Int {
    return java.lang.Short.toUnsignedInt(this)
}

inline fun ByteArray.fill(value: (Int) -> Byte): ByteArray {
    indices.forEach {
        set(it, value(it))
    }
    return this
}

inline fun ByteArray.selfMap(transform: (Byte) -> Byte): ByteArray {
    indices.forEach {
        set(it, transform(get(it)))
    }
    return this
}

inline fun ByteArray.selfMapIndexed(transform: (Int, Byte) -> Byte): ByteArray {
    indices.forEach {
        set(it, transform(it, get(it)))
    }
    return this
}

fun IntArray.asByteArray(): ByteArray {
    val b = ByteBuffer.allocate(size * Integer.BYTES)
    b.asIntBuffer().put(this)
    return b.array()
}