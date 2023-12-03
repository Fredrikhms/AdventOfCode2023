package day3

import java.awt.Point

interface EngineSchematics {
    fun toMapImpl(): EngineSchematicsMapImpl

    fun toStringImpl(): EngineSchematicsStringImpl

    fun toPlainString(): String = toStringImpl().data.joinToString ( "\n" )

    fun validPartsSmm() = toMapImpl().findAllValidPartNumbers().sum()

}

data class EngineSchematicsStringImpl(val data: List<String>) : EngineSchematics {
    override fun toMapImpl(): EngineSchematicsMapImpl =
        EngineSchematicsMapImpl(data.flatMapIndexed { row, str -> str.mapIndexed() { col, c -> Point(row, col) to c } }
            .toMap())

    override fun toStringImpl(): EngineSchematicsStringImpl = this

}

data class EngineSchematicsMapImpl(val data: Map<Point, Char>) : EngineSchematics {
    override fun toMapImpl(): EngineSchematicsMapImpl = this

    override fun toStringImpl(): EngineSchematicsStringImpl = EngineSchematicsStringImpl(data.entries.groupBy { it.key.x }.toSortedMap().map { it.value.map { it.value }.fold("") {acc, c -> acc+c } })
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

fun String.extractNumbers() : List<Int> = """(\d+)+""".toRegex().findAll(this).flatMap { it.destructured.toList() }.map { it.toInt() }.toList()
fun EngineSchematics.findAllValidPartNumbers(): List<Int>  =
    this.removeAllNotValid().toPlainString().extractNumbers()


fun <T> floodFill(changeTo: T, start: Point, map: MutableMap<Point, T>, predicate: (T) -> Boolean) {
    fun outOfBounds(thisPoint: Point) : Boolean = map[thisPoint]?.let{false} ?: true
    fun updateSelf(thisPoint: Point) {
        val thisValue = map[thisPoint] ?: return
        if (predicate(thisValue))
            map[thisPoint] = changeTo
    }

    fun needsToBeUpdated(point: Point) = map[point]?.let { predicate(it) } ?: false

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

    if (outOfBounds(start))
        return
    else
        updateSelf(start)
    neighborsPoints(start)
        .filter { it != start }
        .filter { needsToBeUpdated(it) }
        .forEach { floodFill(changeTo, it, map, predicate)}
}

