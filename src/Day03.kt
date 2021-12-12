fun main() {

    fun part1(input: List<String>): Int = input
        .map { it.toList() }
        .transpose()
        .map { list -> list.groupingBy { it }.eachCount() }
        .map { map -> listOf(map.maxByOrNull { it.value }?.key, map.minByOrNull { it.value }?.key) }
        .transpose()
        .map { it.joinToString("").toInt(2) }
        .reduce(Int::times)

    fun part2(input: List<String>): Int = input
        .map { it.toInt(2) }
        .let { list ->
            var a = list
            var b = list
            val max = input.first().count()
            var pos = 0
            while ((a.size > 1) and (pos < max)) {
                val x = 1 shl max-1 shr pos
                val (l1, l2) = a.partition { (it and x) == x }
                a = if (l1.size >= l2.size) l1 else l2
                pos += 1
            }
            pos = 0
            while ((b.size > 1) and (pos < max)) {
                val x = 1 shl max-1 shr pos
                val (l1, l2) = b.partition { (it and x) == x }
                b = if (l1.size >= l2.size) l2 else l1
                pos += 1
            }
            listOf(a.first(), b.first())
        }
        .reduce(Int::times)

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day03_test")
    check(part1(testInput) == 198)
    check(part2(testInput) == 230)

    val input = readInput("Day03")
    println(part1(input))
    println(part2(input))
}

fun <T> List<List<T>>.transpose(): List<List<T>> {
    val result = (first().indices).map { mutableListOf<T>() }.toMutableList()
    forEach { list -> result.zip(list).forEach { it.first.add(it.second) } }
    return result
}