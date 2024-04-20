package net.spartanb312.everett.graphics.drawing

import net.spartanb312.everett.graphics.GLDataType

enum class VertexFormat(vararg val elements: Element) {

    Pos2fColor(Element.Position2f, Element.Color),// 12
    Pos3fColor(Element.Position3f, Element.Color),// 16
    Pos2fColorTex(Element.Position2f, Element.Color, Element.Texture),// 20
    Pos3fColorTex(Element.Position3f, Element.Color, Element.Texture),// 24
    Pos3fTex(Element.Position3f, Element.Texture),
    Pos2dColor(Element.Position2d, Element.Color),// 20
    Pos3dColor(Element.Position3d, Element.Color),// 28
    Pos2dColorTex(Element.Position2d, Element.Color, Element.Texture),// 28
    Pos3dColorTex(Element.Position3d, Element.Color, Element.Texture),// 36
    Pos2iColor(Element.Position2i, Element.Color),// 12
    Pos3iColor(Element.Position3i, Element.Color),// 16
    Pos2iTex(Element.Position2i, Element.Color, Element.Texture),// 20
    Pos3iTex(Element.Position3i, Element.Color, Element.Texture),// 24

    Pos3fNormalTex(Element.Position3f, Element.Position3f, Element.Texture);// 32

    val totalLength: Int

    init {
        var count = 0
        elements.forEach { count += it.length }
        totalLength = count
    }

    val attribute = buildAttribute(totalLength) {
        var index = 0
        elements.forEach {
            when (it.category) {
                Element.Category.Position -> {
                    float(index, it.count, it.glDataType, false)
                }

                Element.Category.Color -> {
                    float(index, it.count, it.glDataType, true)
                }

                Element.Category.Texture -> {
                    float(index, it.count, it.glDataType, false)
                }
            }
            index++
        }
    }

    enum class Element(val category: Category, val glDataType: GLDataType, val count: Int, val length: Int) {
        Position2f(Category.Position, GLDataType.GL_FLOAT, 2, 8),
        Position3f(Category.Position, GLDataType.GL_FLOAT, 3, 12),
        Position2d(Category.Position, GLDataType.GL_DOUBLE, 2, 16),
        Position3d(Category.Position, GLDataType.GL_DOUBLE, 3, 24),
        Position2i(Category.Position, GLDataType.GL_INT, 2, 8),
        Position3i(Category.Position, GLDataType.GL_INT, 3, 12),
        Color(Category.Color, GLDataType.GL_UNSIGNED_BYTE, 4, 4),
        Texture(Category.Texture, GLDataType.GL_FLOAT, 2, 8);

        enum class Category {
            Position,
            Color,
            Texture
        }
    }

}