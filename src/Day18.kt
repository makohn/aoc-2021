fun main() {
    fun part1(input: List<String>): Int {
        val snailFishNumbers = Day18Helper.parse(input)
        return snailFishNumbers.reduce { a, b -> a + b }.magnitude()
    }

    fun part2(input: List<String>): Int {
        val snailFishNumbers = Day18Helper.parse(input)
        return snailFishNumbers
            .map { it to snailFishNumbers.filter { n -> n != it } }
            .flatMap { it.second.map { n -> it.first + n } }
            .maxOf { it.magnitude() }
    }

    // Magnitude tests
    val testMagnitudes = Day18Helper.parse(readInput("Day18_magnitudes"))
    check(testMagnitudes[0].magnitude() == 143)
    check(testMagnitudes[1].magnitude() == 1384)
    check(testMagnitudes[2].magnitude() == 445)
    check(testMagnitudes[3].magnitude() == 791)
    check(testMagnitudes[4].magnitude() == 1137)
    check(testMagnitudes[5].magnitude() == 3488)

    // First test input
    val testInput = readInput("Day18_test")
    check(part1(testInput) == 3488)

    // Second test input
    val testInput2 = readInput("Day18_test2")
    check(part1(testInput2) == 4140)
    check(part2(testInput2) == 3993)

    val input = readInput("Day18")
    println(part1(input))
    println(part2(input))
}

sealed interface SnailFishNumber {
    fun getPair(depth: Int): SnailFishPair? = null
    fun getInt(max: Int): SnailFishInt?
    fun toList(pivot: SnailFishNumber = this): List<SnailFishNumber>
    fun value(left: Boolean = true): Int
    fun apply(map: Map<SnailFishNumber, SnailFishNumber>) = map[this]
    fun magnitude(): Int
    fun reduce(): Map<SnailFishNumber, SnailFishNumber>? {
        return getPair(4)?.let {
            // explode
            val numbers = toList(it)
            val pivot = numbers.indexOf(it)
            mutableMapOf<SnailFishNumber, SnailFishNumber>(it to 0.sn()).apply {
                numbers.getOrNull(pivot - 1)?.let { l -> set(l, (it.value() + l.value(false)).sn()) }
                numbers.getOrNull(pivot + 1)?.let { r -> set(r, (it.value(false) + r.value()).sn()) }
            }
        }?: getInt(10)?.let {
            // split
            mapOf(it to SnailFishPair((it.value / 2).sn(),((it.value + 1) / 2).sn()))
        }
    }

    operator fun plus(other: SnailFishNumber) =
        generateSequence<SnailFishNumber> (SnailFishPair(this, other)) { n -> n.reduce()?.let { n.apply(it) }}.last()
}
class SnailFishInt(val value: Int): SnailFishNumber {
    override fun getInt(max: Int): SnailFishInt? = if (value >= max) this else null
    override fun toList(pivot: SnailFishNumber) = listOf(this)
    override fun value(left: Boolean) = value
    override fun magnitude() = value
    override fun apply(map: Map<SnailFishNumber, SnailFishNumber>) = super.apply(map) ?: this
}
class SnailFishPair(private val first: SnailFishNumber, private val second: SnailFishNumber): SnailFishNumber {
    override fun getPair(depth: Int): SnailFishPair? {
        if (depth == 0) return this
        first.getPair(depth - 1)?.let { return it }
        second.getPair(depth - 1)?.let { return it }
        return null
    }

    override fun getInt(max: Int): SnailFishInt? {
        first.getInt(max)?.let { return it }
        second.getInt(max)?.let { return it }
        return null
    }

    override fun apply(map: Map<SnailFishNumber, SnailFishNumber>) = super.apply(map)?: SnailFishPair(first.apply(map)!!, second.apply(map)!!)
    override fun toList(pivot: SnailFishNumber) = if (this == pivot) listOf(this) else first.toList(pivot) + second.toList(pivot)
    override fun value(left: Boolean): Int = if (left) first.value(true) else second.value(false)
    override fun magnitude() = 3 * first.magnitude() + 2 * second.magnitude()
}

fun String.split(index: Int): List<String> = listOf(substring(0, index), substring(index+1, length))
fun Int.sn() = SnailFishInt(this)

object Day18Helper {

    fun parse(input: List<String>): List<SnailFishNumber> {
        return input.map { parseSnailFishNumber(it) }
    }

    private fun parseSnailFishNumber(input: String): SnailFishNumber {
        return if (input.first().isDigit()) SnailFishInt(input.first().digitToInt())
        else {
            var count = 0
            val content = input.removeSurrounding("[", "]")
            val (s1, s2) = content.foldIndexed("") { i, acc, c -> when(c) {
                '[' -> count++.let { acc }
                ']' -> count--.let { acc }
                ',' -> if (count == 0) content.split(i).joinToString("#") else acc
                else -> acc
            }}.split("#")
            SnailFishPair(parseSnailFishNumber(s1), parseSnailFishNumber(s2))
        }
    }
}
