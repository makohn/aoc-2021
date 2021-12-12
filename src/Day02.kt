fun main() {

    data class Position(var hor: Int = 0, var aim: Int = 0, var depth: Int = 0)

    fun part1(input: List<String>): Int = common(input)
        .groupBy( { it.first }, { it.second } )
        .mapValues { it.value.sum() }
        .values
        .reduce(Int::times)

    fun part2(input: List<String>): Int = common(input)
        .fold(Position()) { pos, p -> if (p.first == "down")
            pos.apply { aim += p.second } else
            pos.apply { hor += p.second; depth += p.second * aim }}
        .let { it.depth * it.hor }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day02_test")
    check(part1(testInput) == 150)
    check(part2(testInput) == 900)

    val input = readInput("Day02")
    println(part1(input))
    println(part2(input))
}

fun common(input: List<String>): List<Pair<String, Int>> = input
    .map { it.split(" ") }
    .map { it.first() to it.last().toInt() }
    .map { if (it.first == "up") "down" to -it.second else it }
