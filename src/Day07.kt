import kotlin.math.abs

fun main() {

    val day = "07"

    fun part1(input: List<String>): Int {
        val positions = input.first().split(",").map { it.toInt() }
        val costs = mutableListOf<Int>()
        for (goal in (1..positions.maxOf { it })) {
            costs.add(positions.sumOf { pos -> abs(pos - goal) })
        }
        return costs.minOf { it }
    }

    fun part2(input: List<String>): Int {
        return -1
    }

    val testInput = readInput("Day${day}_test")
    check(part1(testInput) == 37)
//    check(part2(testInput) == 0)

    val input = readInput("Day${day}")
    println(part1(input))
//    println(part2(input))
}