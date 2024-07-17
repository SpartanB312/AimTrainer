package net.spartanb312.everett.graphics.uniform

import net.spartanb312.everett.utils.math.ceilToInt

/**
 * Using std140 storage layout
 * Notice: please check the max caps of alignOffset of current device
 */
fun main() {
    val a = UniformBlock {
        vec3("position") // 16
        float("constant") // 4
    }
    println(a.desc)
    println(a.size)
}

class UniformBlock(builder: Struct.() -> Unit) : IStruct by Struct.build(builder = builder)

interface Attribute {
    val name: String
    val type: String
    val baseAlign: Int
    val baseOffset: Int
    val alignOffset: Int
    val used: Int
    val desc: String get() = "$type $name; $baseAlign, $baseOffset, $alignOffset"
    val isDataAttribute get() = this !is StructPadding
}

interface IStruct {
    val elements: MutableList<Attribute>
    val size: Int
    val desc: String
}

open class Struct(
    override val name: String,
    override val type: String = "Struct",
    private val lastMember: Attribute? = null
) : Attribute, IStruct {

    override val elements = mutableListOf<Attribute>()

    final override val baseAlign: Int = 16
    final override val used: Int = 0
    final override val baseOffset: Int = lastMember?.let { it.alignOffset + it.used } ?: 0
    final override val alignOffset: Int = (baseOffset / baseAlign.toFloat()).ceilToInt() * baseAlign
    private val last: Attribute
        get() {
            val lastElement = elements.lastOrNull()
            return if (lastElement is Struct) lastElement.last
            else lastElement ?: this
        }
    override val size get() = last.let { it.baseOffset + it.used }
    override val desc: String
        get() {
            var str = if (lastMember == null) "" else "\n" + super.desc + "\n"
            elements.forEachIndexed { index, it ->
                str += if (index != elements.size - 1) it.desc + "\n"
                else if (lastMember == null) "" else it.desc + "\n"
            }
            return str
        }

    fun float(name: String): Int {
        val attribute = FloatAttribute(name, last)
        elements.add(attribute)
        return attribute.alignOffset
    }

    fun int(name: String): Int {
        val attribute = IntAttribute(name, last)
        elements.add(attribute)
        return attribute.alignOffset
    }

    fun vec2(name: String): Int {
        val attribute = Vec2Attribute(name, last)
        elements.add(attribute)
        return attribute.alignOffset
    }

    fun vec3(name: String): Int {
        val attribute = Vec3Attribute(name, last)
        elements.add(attribute)
        return attribute.alignOffset
    }

    fun vec4(name: String): Int {
        val attribute = Vec4Attribute(name, last)
        elements.add(attribute)
        return attribute.alignOffset
    }

    fun mat3(name: String): Int {
        return struct(name, "Matrix3x3") {
            vec3("$name-1")
            vec3("$name-2")
            vec3("$name-3")
        }
    }

    fun mat4(name: String): Int {
        return struct(name, "Matrix4x4") {
            vec4("$name-1")
            vec4("$name-2")
            vec4("$name-3")
            vec4("$name-4")
        }
    }

    fun mat2x3(name: String): Int {
        return struct(name, "Matrix2x3") {
            vec3("$name-1")
            vec3("$name-2")
        }
    }

    fun floatArray(name: String, size: Int): Int {
        return struct(name, "FloatArray") {
            for (index in 1..size) {
                elements.add(FloatArrayMember("$name-$index", last))
            }
        }
    }

    fun intArray(name: String, size: Int): Int {
        return struct(name, "IntArray") {
            for (index in 1..size) {
                elements.add(IntArrayMember("$name-$index", last))
            }
        }
    }

    fun vec2Array(name: String, size: Int): Int {
        return struct(name, "Vec2Array") {
            for (index in 1..size) {
                elements.add(Vec2ArrayMember("$name-$index", last))
            }
        }
    }

    fun vec3Array(name: String, size: Int): Int {
        return struct(name, "Vec3Array") {
            for (index in 1..size) {
                elements.add(Vec3ArrayMember("$name-$index", last))
            }
        }
    }

    fun vec4Array(name: String, size: Int): Int {
        return struct(name, "Vec4Array") {
            for (index in 1..size) {
                elements.add(Vec4ArrayMember("$name-$index", last))
            }
        }
    }

    fun structArray(name: String, size: Int, builder: Struct.() -> Unit): Int {
        var startOffset = 0
        for (index in 1..size) {
            val alignOffset = struct(name, "StructArray-$index", builder)
            if (index == 1) startOffset = alignOffset
        }
        return startOffset
    }

    fun struct(name: String, type: String = "Struct", builder: Struct.() -> Unit): Int {
        val struct = build(name, type, last, builder)
        elements.add(struct)
        return struct.alignOffset
    }

    fun end() {
        elements.add(StructPadding(name, last))
    }

    companion object {
        fun build(
            name: String = "default",
            type: String = "Struct",
            lastMember: Attribute? = null,
            builder: Struct.() -> Unit
        ): Struct {
            val struct = Struct(name, type, lastMember)
            struct.builder()
            struct.end()
            return struct
        }
    }
}

open class BasicAttribute(
    final override val name: String,
    lastMember: Attribute,
    final override val type: String,
    final override val baseAlign: Int,
    final override val used: Int
) : Attribute {
    final override val baseOffset: Int = lastMember.let { it.alignOffset + it.used }
    final override val alignOffset: Int = (baseOffset / baseAlign.toFloat()).ceilToInt() * baseAlign
}

class FloatAttribute(name: String, lastMember: Attribute) : BasicAttribute(name, lastMember, "Float", 4, 4)
class IntAttribute(name: String, lastMember: Attribute) : BasicAttribute(name, lastMember, "Int", 4, 4)
class FloatArrayMember(name: String, lastMember: Attribute) : BasicAttribute(name, lastMember, "FloatA", 16, 4)
class IntArrayMember(name: String, lastMember: Attribute) : BasicAttribute(name, lastMember, "IntA", 16, 4)
class Vec2ArrayMember(name: String, lastMember: Attribute) : BasicAttribute(name, lastMember, "Vec2A", 16, 8)
class Vec3ArrayMember(name: String, lastMember: Attribute) : BasicAttribute(name, lastMember, "Vec2A", 16, 12)
class Vec4ArrayMember(name: String, lastMember: Attribute) : BasicAttribute(name, lastMember, "Vec2A", 16, 16)
class Vec2Attribute(name: String, lastMember: Attribute) : BasicAttribute(name, lastMember, "Vec2", 8, 8)
class Vec3Attribute(name: String, lastMember: Attribute) : BasicAttribute(name, lastMember, "Vec3", 16, 12)
class Vec4Attribute(name: String, lastMember: Attribute) : BasicAttribute(name, lastMember, "Vec4", 16, 16)
class StructPadding(name: String, lastMember: Attribute) : BasicAttribute(name, lastMember, "Padding", 16, 0)