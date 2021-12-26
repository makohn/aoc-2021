fun main() {

    val day = "25"

    fun part1(input: List<String>): Int {
        var mat = input.map { it.toCharArray() }.toTypedArray()
        val newMat = mat.map { it.copyOf() }.toTypedArray()
        var step = 0

        do {
            step++
            mat = newMat.map { it.copyOf() }.toTypedArray()
            for (i in mat.indices) {
                for (j in mat[0].indices) {
                    val k = if (j < mat[0].lastIndex) j+1 else 0
                    if (mat[i][j] == '>' && newMat[i][k] == '.' && mat[i][k] != '>') {
                        newMat[i][j] = '.'
                        newMat[i][k] = '>'
                    }
                }
            }
            for (i in mat.indices) {
                for (j in mat[0].indices) {
                    val k = if (i < mat.lastIndex) i+1 else 0
                    if (mat[i][j] == 'v' && newMat[k][j] == '.' && mat[k][j] != 'v') {
                        newMat[i][j] = '.'
                        newMat[k][j] = 'v'
                    }
                }
            }
        } while (!(mat.contentDeepEquals(newMat)))
        newMat.forEach { println(it.joinToString("") ) }
        println(step)
        return step
    }

    fun part2(input: List<String>): Int {
        TODO()
    }

    val testInput = readInput("Day${day}_test")
    check(part1(testInput) == 58)
//    check(part2(testInput) == 0)

    val input = readInput("Day${day}")
    println(part1(input))
//    println(part2(input))
}