package tech.softwareologists.qa.fileio

import java.nio.file.FileSystems
import java.nio.file.Path
import java.nio.file.StandardWatchEventKinds.ENTRY_CREATE
import java.nio.file.StandardWatchEventKinds.ENTRY_DELETE
import java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY
import java.nio.file.StandardWatchEventKinds.OVERFLOW
import java.nio.file.WatchKey
import java.nio.file.WatchService
import java.time.Instant
import java.util.concurrent.Executors
import java.util.concurrent.Future
import tech.softwareologists.qa.core.FileEvent
import tech.softwareologists.qa.core.FileEventType
import tech.softwareologists.qa.core.FileIoEmulator

/**
 * [FileIoEmulator] implementation backed by Java NIO's [WatchService].
 */
class NioFileIoEmulator : FileIoEmulator {
    private val recorded = mutableListOf<FileEvent>()
    private val executor = Executors.newSingleThreadExecutor()

    private var watchService: WatchService? = null
    private var future: Future<*>? = null

    override fun watch(paths: List<Path>) {
        watchService = FileSystems.getDefault().newWatchService()
        val service = watchService ?: return

        for (path in paths) {
            path.register(service, ENTRY_CREATE, ENTRY_MODIFY, ENTRY_DELETE)
        }

        future = executor.submit {
            try {
                while (!Thread.currentThread().isInterrupted) {
                    val key: WatchKey = service.take()
                    val parent = key.watchable() as Path
                    for (event in key.pollEvents()) {
                        if (event.kind() == OVERFLOW) continue
                        val child = parent.resolve(event.context() as Path)
                        val type = when (event.kind()) {
                            ENTRY_CREATE -> FileEventType.CREATE
                            ENTRY_MODIFY -> FileEventType.MODIFY
                            ENTRY_DELETE -> FileEventType.DELETE
                            else -> null
                        }
                        if (type != null) {
                            recorded += FileEvent(type, child, Instant.now())
                        }
                    }
                    if (!key.reset()) break
                }
            } catch (_: InterruptedException) {
                // allow thread exit
            }
        }
    }

    override fun stop() {
        future?.cancel(true)
        watchService?.close()
        executor.shutdownNow()
    }

    override fun events(): List<FileEvent> = recorded.toList()
}
