package day2

typealias CubeLines = String
fun CubeLines.parseToGame(): Game = Game(extractId(this)!!, extractObservedCubeSets(this))

enum class CubeColors {
    Green, Blue, Red,
}

typealias CubesShown = List<Cubes>

typealias Bag = Map<CubeColors, Int>

data class Cubes(val count: Int, val type: CubeColors)

data class Game(val id: Int, val observed: Set<CubesShown>) {
    fun possibleWithBag(bag: Bag): Boolean =
        observed
            .fold(true) { acc, new -> acc && new.possibleWithBag(bag) }

    fun minimumBag(): Bag =
        observed.fold(mapOf() ) {acc, new -> acc.combineMax(new.toBag())}

}

fun Bag.power() :Int = this.entries.fold(1) {acc, entry -> acc * entry.value }

fun CubesShown.toBag(): Bag = this.associate { it.type to it.count }

fun Bag.combineMax(other: Bag) : Bag = CubeColors.entries.fold(mapOf()) { acc, new -> acc + (mapOf(new to maxOf(other[new] ?: 0, this[new] ?: 0))) }
fun CubesShown.possibleWithBag(bag: Bag): Boolean =
    this
        .fold(true) { acc, new -> acc &&  (new.count <= bag[new.type]!!) }



fun extractId(line: CubeLines) = Regex("""Game (\d+):""").find(line)?.destructured?.component1()?.toInt()


fun extractObservedCubeSets(line: CubeLines) = line.split(':')[1].split(';').map { parseObservedCubeSets(it) }.toSet()


fun parseObservedCubeSets(line: String) = line.split(",").map { it.trim() }.map { str ->
        val green = regexOnColor(str, """(\d+) green""")?.let { Cubes(it, CubeColors.Green) }
        val red = regexOnColor(str, """(\d+) red""")?.let { Cubes(it, CubeColors.Red) }
        val blue = regexOnColor(str, """(\d+) blue""")?.let { Cubes(it, CubeColors.Blue) }

        listOfNotNull(green, red, blue)
    }.flatten()

fun regexOnColor(string: String, pattern: String) = Regex(pattern).find(string)?.destructured?.component1()?.toInt()