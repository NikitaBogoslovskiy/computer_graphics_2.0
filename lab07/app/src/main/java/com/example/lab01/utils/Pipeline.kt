package com.example.lab01.utils

import android.opengl.Matrix
import java.util.concurrent.atomic.AtomicBoolean

typealias Function = (FloatArray, List<Any>) -> Unit

data class PipelineNode(var args: List<Any>,
                        var function: Function)

class Pipeline {
    var hasExecutedUnique = false
    private var unique = ArrayDeque<PipelineNode>()
    private var repeatable = ArrayDeque<PipelineNode>()

    fun addUnique(vararg args: Any, function: Function) {
        unique.addLast(
            PipelineNode(
                args = args.toList(),
                function = function))
    }

    fun addRepeatable(vararg args: Any, function: Function) {
        repeatable.addLast(
            PipelineNode(
                args = args.toList(),
                function = function))
    }

    fun execute(mat: FloatArray) {
        if (!hasExecutedUnique) {
            Matrix.setIdentityM(mat, 0)
            for (node in unique) {
                node.function(mat, node.args)
            }
            hasExecutedUnique = true
        }

        for (node in repeatable) {
            node.function(mat, node.args)
        }
    }

    fun reset() {
        unique.clear()
        repeatable.clear()
        hasExecutedUnique = false
    }
}