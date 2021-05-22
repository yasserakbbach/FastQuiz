package com.ressay.fastquiz.utils

import java.util.*
import kotlin.math.floor

class SegmentUtil(
    private val total : Int,
    private val countBySegment : Int
) {

    private val stackPairs = Stack<Pair<Int, Int>>()
    private lateinit var currentPair: Pair<Int, Int>

    fun getPairSegments() : List<Pair<Int, Int>> {

        val loops = total / countBySegment
        val modulus = total % countBySegment
        val segments = mutableListOf<Pair<Int, Int>>()

        for(i in 0 until floor(loops.toDouble()).toInt()) {
            segments.add(Pair(i * countBySegment, (countBySegment - 1) + (i * countBySegment)))
        }
        if(modulus >= 1) {
            segments.add(Pair(total - countBySegment, total - 1))
        }

        stackPairs.addAll(segments)
        return segments
    }

    fun hasPairs() = !stackPairs.empty()

    fun nextPair() : Pair<Int, Int> {
        currentPair = stackPairs.pop()
        return currentPair
    }

    fun currentPairToRange() : MutableList<String> {
        val range = mutableListOf<String>()
        for(i in currentPair.first+1..currentPair.second+1) {
            range.add(i.toString())
        }
        return range
    }
}