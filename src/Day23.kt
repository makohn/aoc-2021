import java.util.*
import kotlin.collections.HashMap
import kotlin.collections.HashSet
import kotlin.math.abs

fun main() {

    val day = "23"

    fun part1(input: List<String>): Int {
        val distances = HashMap<Situation, Int>()
        val queue = PriorityQueue(compareBy(Situation::distance))
        val visited = HashSet<Situation>()

        fun enqueue(situation: Situation) {
            val startDistance = distances[situation] ?: Int.MAX_VALUE
            if (situation.distance >= startDistance) return
            distances[situation] = situation.distance
            queue += situation
        }

        val input1 = input.subList(1, 4)
        val start = Situation(input1.map { it.toCharArray() }.toTypedArray(), 0)

        println(start)
        enqueue(start)

        fun cost(amphipod: Char) = when(amphipod) {
            'A' -> 1
            'B' -> 10
            'C' -> 100
            'D' -> 1000
            else -> error("$amphipod")
        }

        while (true) {
            val situation = queue.remove()!!
            if (situation in visited) continue
            visited += situation
            val startDistance = distances[situation]!!
            if (visited.size % 10000 == 0) println("d=$startDistance qs=${queue.size}, fs=${visited.size}")
            var ok = true

            // Check for each room line r, if for each room the correct amphipod is already there
            check@for (r in 1..2) for (i in 0 .. 3) if (situation.config[r][2 * i + 3] != 'A' + i) {
                ok = false
                break@check
            }
            if (ok) {
                println(situation)
                return situation.distance
            }

            // Check the hallway
            for (hallwayPos in 1..11) {
                val hallwayContent = situation.config[0][hallwayPos]
                if (hallwayContent !in 'A'..'D') continue // If hallway does not contain amphipod at position
                val amphipodInt = (hallwayContent - 'A')
                val homeRoom = 2 * amphipodInt + 3

                // If hallway between current pos and homeRoom entrance is not free
                if (!(minOf(hallwayPos, homeRoom) + 1 until maxOf(hallwayPos, homeRoom)).all {
                    x -> situation.config[0][x] == '.'  }) continue

                // If the home room for the current amphipod (also hallwayContent) is not empty or occupied by same-named amphipod
                if (!(1..2).all { r -> situation.config[r][homeRoom] == '.' ||
                    situation.config[r][homeRoom] == hallwayContent }) continue

                // Find the first free space in the amphipod's home room
                val freeSpace = (2 downTo 1).first { space -> situation.config[space][homeRoom] == '.' }
                val newSituation = situation.copy(
                    // prev situation's cost   amphipod factor         entrance - current hallway   pos in room
                    startDistance + cost(hallwayContent) * (abs(homeRoom - hallwayPos) + freeSpace)
                )
                newSituation.config[0][hallwayPos] = '.'
                newSituation.config[freeSpace][homeRoom] = hallwayContent
                enqueue(newSituation)
            }

            // Go through all rooms (and their positions)
            for (i in 0..3) for (r in 1..2) {
                val homeRoom = 2 * i + 3
                val currentRoomPosContent = situation.config[r][homeRoom]
                // If current content is not an amphipod
                if (currentRoomPosContent !in 'A'..'D') continue
                // If the upper space in the room is not empty
                if (r == 2 && situation.config[1][homeRoom] != '.') continue
                for (hallwayPos in 1..11) {
                    // If the current pos is a room entrance
                    if ((hallwayPos - 3) % 2 == 0 && (hallwayPos - 3) / 2 in 0..3) continue
                    // If the path between current pos and homeRoom entry is no empty
                    if (!(minOf(hallwayPos, homeRoom)..maxOf(hallwayPos, homeRoom)).all {
                        cell -> situation.config[0][cell] == '.' }) continue
                    val newSituation = situation.copy(
                            // prev situation's cost    amphipod's cost          distance between current hallway and home entry     pos in room
                        startDistance + cost(currentRoomPosContent) * (abs(hallwayPos - homeRoom) + r)
                    )
                    // Walk from current room pos to (current) hallway pos
                    newSituation.config[r][homeRoom] = '.'
                    newSituation.config[0][hallwayPos] = currentRoomPosContent
                    enqueue(newSituation)
                }
            }
        }
    }

    fun part2(input: List<String>): Int {
        return -1
    }

    val testInput = readInput("Day${day}_test")
    check(part1(testInput) == 12521)
//    check(part2(testInput) == 0)

    val input = readInput("Day${day}")
    println(part1(input))
//    println(part2(input))
}

private data class Situation(val config: Array<CharArray>, val distance: Int) {
    override fun equals(other: Any?): Boolean {
        return other is Situation &&
                (0..2).all { i -> config[i].contentEquals(other.config[i]) }
    }

    override fun hashCode() = (0..2).fold(0) { a, i -> a * 31 + config[i].contentHashCode() }

    fun copy(distance: Int) = Situation(config.map { it.copyOf() }.toTypedArray(), distance)

    override fun toString() = buildList {
        add("#".repeat(13))
        addAll(config.map { it.concatToString() })
        add("distance=$distance")
    }.joinToString("\n")
}