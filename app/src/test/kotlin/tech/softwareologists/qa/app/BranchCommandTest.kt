import kotlin.io.path.createTempDirectory
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import tech.softwareologists.qa.core.*
import tech.softwareologists.qa.app.BranchCreateCommand

class BranchCommandTest {
    @Test
    fun branch_command_creates_variant_file() {
        val dir = createTempDirectory()
        val base = dir.resolve("flow.yaml")
        val steps = listOf(
            FlowStep("1", "first"),
            FlowStep("2", "second"),
            FlowStep("3", "third")
        )
        val flow = Flow(
            version = "1",
            appVersion = "test",
            emulator = EmulatorData(HttpData(), FileData()),
            steps = steps
        )
        FlowIO.write(flow, base)

        BranchCreateCommand().parse(
            arrayOf("--base", base.toString(), "--at", "2", "--name", "variant")
        )

        val variantFile = dir.resolve("variant.yaml")
        println("created: ${variantFile}")
        println("exists? ${java.nio.file.Files.exists(variantFile)}")
        assertTrue(java.nio.file.Files.exists(variantFile))
        val branched = FlowIO.read(variantFile)
        assertEquals(steps.subList(0, 2), branched.steps)

        dir.toFile().deleteRecursively()
    }
}
