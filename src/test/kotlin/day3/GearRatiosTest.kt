package day3

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.AssertionsForClassTypes
import org.testng.annotations.Test
import java.io.File

class GearRatiosTest {
    val folder = File("resources/day3")

    private val sample = File(folder, "sampleInput")

    private val actual = File(folder, "input")

    @Test
    fun `Files test files exist`() {
        AssertionsForClassTypes.assertThat(folder).isDirectory()
        AssertionsForClassTypes.assertThat(sample).exists()
        AssertionsForClassTypes.assertThat(actual).exists()
    }

    private val engineSchematic = EngineSchematicsStringImpl("""56.
        ...
        12.
        ..*
    """.split("\n").map { it.trim() })

    @Test
    fun `Flood fill over all digits`(){
        val copy = engineSchematic.floodFilledCopy().toStringImpl()

        val engineFloodFilled = EngineSchematicsStringImpl("""56.
            ...
            **.
            ..*""".split("\n").map { it.trim() })
        assertThat(copy).isEqualTo(engineFloodFilled)
    }

    @Test
    fun `Sample - flood fill over all digits`(){
        val sample = sample.readLines()
        val floodFilled = EngineSchematicsStringImpl(sample).floodFilledCopy().toStringImpl()

        assertThat(floodFilled.toPlainString()).contains("114")
        assertThat(floodFilled.toPlainString()).contains("58")
        assertThat(floodFilled.toPlainString().filter { it.isDigit() }).isEqualTo("11458")
        println(floodFilled.toPlainString())
    }

    @Test
    fun `Extract numbers from string`(){
        val str = "12..34..\n..5..123"

        val extractedNumbers = str.extractNumbers()
        assertThat(extractedNumbers).contains(12)
        assertThat(extractedNumbers).contains(34)
        assertThat(extractedNumbers).contains(5)
        assertThat(extractedNumbers).contains(123)
        assertThat(extractedNumbers).size().isEqualTo(4)
    }

    @Test
    fun `Sample - sum of valid digits`(){
        val input = EngineSchematicsStringImpl(sample.readLines())
        assertThat(input.validPartsSmm())
            .isEqualTo(4361)
    }

    @Test
    fun `Actual - sum of valid digits is sensible`(){
        val input = EngineSchematicsStringImpl(actual.readLines())
        assertThat(input.validPartsSmm())
            .isEqualTo(536202)
    }
}