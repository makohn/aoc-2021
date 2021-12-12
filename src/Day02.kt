fun main() {
    fun part1(input: List<String>): Int = input
        .map { it.split(" ") }
        .map { it.first() to it.last().toInt() }
        .map { if (it.first == "up") "down" to -it.second else it }
        .groupBy( { it.first }, { it.second } )
        .mapValues { it.value.sum() }
        .values
        .reduce(Int::times)

    fun part2(input: List<String>): Int = TODO()

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day02_test")
    check(part1(testInput) == 150)

    val input = readInput("Day02")
    println(part1(input))
}
