import Day06.count
import Day06.iterate
import Day06.parse

fun main() {

    val day = "06"

    fun part1(input: List<String>, n: Int): Int {
        var fish = parse(input)
        repeat(n) { fish = iterate(fish) }
        return fish.count()
    }

    fun part2(input: List<String>): Long {
        val fish = parse(input)
        val counts = MutableList(9) { 0L }
        fish.forEach { counts[it]++ }
        return count(counts, 256)
    }

    val testInput = readInput("Day${day}_test")
    check(part1(testInput, 18) == 26)
    check(part2(testInput) == 26984457539L)

    val input = readInput("Day${day}")
    println(part1(input, 80))
    println(part2(input))
}

object Day06 {

    fun parse(input: List<String>): MutableList<Int> {
        return input.first().split(',').map { it.toInt() }.toMutableList()
    }

    fun iterate(fish: MutableList<Int>): MutableList<Int> {
        var newFish = fish
            .map { it - 1 }
            .toMutableList()
        repeat(newFish.count { it == -1 }) { newFish.add(8) }
        newFish = newFish.map { if (it == -1) 6 else it }.toMutableList()
        return newFish
    }

    fun count(counts: MutableList<Long>, n: Int): Long {
        repeat(n) {
            val nextGen = counts.removeFirst()
            counts[6] += nextGen
            counts.add(nextGen)
        }
        return counts.fold(0L) { acc, e -> acc + e}
    }
}