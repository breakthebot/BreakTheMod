package net.chariskar.breakthemod

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import net.chariskar.breakthemod.client.utils.Config

object TestScope {
    val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    fun shutdown() {
        scope.cancel()
    }
}

object Main {
    val config = Config.ConfigData()

}