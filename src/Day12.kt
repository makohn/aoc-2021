fun main() {

    val day = "12"

    fun part1(input: List<String>): Int {
        val connections = parse(input)
        return findPaths(connections, false)
    }

    fun part2(input: List<String>): Int {
        val connections = parse(input)
        return findPaths(connections, true)
    }

    val testInput = readInput("Day${day}_test")
    val testInput2= readInput("Day${day}_test2")
    check(part1(testInput) == 19)
    check(part1(testInput2) == 226)
    check(part2(testInput) == 103)
    check(part2(testInput2) == 3509)

    val input = readInput("Day${day}")
    println(part1(input))
    println(part2(input))
}

private fun parse(input: List<String>): Map<String, List<String>> {
    return input
        .map { it.split("-") }
        .flatMap { listOf(it.first() to it.last(), it.last() to it.first()) }
        .groupBy { it.first }
        .mapValues { it.value.map { v -> v.second }.distinct() }
}

private fun findPaths(connections: Map<String, List<String>>, twice: Boolean): Int {

    var paths = 0

    fun nextPaths(cave: String, visited: Set<String>, twice: Boolean) {
        var v = visited
        if (cave == cave.lowercase()) v = visited.union(listOf(cave))
        for (nextCave in connections[cave]!!) {
            if (nextCave == "end") paths++
            else if (nextCave !in v) nextPaths(nextCave, v, twice)
            else if (nextCave != "start" && twice) nextPaths(nextCave, v, false)
        }
    }
    nextPaths("start", mutableSetOf(), twice)
    return paths
}