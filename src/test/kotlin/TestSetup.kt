import org.assertj.core.api.AssertionsForClassTypes
import org.testng.annotations.Test
import java.io.File

class TestSetup {

    @Test
    fun `test runs`(){
        assert(true)
    }


    private val abcFile = File("resources/abcFile")

    @Test
    fun `Files test files exist`() {
        AssertionsForClassTypes.assertThat(abcFile).exists()
    }
    @Test
    fun `Load abcFile`() {
        assert(abcFile.exists())
        AssertionsForClassTypes.assertThat(abcFile.readText().trim())
            .isEqualTo("ABC".trim())

    }

}
