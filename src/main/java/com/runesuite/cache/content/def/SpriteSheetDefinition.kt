package com.runesuite.cache.content.def

import com.runesuite.cache.extensions.readArray
import com.runesuite.cache.extensions.toUnsigned
import io.netty.buffer.ByteBuf
import java.awt.image.BufferedImage

class SpriteSheetDefinition : CacheDefinition() {

    companion object {
        const val IMAGE_TYPE = BufferedImage.TYPE_INT_ARGB
    }

    class Sprite {
        var offsetX: Int = 0
        var offsetY: Int = 0
        var subWidth: Int = 0
        var subHeight: Int = 0
        var flags: Int = 0
        lateinit var paletteColors: ByteArray
        var alphas: ByteArray? = null
    }

    var spriteWidth: Int = 0
    var spriteHeight: Int = 0
    lateinit var sprites: Array<Sprite>
    lateinit var palette: IntArray

    fun toImage(index: Int): BufferedImage {
        val s = sprites[index]
        val img = BufferedImage(spriteWidth, spriteHeight, IMAGE_TYPE)
        for (y in 0 until s.subHeight) {
            for (x in 0 until s.subWidth) {
                val arrayIndex = when (s.flags and Flag.VERTICAL.id != 0) {
                    true -> x * s.subHeight + y
                    false -> y * s.subWidth + x
                }
                val paletteIndex = s.paletteColors[arrayIndex].toUnsigned()
                val rgb = palette[paletteIndex]
                val alpha = when (s.flags and Flag.ALPHA.id != 0) {
                    true -> s.alphas!![arrayIndex].toUnsigned()
                    false -> rgb.takeIf { it == 0 } ?: 0xFF
                }
                val argb = rgb or (alpha shl 24)
                img.setRGB(x + s.offsetX, y + s.offsetY, argb)
            }
        }
        return img
    }

    fun toImage(): BufferedImage {
        val bi = BufferedImage(spriteWidth * sprites.size, spriteHeight, SpriteSheetDefinition.IMAGE_TYPE)
        val g = bi.graphics
        sprites.forEachIndexed { i, s ->
            g.drawImage(toImage(i), i * spriteWidth, 0, null)
        }
        g.dispose()
        return bi
    }

    override fun read(buffer: ByteBuf) {
        val spriteCount = buffer.getUnsignedShort(buffer.writerIndex() - 2)
        check(spriteCount > 0)
        buffer.markReaderIndex()
        buffer.readerIndex(buffer.writerIndex() - 7 - spriteCount * 8)
        spriteWidth = buffer.readUnsignedShort()
        spriteHeight = buffer.readUnsignedShort()
        val paletteLength = buffer.readUnsignedByte() + 1
        sprites = Array(spriteCount) { Sprite() }
        sprites.forEach { it.offsetX = buffer.readUnsignedShort() }
        sprites.forEach { it.offsetY = buffer.readUnsignedShort() }
        sprites.forEach { it.subWidth = buffer.readUnsignedShort() }
        sprites.forEach { it.subHeight = buffer.readUnsignedShort() }
        buffer.resetReaderIndex()
        sprites.forEach { s ->
            val dimension = s.subWidth * s.subHeight
            s.flags = buffer.readUnsignedByte().toInt()
            s.paletteColors = buffer.readArray(dimension)
            if (s.flags and Flag.ALPHA.id != 0) {
                s.alphas = buffer.readArray(dimension)
            }
        }
        palette = IntArray(paletteLength) {
            if (it == 0) 0
            else buffer.readUnsignedMedium().takeUnless { it == 0 } ?: 1
        }
    }

    enum class Flag(idPosition: Int) {
        VERTICAL(0),
        ALPHA(1);

        val id = 1 shl idPosition
    }
}