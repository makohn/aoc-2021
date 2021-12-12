fun main() {

    fun part1(input: List<String>): Int = input
        .map { it.toList() }
        .transpose()
        .map { list -> list.groupingBy { it }.eachCount() }
        .map { map -> listOf(map.maxByOrNull { it.value }?.key, map.minByOrNull { it.value }?.key) }
        .transpose()
        .map { it.joinToString("").toInt(2) }
        .reduce(Int::times)

    fun part2(input: List<String>): Int = TODO()

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day03_test")
    check(part1(testInput) == 198)

    val input = readInput("Day03")
    println(part1(input))
}

fun <T> List<List<T>>.transpose(): List<List<T>> {
    val result = (first().indices).map { mutableListOf<T>() }.toMutableList()
    forEach { list -> result.zip(list).forEach { it.first.add(it.second) } }
    return result
}