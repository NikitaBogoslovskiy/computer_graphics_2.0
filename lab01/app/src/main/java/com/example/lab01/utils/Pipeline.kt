package com.example.lab01.utils

import android.opengl.Matrix

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

    fun executeUnique(mat: FloatArray) {
        for (node in unique) {
            node.function(mat, node.args)
        }
        hasExecutedUnique = true
    }

    fun executeRepeatable(mat: FloatArray) {
        for (node in repeatable) {
            node.function(mat, node.args)
        }
    }

    fun reset() {
        unique.clear()
        repeatable.clear()
    }
}