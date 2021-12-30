
fun main() {

    val day = "19"

    fun part1(input: List<String>): Int {
        val scanners = parse(input)
        return countBeacons(scanners.map { it.toMutableSet() })
    }

    fun part2(input: List<String>): Int {
        return 0
    }

    val testInput = readInput("Day${day}_test")
    check(part1(testInput) == 79)
//    check(part2(testInput) == 0)

    val input = readInput("Day${day}")
    println(part1(input))
//    println(part2(input))
}

data class Vector3(val x: Int, val y: Int, val z: Int)

private operator fun Vector3.minus(other: Vector3) = Vector3(x - other.x, y - other.y, z - other.z)
private operator fun Vector3.plus(other: Vector3) = Vector3(x + other.x, y + other.y, z + other.z)

private fun roll(v: Vector3) = Vector3(v.x, v.z, -v.y)
private fun turn(v: Vector3) = Vector3(-v.y, v.x, v.z)

// Credit to https://stackoverflow.com/a/16467849/5095444
private fun getRotations(v: Vector3) = sequence {
    var w = v
    repeat(2) {
        repeat(3) {             // Yield RTTT 3 times
            w = roll(w)
            yield(w)                  // Yield R
            repeat(3) {         // Yield TTT
                w = turn(w)
                yield(w)
            }
        }
        w = roll(turn(roll(w)))  // Do RTR
    }
}

private fun parse(input: List<String>): List<Set<Vector3>> {
    val scanners = mutableListOf<MutableSet<Vector3>>()
    for (line in input.filter { it.isNotEmpty() }) {
       if (line.startsWith("--")) {
           scanners.add(mutableSetOf())
       } else {
           val (x, y, z) = line.split(',').map { it.toInt() }
           scanners.last().add(Vector3(x, y, z))
       }
    }
    return scanners
}

private fun findOverlaps(s1: Set<Vector3>, s2: Set<Vector3>): Map<Vector3, Set<Vector3>> {
    val distances = mutableMapOf<Vector3, MutableSet<Vector3>>()
    for (beacon in s1) {
        for (otherBeacon in s2) {
            for (rotation in getRotations(otherBeacon)) {
                distances.getOrPut(beacon - rotation) { mutableSetOf() }.add(otherBeacon)
            }
        }
    }
    return distances.filter { it.value.size >= 12 }
}

private fun countBeacons(scanners: List<MutableSet<Vector3>>): Int {
    for (i in 0 until scanners.lastIndex) {
        for (j in i+1 .. scanners.lastIndex) {
            val overlaps = findOverlaps(scanners[i], scanners[j]).entries.firstOrNull()
            overlaps?.let {
                scanners[j].removeAll(it.value)
            }
        }
    }
    return scanners.sumOf { it.count() }.also { println(it) }
}