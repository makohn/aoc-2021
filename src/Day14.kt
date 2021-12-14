fun main() {

    fun part1(input: List<String>): Int {
        val (template, rules) = Day14Helper.parse(input)
        val polymer = Day14Helper.buildPolymer(template, rules)
        val occurrences = polymer.groupingBy { it }.eachCount().values
        return occurrences.maxOf { it } - occurrences.minOf { it }
    }

    fun part2(input: List<String>): Int {
        return 0
    }

    val testInput = readInput("Day14_test")
    check(part1(testInput) == 1588)
//    check(part2(testInput) == 16)

    val input = readInput("Day14")
    println(part1(input))
//    println(part2(input))
}

object Day14Helper {

    fun parse(input: List<String>): Pair<String, Map<String, String>> {
        val template = input.first()
        val rules = input.drop(2)
            .map { it.split(" -> ") }
            .associate { it.first() to it.last() }
        return template to rules
    }

    fun buildPolymer(template: String, rules: Map<String, String>, step: Int = 10): String {
        var polymer = template
        (0 until step).forEach { _ ->
            polymer = polymer.windowed(2) {
                if (rules.contains(it)) "${it[0]}${rules[it]}${it[1]}" else it
            }.foldRight("") { acc, e -> "${acc}${e.drop(1)}"}
        }
        return polymer
    }
}
