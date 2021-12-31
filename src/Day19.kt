
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

private fun findOverlaps(s1: Set<Vector3>, s2: Set<Vector3>): Pair<Int, Vector3>? {
    val distances = mutableMapOf<Vector3, MutableSet<Vector3>>()
    val rotations = s2.map { getRotations(it).toList() }
    var r = 11
    for (beacon in s1) {
            for (rotation in rotations) {
                for ((i, otherBeacon) in rotation.withIndex()) {
                val distance = beacon - otherBeacon
                distances.getOrPut(distance) { mutableSetOf() }.add(otherBeacon)
                if (distances[distance]!!.size >= 12) r = i
            }
        }
    }
    val overlaps = distances.filter { it.value.size >= 12 }
    overlaps.entries.firstOrNull()?.let {
        return r to it.key
    }
    return null
}

private fun countBeacons(scanners: List<MutableSet<Vector3>>): Int {
    val translations = mutableMapOf<Pair<Int, Int>, Pair<Int, Vector3>>()
    for (i in 0 .. scanners.lastIndex) {
        for (j in 0 .. scanners.lastIndex) {
            if (i == j || translations.keys.any { it.second == j }) continue
            val res = findOverlaps(scanners[i], scanners[j])
            if (res != null) {
                translations[i to j] = res.first to res.second
            }
        }
    }
    var count = scanners.flatten().distinct().count()
    do {
        val oldCount = count
        for (i in scanners.indices) {
            val translation = translations.entries.firstOrNull { it.key.second == i }?: continue
            val (j, k) = translation.key
            val (r, o) = translation.value
            val rotations = scanners[i].map { getRotations(it).toList() }
            val rotated = rotations.map { it[r] }
            val translated = rotated.map { it + o }
            scanners[k].clear()
            scanners[j].addAll(translated)
        }
        count = scanners.flatten().distinct().count()
    } while (oldCount != count)
    return scanners.flatten().distinct().count()
}