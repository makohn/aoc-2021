import java.util.*
import kotlin.math.abs

fun main() {

    val day = "23"

    fun part1(input: List<String>): Int {
        val start = Situation(input.subList(1, 4).map { it.toCharArray() }.toTypedArray(), 2, 0)
        return solve(start, 2)
    }

    fun part2(input: List<String>): Int {
        val newInput = input.subList(1, 3).toMutableList()
        newInput += "  #D#C#B#A#  "
        newInput += "  #D#B#A#C#  "
        newInput += input.subList(3, 4)
        val start = Situation(newInput.map { it.toCharArray() }.toTypedArray(), 4,  0)
        return solve(start, 4)
    }

    val testInput = readInput("Day${day}_test")
    check(part1(testInput) == 12521)
    check(part2(testInput) == 44169)

    val input = readInput("Day${day}")
    println(part1(input))
    println(part2(input))
}

private val costs = mapOf(
    'A' to 1,
    'B' to 10,
    'C' to 100,
    'D' to 1000
)

private data class Situation(val config: Array<CharArray>, val n: Int, val distance: Int) {
    override fun equals(other: Any?): Boolean {
        return other is Situation &&
                (0..n).all { i -> config[i].contentEquals(other.config[i]) }
    }

    override fun hashCode() = (0..n).fold(0) { a, i -> a * 31 + config[i].contentHashCode() }

    fun copy(distance: Int) = Situation(config.map { it.copyOf() }.toTypedArray(), n, distance)

    override fun toString() = buildList {
        add("#############")
        addAll(config.map { it.concatToString() })
        add("  #########  ")
    }.joinToString("\n")
}

private fun solve(startSituation: Situation, n: Int): Int {
    val queue = PriorityQueue(compareBy(Situation::distance))
    val visited = mutableSetOf<Situation>()
    val distances = mutableMapOf<Situation, Int>()

    fun enqueue(situation: Situation) {
        val startDistance = distances[situation] ?: Int.MAX_VALUE
        if (situation.distance >= startDistance) return
        distances[situation] = situation.distance
        queue += situation
    }

    enqueue(startSituation)

    while (true) {
        val situation = queue.remove()!!
        if (situation in visited) continue
        visited += situation
        val startDistance = distances[situation]!!
        var correctPosition = true

        // Check for each room line r, if for each room the correct amphipod is already there
        for (pos in 1..n) {
            for (room in 0 .. 3) {
                if (situation.config[pos][2 * room + 3] != 'A' + room) {
                    correctPosition = false
                    break
                }
            }
        }
        if (correctPosition) return situation.distance

        // Check the hallway
        for (hallwayPos in 1..11) {
            val hallwayContent = situation.config[0][hallwayPos]
            // If hallway does not contain amphipod at position
            if (hallwayContent !in 'A'..'D') continue
            val amphipodInt = (hallwayContent - 'A')
            val homeRoom = 2 * amphipodInt + 3

            // If hallway between current pos and homeRoom entrance is not free
            if (!(minOf(hallwayPos, homeRoom) + 1 until maxOf(hallwayPos, homeRoom)).all {
                        x -> situation.config[0][x] == '.'  }) continue

            // If the home room for the current amphipod (also hallwayContent) is not empty or occupied by same-named amphipod
            if (!(1..n).all { situation.config[it][homeRoom] == '.' ||
                        situation.config[it][homeRoom] == hallwayContent }) continue

            // Find the first free space in the amphipod's home room
            val freeSpace = (n downTo 1).first { situation.config[it][homeRoom] == '.' }
            val newSituation = situation.copy(
                // prev situation's cost   amphipod factor         entrance - current hallway   pos in room
                startDistance + costs[hallwayContent]!! * (abs(homeRoom - hallwayPos) + freeSpace)
            )
            newSituation.config[0][hallwayPos] = '.'
            newSituation.config[freeSpace][homeRoom] = hallwayContent
            enqueue(newSituation)
        }

        // Go through all rooms (and their positions)
        for (i in 0..3) for (r in 1..n) {
            val homeRoom = 2 * i + 3
            val currentRoomPosContent = situation.config[r][homeRoom]
            // If current content is not an amphipod
            if (currentRoomPosContent !in 'A'..'D') continue
            // If the upper space in the room is not empty
            if (r == n && situation.config[1][homeRoom] != '.') continue
            if (!(1 until r).all { situation.config[it][homeRoom] == '.' }) continue
            for (hallwayPos in 1..11) {
                // If the current pos is a room entrance
                if ((hallwayPos - 3) % 2 == 0 && (hallwayPos - 3) / 2 in 0..3) continue
                // If the path between current pos and homeRoom entry is no empty
                if (!(minOf(hallwayPos, homeRoom)..maxOf(hallwayPos, homeRoom)).all {
                            cell -> situation.config[0][cell] == '.' }) continue
                val newSituation = situation.copy(
                    // prev situation's cost    amphipod's cost          distance between current hallway and home entry     pos in room
                    startDistance + costs[currentRoomPosContent]!! * (abs(hallwayPos - homeRoom) + r)
                )
                // Walk from current room pos to (current) hallway pos
                newSituation.config[r][homeRoom] = '.'
                newSituation.config[0][hallwayPos] = currentRoomPosContent
                enqueue(newSituation)
            }
        }
    }
}