package net.spartanb312.boar.graphics.drawing

import org.lwjgl.opengl.GL11

enum class VertexFormat(vararg val elements: Element) {

    Pos2fColor(Element.Position2f, Element.Color),// 12
    Pos3fColor(Element.Position3f, Element.Color),// 16
    Pos2fTex(Element.Position2f, Element.Color, Element.Texture),// 20
    Pos3fTex(Element.Position3f, Element.Color, Element.Texture),// 24
    Pos2dColor(Element.Position2d, Element.Color),// 20
    Pos3dColor(Element.Position3d, Element.Color),// 28
    Pos2dTex(Element.Position2d, Element.Color, Element.Texture),// 28
    Pos3dTex(Element.Position3d, Element.Color, Element.Texture),// 36
    Pos2iColor(Element.Position2i, Element.Color),// 12
    Pos3iColor(Element.Position3i, Element.Color),// 16
    Pos2iTex(Element.Position2i, Element.Color, Element.Texture),// 20
    Pos3iTex(Element.Position3i, Element.Color, Element.Texture);// 24

    val totalLength: Int

    init {
        var count = 0
        elements.forEach { count += it.length }
        totalLength = count
    }

    enum class Element(val category: Category, val constant: Int, val count: Int, val length: Int) {
        Position2f(Category.Position, GL11.GL_FLOAT, 2, 8),
        Position3f(Category.Position, GL11.GL_FLOAT, 3, 12),
        Position2d(Category.Position, GL11.GL_DOUBLE, 2, 16),
        Position3d(Category.Position, GL11.GL_DOUBLE, 3, 24),
        Position2i(Category.Position, GL11.GL_INT, 2, 8),
        Position3i(Category.Position, GL11.GL_INT, 3, 12),
        Color(Category.Color, GL11.GL_UNSIGNED_BYTE, 4, 4),
        Texture(Category.Texture, GL11.GL_FLOAT, 2, 8);

        enum class Category {
            Position,
            Color,
            Texture
        }
    }

}