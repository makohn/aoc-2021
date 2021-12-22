fun main() {

    val day = "08"

    fun part1(input: List<String>): Int {
        val outputs = input
            .map { it.split(" | ") }
            .map { it.first() to it.last() }
            .map { it.second.split(" ") }
        return outputs.sumOf { it.count { output -> output.length in listOf(2, 3, 4, 7) } }
    }

    fun part2(input: List<String>): Int {
        return -1
    }

    val testInput = readInput("Day${day}_test")
    check(part1(testInput) == 26)
//    check(part2(testInput) == 0)

    val input = readInput("Day${day}")
    println(part1(input))
//    println(part2(input))
}