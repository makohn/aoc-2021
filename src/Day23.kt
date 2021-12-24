import java.util.*
import kotlin.math.abs

fun main() {

    val day = "23"

    fun part1(input: List<String>): Int {
        val hallway = input[1].removeSurrounding("#").map { it }
        val amphipods = input.subList(2, 4).map { it.toCharArray().filter { c -> c != '#' && c != ' ' }}
        val rooms = amphipods[0].zip(amphipods[1]).map { listOf(it.first, it.second) }

        val startSituation = Situation(hallway, rooms)
        println(startSituation)
        println(rooms)

        val visited = mutableSetOf<Situation>()
        val queue = PriorityQueue<Situation>().apply { add(startSituation) }

        var count = 10
        while (true) {
            count--
            val situation = queue.remove()
            if (situation.final()) {
                println("Win: ${situation.cost}")
                break
            }
            println("Total dist: ${situation.cost}")
            if (situation in visited) continue
            visited.add(situation)
            transform(situation).forEach { queue.add(it) }
        }
        return -1
    }

    fun part2(input: List<String>): Int {
        return -1
    }

    val testInput = readInput("Day${day}_test")
    check(part1(testInput) == 0)
    check(part2(testInput) == 0)

    val input = readInput("Day${day}")
    println(part1(input))
    println(part2(input))
}

val costs = mapOf(
    'A' to 1,
    'B' to 10,
    'C' to 100,
    'D' to 1000
)

private fun Char.correctlyOccupied(pos: Int) = this@correctlyOccupied == "ABCD"[pos]
private fun Char.isFree() = this@isFree == '.'

data class Situation(val hallway: List<Char>, val rooms: List<List<Char>>, val cost: Int = 0)
    :Comparable<Situation>{

    override fun toString() = """
        #############
        #${hallway.joinToString("")}#
        ###${rooms[0][0]}#${rooms[1][0]}#${rooms[2][0]}#${rooms[3][0]}###
          #${rooms[0][1]}#${rooms[1][1]}#${rooms[2][1]}#${rooms[3][1]}#
          #########""".trimMargin()

    fun final() = rooms.foldIndexed(true) { i, a, b -> a && b.all { it.correctlyOccupied(i) } }

    fun isPathFree(from: Int, to: Int) = hallway
        .subList(minOf(from, to), maxOf(from, to) + 1)
        .all { it.isFree() }

    fun freeHallway() = hallway.indices.filter { i -> i !in listOf(2, 4, 6, 8) }

    override fun compareTo(other: Situation) = this.cost.compareTo(other.cost)
}

private fun Situation.insertInHallway(pos: Int, char: Char): List<Char> {
    val newHallway = mutableListOf<Char>().apply { addAll(this@insertInHallway.hallway) }
    newHallway[pos] = char
    return newHallway
}

private fun Situation.insertInRooms(room: Int, pos: Int, char: Char): List<List<Char>> {
    val newRooms = mutableListOf<MutableList<Char>>().apply {
        addAll(this@insertInRooms.rooms.map { it.toMutableList() }) }
    newRooms[room][pos] = char
    return newRooms
}

private fun transform(situation: Situation): List<Situation> {
    val newSituations = mutableListOf<Situation>()

    val frontRow = situation.rooms
        .mapIndexed { i, room ->  i to room.indexOfFirst { it != '.' } }
        .filter { it.second >= 0 }
        .map { (it.first to it.second) to 1 + it.second }

    for (front in frontRow) {
        val (position, distance) = front
        val (room, place) = position
        val entrance = 2 + 2 * room
        val amphipod = situation.rooms[room][place]
        for (space in situation.freeHallway()) {
            if (situation.isPathFree(space, entrance)) {
                val newHallway = situation.insertInHallway(space, amphipod)
                val newRooms = situation.insertInRooms(room, place,'.')
                val cost = situation.cost + (distance + abs(space - entrance)) * costs[amphipod]!!
                val newSituation = Situation(newHallway, newRooms, cost)
                println("Transform")
                println(newSituation)
                println(cost)
                val improvedSituations = moveToPlace(newSituation)
                if (improvedSituations.isEmpty()) newSituations.add(newSituation)
                else newSituations.addAll(improvedSituations)
            }
        }
    }
    return newSituations
}

private fun moveToPlace(situation: Situation): List<Situation> {

    val newSituations = mutableListOf<Situation>()

    // Get all amphipods in hallway
    for ((pos, amphipod) in situation.hallway.withIndex().filterNot { it.value.isFree() }) {
        // Get home room for current amphipod and its entrance index
        val home = "ABCD".indexOf(amphipod)
        val entrance = 2 + 2*home
        var homeSpace = -1
        for ((homePos, content) in situation.rooms[home].reversed().withIndex()) {
            if (content.isFree()) {
                homeSpace = 1 - homePos
                break
            }
            if (content != amphipod) break
        }
        val distance = 2 - homeSpace

        // If the path to the entrance is free and there is a free space at home
        if (situation.isPathFree(pos, entrance) && homeSpace != -1) {
            val cost = situation.cost + (abs(pos - entrance) + distance) * costs[amphipod]!!
            val newHallway = situation.insertInHallway(pos, '.')
            val newRooms = situation.insertInRooms(home, homeSpace, amphipod)
            val newSituation = Situation(newHallway, newRooms, cost)
            println("Move to place")
            println(newSituation)
            newSituations.add(newSituation)
        }
    }
    return newSituations
}