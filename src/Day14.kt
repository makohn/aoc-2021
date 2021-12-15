fun main() {

    fun part1(input: List<String>): Long {
        val (template, rules) = Day14Helper.parse(input)
        val occurrences = Day14Helper.simulatePolymer(template, rules, steps = 10).values
        return 0L + occurrences.maxOf { it } - occurrences.minOf { it }
    }

    fun part2(input: List<String>): Long {
        val (template, rules) = Day14Helper.parse(input)
        val occurrences = Day14Helper.simulatePolymer(template, rules).values
        return 0L + occurrences.maxOf { it } - occurrences.minOf { it }
    }

    val testInput = readInput("Day14_test")
    check(part1(testInput) == 1588L)
    check(part2(testInput) == 2188189693529)

    val input = readInput("Day14")
    println(part1(input))
    println(part2(input))
}

object Day14Helper {

    fun parse(input: List<String>): Pair<String, Map<String, String>> {
        val template = input.first()
        val rules = input.drop(2)
            .map { it.split(" -> ") }
            .associate { it.first() to it.last() }
        return template to rules
    }

    fun buildPolymer(template: String, rules: Map<String, String>, steps: Int = 10): String {
        var polymer = template
        repeat(steps) {
            polymer = polymer.windowed(2) {
                if (rules.contains(it)) "${it[0]}${rules[it]}${it[1]}" else it
            }.also { println(it) }.foldRight("") { acc, e -> "${acc}${e.drop(1)}"}
        }
        return polymer
    }

    fun simulatePolymer(template: String, rules: Map<String, String>, steps: Int = 40): Map<String, Long> {
        val pairRules = rules.map { it.key to (it.key[0]+it.value to it.value+it.key[1]) }.toMap()
        var polymer = template.windowed(2).groupingBy { it }.fold(0L) { acc, _ -> acc + 1 }
        repeat(steps) {
            val newPolymer = mutableMapOf<String, Long>()
            polymer.entries.forEach { entry ->
                pairRules[entry.key]?.toList()?.forEach { rule ->
                   newPolymer[rule] = newPolymer.getOrDefault(rule, 0) + entry.value
                }
            }
            polymer = newPolymer
        }
        return polymer.flatMap { listOf(it.key[0].toString() to it.value, it.key[1].toString() to it.value) }
            .groupingBy { it.first }
            .fold(0L) { acc, e -> acc + (e.second/2.0).toLong() }
    }
}
