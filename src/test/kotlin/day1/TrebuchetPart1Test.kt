package day1

import org.assertj.core.api.AssertionsForClassTypes.assertThat
import org.testng.annotations.Test
import java.io.File

class TrebuchetPart1Test {

    val folder = File("resources/day1")

    private val sample = File(folder, "sampleInput")

    private val actual = File(folder, "input")

    @Test
    fun `Files test files exist`() {
        assertThat(folder).isDirectory()
        assertThat(sample).exists()
        assertThat(actual).exists()
    }

    @Test
    fun `Load from sampleInput works`() {
        assert(sample.exists())
        assertThat(sample.readText())
            .contains("1abc2")
    }

    @Test
    fun `artistic amended line '1abc2' is  12`() {
        val line : ArtisticLine = "1abc2"
        assertThat(line.parseArtisticCoordinates()).isEqualTo(12)
    }

    @Test
    fun `rest of artistic amended lines are correct `() {
        assertThat("pqr3stu8vwx".parseArtisticCoordinates()).isEqualTo(38)
        assertThat("a1b2c3d4e5f".parseArtisticCoordinates()).isEqualTo(15)
        assertThat("treb7uchet".parseArtisticCoordinates()).isEqualTo(77)
    }

    @Test
    fun `Amended calibration document is parsed correctly`() {
        val amendedCalibrationDoc : CalibrationDoc = """1abc2
pqr3stu8vwx
a1b2c3d4e5f
treb7uchet""".split("\n")
        assertThat(amendedCalibrationDoc.sumOfCalibrationValues(spelledOut = false))
            .isEqualTo(142)
    }

    @Test
    fun `Amended calibration doc as file is parsed correctly`() {
        val amendedCalibrationDoc : CalibrationDoc = sample.readLines()
        assertThat(amendedCalibrationDoc.sumOfCalibrationValues(spelledOut = false))
            .isEqualTo(142)
    }

    @Test
    fun `Amended calibration solution`() {
        val amendedCalibrationDoc : CalibrationDoc = actual.readLines()
        assertThat(amendedCalibrationDoc.sumOfCalibrationValues(spelledOut = false))
            .isEqualTo(55816)
    }







}