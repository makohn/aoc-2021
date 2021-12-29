fun main() {

    val day = "12"

    fun part1(input: List<String>): Int {
        val connections = input
            .map { it.split("-") }
            .flatMap { listOf(it.first() to it.last(), it.last() to it.first()) }
            .groupBy { it.first }
            .mapValues { it.value.map { v -> v.second }.distinct() }
        return findPaths(connections)
    }

    fun part2(input: List<String>): Int {
        return -1
    }

    val testInput = readInput("Day${day}_test")
    val testInput2= readInput("Day${day}_test2")
    check(part1(testInput) == 19)
    check(part1(testInput2) == 226)
//    check(part2(testInput) == 0)

    val input = readInput("Day${day}")
    println(part1(input))
//    println(part2(input))
}

private fun findPaths(connections: Map<String, List<String>>): Int {

    var paths = 0

    fun nextPaths(cave: String, visited: Set<String>) {
        var v = visited
        if (cave == cave.lowercase()) v = visited.union(listOf(cave))
        for (nextCave in connections[cave]!!) {
            if (nextCave == "end") paths++
            else if (nextCave !in v) nextPaths(nextCave, v)
        }
    }
    nextPaths("start", mutableSetOf())
    return paths
}