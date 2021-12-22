import kotlin.streams.asSequence

fun main() {

    val day = "08"

    fun part1(input: List<String>): Int {
        val outputs = input
            .map { it.split(" | ") }
            .map { it.last().split(" ") }
        return outputs.sumOf { it.count { output -> output.length in listOf(2, 3, 4, 7) } }
    }

    fun part2(input: List<String>): Int {
        val notes = input
            .map { it.split(" | ") }
            .map { it.first() to it.last() }

        var sum = 0
        for (note in notes) {
            val signals = note.first.split(" ")
                .sortedBy { it.length }
                .map { it.chars().asSequence().toMutableSet() }
            val outputs = note.second.split(" ")

            val segments = decode(signals)
            val number = outputs.fold(0) { acc, o -> acc * 10 + toNumber(o.map { c -> segments[c]!! }) }
            sum += number
        }
        return sum
    }

    val testInput = readInput("Day${day}_test")
    check(part1(testInput) == 26)
    check(part2(testInput) == 61229)

    val input = readInput("Day${day}")
    println(part1(input))
    println(part2(input))
}

private fun toNumber(segments: List<Int>): Int {
    return when (segments.sorted()) {
        listOf(0, 1, 2, 4, 5, 6) -> 0
        listOf(2, 5) -> 1
        listOf(0, 2, 3, 4, 6) -> 2
        listOf(0, 2, 3, 5, 6) -> 3
        listOf(1, 2, 3, 5) -> 4
        listOf(0, 1, 3, 5, 6) -> 5
        listOf(0, 1, 3, 4, 5, 6) -> 6
        listOf(0, 2, 5) -> 7
        listOf(0, 1, 2, 3, 4, 5, 6) -> 8
        listOf(0, 1, 2, 3, 5, 6) -> 9
        else -> -1
    }
}

private fun decode(signals: List<MutableSet<Int>>): Map<Char, Int> {

    //    0
    //  1   2
    //    3
    //  4   5
    //    6
    val segments = IntArray(7)

    // 0 -> 6 -> [0, 1, 2,    4, 5, 6]
    // 1 -> 2 -> [      2,       5]
    // 2 -> 5 -> [0,    2, 3, 4,    6]
    // 3 -> 5 -> [0,    2, 3,    5, 6]
    // 4 -> 4 -> [   1, 2, 3,    5]
    // 5 -> 5 -> [0, 1,    3,    5, 6]
    // 6 -> 6 -> [0, 1,    3, 4, 5, 6]
    // 7 -> 3 -> [0,    2,       5]
    // 8 -> 7 -> [0, 1, 2, 3, 4, 5, 6]
    // 9 -> 6 -> [0, 1, 2, 3,    5, 6]

    // signals
    // 2, 3, 4, 5, 5, 5, 6, 6, 6, 7 (length)
    // 0  1  2  3  4  5  6  7  8  9 (index)

    // Segment 0
    segments[0] = (signals[1] - signals[0]).first()
    signals.forEach { it.remove(segments[0]) }

    // Segment 6
    val sixSegmentNumbers = signals.subList(6, 9).toMutableList()
    val nine = sixSegmentNumbers.first { it.containsAll(signals[2]) }
    segments[6] = (nine - signals[2]).first()

    // Segment 4
    val i = signals.indexOf(nine)
    signals.forEach { it.remove(segments[6]) }
    segments[4] = (signals[9] - signals[i]).first()

    // Segment 3
    signals.forEach { it.remove(segments[4]) }
    segments[3] = signals.subList(3, 6).reduce { a, b -> a.intersect(b).toMutableSet() }.first()

    // Segment 1
    signals.forEach { it.remove(segments[3]) }
    segments[1] = (signals[i] - signals[0]).first()

    // Segment 2
    signals.forEach { it.remove(segments[1]) }
    val six = signals.subList(6, 9).minByOrNull { it.size }!!
    segments[2] = (signals[0] - six).first()

    // Segment 5
    signals.forEach { it.remove(segments[2]) }
    segments[5] = signals[0].first()

    return segments.mapIndexed { j, c -> c.toChar() to j}.toMap()
}