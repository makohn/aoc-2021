fun main() {

    val day = "05"

    fun part1(input: List<String>): Int {
        val instructions = input
            .flatMap { it.split(" -> ").flatMap { p -> p.split(",") } }
            .asSequence().map { it.toInt() }
            .chunked(2).map { it.first() to it.last() }
            .chunked(2).map { it.first() to it.last() }
            .toList()

        return instructions
            .filter { it.first.first == it.second.first || it.first.second == it.second.second }
            .flatMap { (it.first .. it.second) }
            .groupingBy { it }.eachCount()
            .filter { it.value >= 2 }.count()
    }

    fun part2(input: List<String>): Int {
        return 0
    }

    val testInput = readInput("Day${day}_test")
    check(part1(testInput) == 5)
//    check(part2(testInput) == 0)

    val input = readInput("Day${day}")
    println(part1(input))
//    println(part2(input))
}

infix operator fun Pair<Int, Int>.rangeTo(other: Pair<Int, Int>) = generateSequence(this) {
    if (it.first < other.first) it.first + 1 to it.second
    else if (it.first > other.first) it.first - 1 to it.second
    else if (it.second < other.second) it.first to it.second + 1
    else if (it.second > other.second) it.first to it.second - 1
    else null
}