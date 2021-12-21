import Day21.playGame

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

    fun part2(input: List<String>): Long {
        val (p1, p2) = input.map { it.last().digitToInt() }
        val res = playGame(p1, p2)
        return maxOf(res.first, res.second)
    }

    val testInput = readInput("Day${day}_test")
    check(part1(testInput) == 739785)
    check(part2(testInput) == 444356092776315L)

    val input = readInput("Day${day}")
    println(part1(input))
    println(part2(input))
}

class Die {
    private var pos = 1
    fun roll(n: Int) = sequence { while (true) { yield(((pos++ -1) % 100) + 1) } }.take(n)
}

object Day21 {

    fun playGame(p1: Int, p2: Int): Pair<Long, Long> {
        val cache = mutableMapOf<Quadruple<Int>, Pair<Long, Long>>()

        fun rollDice(state: Quadruple<Int>): Pair<Long, Long> {
            val (pos1, pos2, score1, score2) = state
            if (cache.contains(state)) return cache[state]!!
            if (score1 >= 21) return (1L to 0L)
            if (score2 >= 21) return (0L to 1L)

            var score = 0L to 0L

            // Dice 3 times -> Consider all result combinations (i, j, k) for i, j, k in (1, 2, 3)
            // We basically iterate over a tree depth-first, starting with the 1 -> 1 -> 1 -> ... "universe"
            // This is recursively repeated for each branch, alternating between player 1 and player 2 until either on reaches 21
            for (i in (1 .. 3)) {
                for (j in (1 .. 3)) {
                    for (k in (1 .. 3)) {
                        // Move position by sum of dice results (clockwise) (-1 / +1 to account for 10 % 10 being 0)
                        val newPos1 = ((pos1 + i + j + k - 1) % 10) + 1
                        val newScore1 = score1 + newPos1
                        // Now roll the dice for player 2 (recursively rolls again for player 1 and so on)
                        val newScore = rollDice(Quadruple(pos2, newPos1, score2, newScore1))
                        // Add the final score for this branch to overall score
                        score = score.first + newScore.second to score.second + newScore.first
                    }
                }
            }
            cache[state] = score
            return score
        }
        return rollDice(Quadruple(p1, p2, 0, 0))
    }
}