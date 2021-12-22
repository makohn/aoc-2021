import kotlin.math.abs

fun main() {

    val day = "07"

    fun part1(input: List<String>): Int {
        val positions = parse(input)
        return minimizeCost(positions) { goal ->
            positions.sumOf { pos -> abs(pos - goal) }
        }
    }

    fun part2(input: List<String>): Int {
        val positions = parse(input)
        return minimizeCost(positions) { goal ->
            positions.sumOf { pos ->
                var sum = 0
                for (j in (1 .. abs(pos - goal))) sum += j
                sum
            }
        }
    }

    val testInput = readInput("Day${day}_test")
    check(part1(testInput) == 37)
    check(part2(testInput) == 168)

    val input = readInput("Day${day}")
    println(part1(input))
    println(part2(input))
}

private fun parse(input: List<String>) = input.first().split(",").map { it.toInt() }

private fun minimizeCost(positions: List<Int>, costFunction: (Int) -> Int): Int {
    val costs = mutableListOf<Int>()
    for (goal in (1..positions.maxOf { it })) {
        costs.add(costFunction.invoke(goal))
    }
    return costs.minOf { it }
}