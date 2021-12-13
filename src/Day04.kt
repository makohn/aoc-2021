const val numRows = 5
const val boardSize = numRows * numRows
const val marker = -1
const val invalidIndex = -1

fun main() {
    fun part1(input: List<String>): Int {
        val (numbers, games) = parseInput(input)
        val (number, index) = evaluate(numbers, games)
        return games.chunked(boardSize)[index].filter { it > marker }.sum() * number
    }

    fun part2(input: List<String>): Int {
        val (numbers, games) = parseInput(input)
        val (number, index) = evaluate(numbers, games) { false }
        return games.chunked(boardSize)[index].filter { it > marker }.sum() * number
    }

    val testInput = readInput("Day04_test")
    check(part1(testInput) == 4512)
    check(part2(testInput) == 1924)

    val input = readInput("Day04")
    println(part1(input))
    println(part2(input))
}

fun evaluate(numbers: List<Int>, games: MutableList<Int>, terminate: (Set<Int>) -> Boolean = Set<Int>::isNotEmpty)
        : Pair<Int, Int> {
    var winningIndex = invalidIndex
    var winningNumber = invalidIndex
    val indices = mutableSetOf<Int>()
    numbers
        .dropWhile { number ->
            games.replaceAll { nr -> if (nr == number) marker else nr }
            val index = games
                .asSequence()
                .chunked(boardSize)
                .withIndex()
                .filter(::isBingo)
                .map { it.index }
                .filter { !indices.contains(it) }
                .toList()
            if (index.isNotEmpty()) {
                winningIndex = index.last()
                winningNumber = number
                indices += index
            }
            !terminate(indices) && indices.size < games.size / 25
        }
    return winningNumber to winningIndex
}

fun parseInput(input: List<String>): Pair<List<Int>, MutableList<Int>> {
    val numbers = input.first().toString().split(",").map(String::toInt)
    val games = input
        .asSequence()
        .drop(1)
        .filter { it.isNotEmpty() }
        .map { it.trim().split("\\s+".toRegex()).map(String::toInt) }
        .flatten()
        .toMutableList()
    return numbers to games
}

fun isBingo(game: IndexedValue<List<Int>>) = bingoRow(game.value) or bingoCol(game.value)
fun bingoRow(game: List<Int>) = game.chunked(numRows).map { it.sum() }.contains(numRows * marker)
fun bingoCol(game: List<Int>) = bingoRow(game.chunked(numRows).transpose().flatten())