package day3

import day3.EngineSchematicsStringImpl.Gear.Companion.gearOf
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.AssertionsForClassTypes
import org.testng.annotations.Test
import java.io.File
import java.util.UUID

class GearRatiosTest {
    val folder = File("resources/day3")

    private val sample = File(folder, "sampleInput")

    private val actual = File(folder, "input")

    val engineSchematicSample : EngineSchematicsStringImpl by lazy {EngineSchematicsStringImpl(sample.readLines())}

    @Test
    fun `Files test files exist`() {
        AssertionsForClassTypes.assertThat(folder).isDirectory()
        AssertionsForClassTypes.assertThat(sample).exists()
        AssertionsForClassTypes.assertThat(actual).exists()
    }

    @Test
    fun `Actual - sum of valid digits is sensible`() {
        val input = EngineSchematicsStringImpl(actual.readLines())
        assertThat(input.validPartsSmm()).isEqualTo(536202)
    }

    @Test
    fun`Actual - sum of power of gearparts is sensible`() {
        val input = EngineSchematicsStringImpl(actual.readLines())
        assertThat(input.gearPowerSum())
            .isEqualTo(78272573)
    }

    @Test
    fun`Sample - sum of power of gearparts is sensible`() {
        val input = EngineSchematicsStringImpl(sample.readLines())
        assertThat(input.gearPowerSum())
            .isEqualTo(467835)
    }

    private val engineSchematic = EngineSchematicsStringImpl("""56.
        ...
        12.
        ..*
    """.split("\n").map { it.trim() })


    @Test
    fun `Flood fill over all digits`() {
        val copy = engineSchematic.floodFilledCopy().toStringImpl()

        val engineFloodFilled = EngineSchematicsStringImpl("""56.
            ...
            **.
            ..*""".split("\n").map { it.trim() })
        assertThat(copy).isEqualTo(engineFloodFilled)
    }

    @Test
    fun `Sample - flood fill over all digits`() {
        val sample = sample.readLines()
        val floodFilled = EngineSchematicsStringImpl(sample).floodFilledCopy().toStringImpl()

        assertThat(floodFilled.toPlainString()).contains("114")
        assertThat(floodFilled.toPlainString()).contains("58")
        assertThat(floodFilled.toPlainString().filter { it.isDigit() }).isEqualTo("11458")
        println(floodFilled.toPlainString())
    }

    @Test
    fun `Extract numbers from string`() {
        val str = "12..34..\n..5..123"

        val extractedNumbers = str.extractNumbers()
        assertThat(extractedNumbers).contains(12)
        assertThat(extractedNumbers).contains(34)
        assertThat(extractedNumbers).contains(5)
        assertThat(extractedNumbers).contains(123)
        assertThat(extractedNumbers).size().isEqualTo(4)
    }

    @Test
    fun `Sample - sum of valid digits`() {
        val input = EngineSchematicsStringImpl(sample.readLines())
        assertThat(input.validPartsSmm()).isEqualTo(4361)
    }

    @Test
    fun `parseLine - parsing of line  `() {
        val input = EngineSchematicsLineParser.parseLine(".12.*3")
        assertThat(input?.resetIdOfParts()).containsExactlyElementsOf(
            listOf(
                EmptySpace,
                SchematicNumber(12),
                SchematicNumber(12),
                EmptySpace,
                Part('*', 4.toUUID()),
                SchematicNumber(3)
            )
        )
    }

    @Test
    fun `engineSchematic - EngineSchematics parser`() {
        val input = EngineSchematicsParser().parseToGraph(engineSchematic)
        assertThat(input.resetIdOfParts().content)
            .containsEntry(
                Part('*', 0.toUUID()), listOf(SchematicNumber(12))
            )
    }

    fun Int.toUUID() = UUID(0, this.toLong())
    fun EngineSchematicsGraph.resetIdOfParts() = this.copy(
        content = content.keys.resetIdOfParts().filterIsInstance<Part>().zip(content.values).toMap()
    )
    fun Iterable<SchematicElement>.resetIdOfParts() = this
        .mapIndexed(){ index, it -> when(it) {
            is Part -> Part(it.symbol, index.toUUID())
            else -> it
        } }

    fun Part.resetIdTo(id: Int) = Part(this.symbol, id.toUUID())


    @Test
    fun `EngineSchematics-getGears() with a gear finds it`() {
        val schematicWithGear = EngineSchematicsStringImpl("12.\n.3*".splitOnNewLineAndTrim())

        val input = schematicWithGear.getGears()
        assertThat(input)
            .contains(gearOf(12, 3))
    }

    @Test
    fun `EngineSchematics-getGears() without a gear is empty`() {
        val schematicWithoutGear = EngineSchematicsStringImpl("1..\n.3*".splitOnNewLineAndTrim())

        val input = schematicWithoutGear.getGears()
        assertThat(input).isEmpty()
    }

    @Test
    fun `EngineSchematics-getGears() with a non-match from sample is found`() {
        val schematicWithoutGear = EngineSchematicsStringImpl("67..\n.*..\n.35.".splitOnNewLineAndTrim())

        val input = schematicWithoutGear.getGears()
        assertThat(input)
            .contains(gearOf(67,35))
    }

    @Test
    fun `sample - getGears() finds all the gears and no more`() {
        val input = EngineSchematicsStringImpl(sample.readLines()).getGears()
        assertThat(input)
            .containsExactlyInAnyOrderElementsOf(setOf(
                gearOf(467, 35),
                gearOf(598, 755),
            ))
    }

    @Test
    fun `sample - getGears() find all parts`() {
        fun Char.isPart() = !(this.isDigit() || this == '.')
        val input = EngineSchematicsParser().parseToGraph(engineSchematicSample)
        assertThat(input.content.map{it.key}.map { it.symbol } )
            .containsExactlyInAnyOrderElementsOf(
                engineSchematicSample.data.flatMap { it.toList() }.filter { it.isPart()}
            )
    }

    @Test
    fun `Gear power is power`(){
        val gear = gearOf(123,3)
        assertThat(gear.ratio())
            .isEqualTo(123*3)
    }

    private fun String.splitOnNewLineAndTrim() = this.split("\n").map { it.trim() }


}