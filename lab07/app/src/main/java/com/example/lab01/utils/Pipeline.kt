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
    private var nodes = ArrayDeque<PipelineNode>()

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

    fun add(vararg args: Any, function: Function) {
        nodes.addLast(
            PipelineNode(
                args = args.toList(),
                function = function))
    }

    fun execute(mat: FloatArray) {
        for (node in nodes) {
            node.function(mat, node.args)
        }
        nodes.clear()
    }

    fun execute(mat: FloatArray, needToExecuteUnique: Boolean) {
        if (needToExecuteUnique) {
            Matrix.setIdentityM(mat, 0)
            for (node in unique) {
                node.function(mat, node.args)
            }
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