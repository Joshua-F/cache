package com.runesuite.cache.format

import com.runesuite.cache.extensions.update
import com.runesuite.cache.extensions.value32
import io.netty.buffer.ByteBuf
import io.netty.buffer.PooledByteBufAllocator
import java.util.zip.CRC32

interface Archive {

    val compressor: Compressor get() = Compressor.NONE

    val compressed: ByteBuf get() = compressor.compress(decompressed.slice())

    val decompressed: ByteBuf get() = compressor.decompress(compressed.slice())

    val version: Int? get() = null

    val crc: Int get() {
        val crc = CRC32()
        crc.update(buffer)
        return crc.value32
    }

    val buffer: ByteBuf get() {
        val b = PooledByteBufAllocator.DEFAULT.buffer(HEADER_LENGTH + compressed.readableBytes() + FOOTER_LENGTH)
        b.writeByte(compressor.id.toInt())
        b.writeInt(compressed.readableBytes() - compressor.headerLength)
        b.writeBytes(decompressed)
        version?.let {
            b.writeShort(it)
        }
        return b
    }

    companion object {
        const val HEADER_LENGTH = java.lang.Byte.BYTES + Integer.BYTES
        const val FOOTER_LENGTH = java.lang.Short.BYTES
    }
}