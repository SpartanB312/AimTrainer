package net.spartanb312.boar.graphics.matrix

import com.soywiz.kds.iterators.fastForEachReverse
import org.joml.Matrix4f
import org.lwjgl.opengl.GL11
import java.util.*

class MatrixStack {

    private val stack = Stack<Matrix4f>().apply {
        push(Matrix4f())
    }

    fun pushMatrix(matrix4f: Matrix4f = Matrix4f()) {
        stack.push(matrix4f)
    }

    fun popMatrix(): Matrix4f {
        return if (stack.empty()) {
            throw Exception("Stack must has at least 1 matrix!")
        } else if (stack.size == 1) {
            stack.peek().identity()
        } else {
            stack.pop()
        }
    }

    fun getMatrix(): Matrix4f {
        val result = Matrix4f()
        stack.fastForEachReverse { mat ->
            result.mul(mat)
        }
        return result
    }

    fun current(): Matrix4f = stack.peek()

    fun applyToGL() = GL11.glLoadMatrixf(getMatrix().getFloatArray())

    fun mulToGL() = GL11.glMultMatrixf(getMatrix().getFloatArray())

}