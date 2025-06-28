package tech.softwareologists.qa.fileio

import java.nio.file.Path
import tech.softwareologists.qa.core.FileEvent
import tech.softwareologists.qa.core.FileIoEmulator

class NioFileIoEmulator : FileIoEmulator {
    override fun watch(paths: List<Path>) {
        // no-op
    }

    override fun stop() {
        // no-op
    }

    override fun events(): List<FileEvent> = emptyList()
}
