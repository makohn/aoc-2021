fun main() {

    fun part1(input: String): Long {
        val bits = parse(input)
        return parsePacket(bits).map{ it.version() }.sum()
    }

    fun part2(input: String): Long {
        val bits = parse(input)
        return parsePacket(bits).map { it.value() }.first()
    }

    val testInput = readInput("Day16_test")
    check(part1(testInput[0]) == 16L)
    check(part1(testInput[1]) == 12L)
    check(part1(testInput[2]) == 23L)
    check(part1(testInput[3]) == 31L)
    val testInput2 = readInput("Day16_test2")
    check(part2(testInput2[0]) == 3L)
    check(part2(testInput2[1]) == 54L)
    check(part2(testInput2[2]) == 7L)
    check(part2(testInput2[3]) == 9L)
    check(part2(testInput2[4]) == 1L)
    check(part2(testInput2[5]) == 0L)
    check(part2(testInput2[6]) == 0L)
    check(part2(testInput2[7]) == 1L)

    val input = readInput("Day16")
    println(part1(input.first()))
    println(part2(input.first()))
}

typealias BitStream = MutableList<String>

abstract class Packet<T>(val version: Long, val type: Long, val value: T) {
    abstract fun version(): Long
    abstract fun value(): Long
}
class LiteralPacket(version: Long, type: Long, value: Long): Packet<Long>(version, type, value) {
    override fun version(): Long = version
    override fun value(): Long = value
}
class OperatorPacket(version: Long, type: Long, value: Sequence<Packet<*>>): Packet<Sequence<Packet<*>>>(version, type, value) {
    override fun version(): Long = version + value.map { it.version() }.sum()
    override fun value(): Long = when(type) {
        0L -> value.sumOf { it.value() }
        1L -> value.map { it.value() }.reduce { a, b -> a * b }
        2L -> value.minOf { it.value() }
        3L -> value.maxOf { it.value() }
        5L -> value.map { it.value() }.reduce { a, b -> if (a > b) 1 else 0 }
        6L -> value.map { it.value() }.reduce { a, b -> if (a < b) 1 else 0 }
        7L -> if (value.groupBy { it.value() }.size == 1) 1 else 0
        else -> error("Illegal type: $type")
    }
}

fun BitStream.slice(n: Int): BitStream {
    val newBits = mutableListOf<String>()
    repeat(n) {
        newBits.add(removeFirst())
    }
    return newBits
}

infix fun <R> Int.times(transform: () -> Sequence<R>): Sequence<R> = (0 until  this).flatMap { transform.invoke() }.asSequence()

private fun parse(input: String) = input
    .map {
        it.digitToInt(16)
            .toString(2)
            .padStart(4, '0')
    }
    .flatMap { it.chunked(1) }
    .toMutableList()

private fun readNumber(n: Int, bits: BitStream, s: Long = 0L): Long {
    var b = "0"
    val m = minOf(bits.size, n)
    repeat(m) { b += bits.removeFirst() }
    return (s shl m) or b.toLong(2)
}

private tailrec fun readLiteral(bits: BitStream, s: Long = 0L): Long = when (readNumber(1, bits)) {
    1L -> readLiteral(bits, readNumber(4, bits, s))
    else -> readNumber(4, bits, s)
}

fun parsePacket(bits: BitStream): Sequence<Packet<*>> = sequence {
    while (true) {
        val version = readNumber(3, bits)
        when (val type = readNumber(3, bits)) {
            4L -> yield(LiteralPacket(version, 4, readLiteral(bits)))
            type -> when (readNumber(1, bits)) {
                0L -> {
                    val spl = readNumber(15, bits).toInt()
                    if (spl > 0) {
                        yield(OperatorPacket(version, type, parsePacket(bits.slice(spl))))
                    } else break
                }
                1L -> {
                    val spc = readNumber(11, bits).toInt()
                    if (spc > 0) {
                        yield(OperatorPacket(version, type, parsePacket(bits).take(spc)))
                    } else break
                }
            }
        }
    }
}
