package day3

import sun.reflect.generics.reflectiveObjects.NotImplementedException
import java.awt.Point
import java.util.UUID

interface EngineSchematics {
    fun toMapImpl(): EngineSchematicsMapImpl

    fun toStringImpl(): EngineSchematicsStringImpl

    fun toPlainString(): String = toStringImpl().data.joinToString("\n")

    fun validPartsSmm() = toMapImpl().findAllValidPartNumbers().sum()

}

val defaultParser = EngineSchematicsParser()
data class EngineSchematicsStringImpl(val data: List<String>) : EngineSchematics {
    override fun toMapImpl(): EngineSchematicsMapImpl =
        EngineSchematicsMapImpl(data.flatMapIndexed { row, str -> str.mapIndexed() { col, c -> Point(row, col) to c } }
            .toMap())

    override fun toStringImpl(): EngineSchematicsStringImpl = this

    data class Gear constructor(val twoNumbers: Set<SchematicNumber>) {
        init {
            assert(twoNumbers.size == 2)
        }
        companion object{
            fun gearOf(nr1: Int, nr2: Int) =
                Gear(  setOf(SchematicNumber(nr1), SchematicNumber(nr2)))


            fun gearOf(part: Part, nr1: SchematicNumber, nr2: SchematicNumber) = part
                .also { assert(it.symbol == '*') }
                .let { Gear(setOf(nr1, nr2)) }

        }
    }


    fun getGears(parser: EngineSchematicsParser  = defaultParser) : Set<Gear>{
        return parser.parseToGraph(this).content.filter { it.isGear() }.map { Gear.gearOf(it.key, it.value[0], it.value[1]) }.toSet()
    }

    fun gearPowerSum(parser: EngineSchematicsParser = defaultParser) :Int? = getGears(parser).ifEmpty { null }?.map { it.ratio() }?.sum()

    fun Map.Entry<Part, List<SchematicNumber>>.isGear() = this.value.count() == 2


}


fun EngineSchematicsStringImpl.Gear.ratio() = this.twoNumbers.map { it.number }.reduce { acc, t -> acc * t }

sealed interface SchematicElement

@JvmInline
value class SchematicNumber(val number: Int) : SchematicElement {
    operator fun times(other: SchematicNumber) : Int = this.number * other.number
}
//data class PartNumber(val number: SchematicNumber, val adjacent: Part) : SchematicElement
data class Part(val symbol: Char, val id : UUID = UUID.randomUUID()) : SchematicElement
data object EmptySpace : SchematicElement

typealias EngineSchematicsLine = String

data class EngineSchematicsGraph(val content : Map<Part, List<SchematicNumber>>)


data class StringMonad<T>(val acc: List<T>, val rest: String)


object EngineSchematicsLineParser {
    fun tryParseNumberAtStartOfString(rest: String) :SchematicNumber? =
        """^(\d+)""".toRegex().find(rest)?.value?.toInt()?.let { SchematicNumber(it) }

    fun SchematicNumber.nrOfDigits() = number.toString().length

    fun tryParseNumber(rest: String): StringMonad<SchematicElement>?
        = tryParseNumberAtStartOfString(rest)?.let {
            StringMonad(listOf(it), rest.drop(it.nrOfDigits()))
        }


    fun tryParseNumberWithRepeatingPerDigit(rest: String): StringMonad<SchematicElement>?
        = tryParseNumberAtStartOfString(rest)?.let {
            StringMonad(List(it.nrOfDigits()) {_ -> it }, rest.drop(it.nrOfDigits())) }


    fun singleCharMatch(c: Char, schematicElement: SchematicElement): (String) -> StringMonad<SchematicElement>? =
        { str: String -> if (str.firstOrNull() == c) StringMonad(listOf(schematicElement), str.drop(1)) else null }

    fun anyCharMatch(match: (Char) -> SchematicElement): (String) -> StringMonad<SchematicElement>? = { str: String ->
        str.firstOrNull()?.let { c ->
            StringMonad(listOf(match(c)), str.drop(1))
        }
    }


    fun partialParse(rest: String): StringMonad<SchematicElement>? {
        if (rest.isEmpty())
            return null

        return tryParseNumberWithRepeatingPerDigit(rest)
            ?: singleCharMatch('.', EmptySpace)(rest)
            ?: anyCharMatch { c -> Part(c) }(rest)
    }


