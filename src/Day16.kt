fun main() {

    fun part1(input: String): Int {
        val bits = input
            .map { it.digitToInt(16)
                .toString(2)
                .padStart(4, '0')
            }
            .flatMap { it.chunked(1) }
            .toMutableList()
        return Day16Helper.parsePacket(bits).sumOf { it.first }
    }

    fun part2(input: String): Int {
        return 0
    }

    val testInput = readInput("Day16_test")
    check(part1(testInput[0]) == 16)
    check(part1(testInput[1]) == 12)
    check(part1(testInput[2]) == 23)
    check(part1(testInput[3]) == 31)
//    check(part2(testInput) == 2)

    val input = readInput("Day16")
    println(part1(input.first()))
//    println(part2(input))
}

typealias BitStream = MutableList<String>

fun BitStream.slice(n: Int): BitStream {
    val newBits = mutableListOf<String>()
    repeat(n) {
        newBits.add(removeFirst())
    }
    return newBits
}

object Day16Helper {

    private fun readNumber(n: Int, bits: BitStream, s: Int = 0): Int {
        var b = "0"
        val m = minOf(bits.size, n)
        repeat(m) { b += bits.removeFirst() }
        return (s shl m) or b.toInt(2)
    }

    private tailrec fun readLiteral(bits: BitStream, s: Int = 0): Int = when (readNumber(1, bits)) {
        1 -> readLiteral(bits, readNumber(4, bits, s))
        else -> readNumber(4, bits, s)
    }

    fun parsePacket(bits: BitStream): Sequence<Triple<Int, Int, Int>> = sequence {
        while (true) {
            val version = readNumber(3, bits)
            when (val type = readNumber(3, bits)) {
                4 -> yield(Triple(version, 4, readLiteral(bits)))
                type -> when (readNumber(1, bits)) {
                    0 -> {
                        val spl = readNumber(15, bits)
                        if (spl > 0) {
                            yield(Triple(version, type, 0))
                            yieldAll(parsePacket(bits.slice(spl)))
                        } else break
                    }
                    1 -> {
                        val spc = readNumber(11, bits)
                        if (spc > 0) {
                            yield(Triple(version, type, 0))
                            repeat(spc) {
                                yieldAll(parsePacket(bits))
                            }
                        } else break
                    }
                }
            }
        }
    }
}
