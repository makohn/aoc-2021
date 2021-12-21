
fun main() {

    val day = "21"

    fun part1(input: List<String>): Int {
        var playerPositions = input.map { it.last().digitToInt() }
        val die = Die()
        var scores = listOf(0, 0)
        var newScores = scores
        var i = 0
        while (1000 !in newScores) {
            val newPositions = playerPositions.map { (((it + die.roll(3).sum()) -1) % 10) + 1 }
            newScores = scores.zip(newPositions).map { it.first + it.second }
            playerPositions = newPositions
            if (1000 !in newScores) {
                scores = newScores
                i += 6
            } else { i += 3 }
        }
        return i * scores.minOf { it }
    }

    fun part2(input: List<String>): Int {
        return 0
    }

    val testInput = readInput("Day${day}_test")
    check(part1(testInput) == 739785)
    check(part2(testInput) == 0)

    val input = readInput("Day${day}")
    println(part1(input))
    println(part2(input))
}

class Die {
    private var pos = 1
    fun roll(n: Int) = sequence { while (true) { yield(((pos++ -1) % 100) + 1) } }.take(n)
}