    fun parseLine(str: String): List<SchematicElement>?  =
        partialParse(str)?.let { it.acc.plus(parseLine(it.rest) ?: listOf()) }
}

class EngineSchematicsParser(private val lineParser: EngineSchematicsLineParser = EngineSchematicsLineParser) {
    fun parse (engineSchematics: EngineSchematicsStringImpl)
        = engineSchematics.data.mapNotNull { lineParser.parseLine(it) }.flatMapIndexed { rowIndex, line ->
            line.mapIndexed {colIndex, schema ->
                Point(rowIndex, colIndex) to schema
            }
        }.associate { it }


    private fun toGraph(board :  Map<Point, SchematicElement>): EngineSchematicsGraph
        = board.entries
            .asSequence()
            .map { it.key to it.value }
            .filter { it.second  is Part}
            .filterIsInstance<Pair<Point, Part>>()
            .map { println(it) ; it }
            .associate { (point, part) -> part to adjacentSchematicNumber(point, board) }
            .let { EngineSchematicsGraph(it) }


    fun parseToGraph(engineSchematics: EngineSchematicsStringImpl) =
        toGraph(parse(engineSchematics))

    private fun adjacentSchematicNumber(point: Point, board: Map<Point, SchematicElement>) =
        neighborsPoints(point)
            .mapNotNull { board[it] }
            .distinct()
            .filterIsInstance<SchematicNumber>()

    /*private fun adjacentPartNumbers(part: Pair<Point, Part>, board: Map<Point, SchematicElement>) =
        adjacentSchematicNumber(part.first, board)
            .map { PartNumber(it, part.second) }
            .map { println(it) ; it }*/


}

fun EngineSchematicsStringImpl.mapToAst(): Map<Point, SchematicElement> {
    throw NotImplementedException()
}


data class EngineSchematicsMapImpl(val data: Map<Point, Char>) : EngineSchematics {
    override fun toMapImpl(): EngineSchematicsMapImpl = this

    override fun toStringImpl(): EngineSchematicsStringImpl =
        EngineSchematicsStringImpl(data.entries.groupBy { it.key.x }.toSortedMap()
            .map { it.value.map { it.value }.fold("") { acc, c -> acc + c } })
}

fun EngineSchematics.floodFilledCopy(): EngineSchematicsMapImpl {
    val mut = toMapImpl().data.toMutableMap()

    mut.forEach { (point, c) ->
        if (!c.isDigit() && c != '.')
            floodFill(c, point, mut) { it.isDigit() }
    }
    return EngineSchematicsMapImpl(mut.toMap())
}

fun EngineSchematics.removeAllNotValid(): EngineSchematicsMapImpl {
    val mut = toMapImpl().data.toMutableMap()
    val floodFilled = this.floodFilledCopy()

    mut.forEach { (point, _) ->
        if (floodFilled.data[point]?.isDigit() == true)
            mut[point] = '.'
    }
    return EngineSchematicsMapImpl(mut.toMap())
}

fun String.extractNumbers(): List<Int> =
    """(\d+)+""".toRegex().findAll(this).flatMap { it.destructured.toList() }.map { it.toInt() }.toList()

fun EngineSchematics.findAllValidPartNumbers(): List<Int> =
    this.removeAllNotValid().toPlainString().extractNumbers()

fun neighborsPoints(thisPoint: Point): Set<Point> =
    (-1..1).flatMap { rowOffset ->
        (-1..1).map { colOffset ->
            val newPoint = Point(rowOffset + thisPoint.x, colOffset + thisPoint.y)
            if (newPoint == thisPoint)
                null
            else
                newPoint
        }
    }.filterNotNull().toSet()

fun <T> floodFill(changeTo: T, start: Point, map: MutableMap<Point, T>, predicate: (T) -> Boolean) {
    fun outOfBounds(thisPoint: Point): Boolean = map[thisPoint]?.let { false } ?: true
    fun updateSelf(thisPoint: Point) {
        val thisValue = map[thisPoint] ?: return
        if (predicate(thisValue))
            map[thisPoint] = changeTo
    }

    fun needsToBeUpdated(point: Point) = map[point]?.let { predicate(it) } ?: false

    if (outOfBounds(start))
        return
    else
        updateSelf(start)
    neighborsPoints(start)
        .filter { it != start }
        .filter { needsToBeUpdated(it) }
        .forEach { floodFill(changeTo, it, map, predicate) }
}

