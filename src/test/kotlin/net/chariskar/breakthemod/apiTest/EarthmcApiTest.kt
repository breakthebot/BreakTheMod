package net.chariskar.breakthemod.apiTest;

import kotlinx.coroutines.runBlocking
import net.chariskar.breakthemod.Main
import net.chariskar.breakthemod.TestScope
import net.chariskar.breakthemod.client.api.Fetch
import net.chariskar.breakthemod.client.objects.StaffList
import net.chariskar.breakthemod.client.utils.Config
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull
import kotlin.test.assertIs

/**
 * Test class for [net.chariskar.breakthemod.client.api.Fetch]
 * */
class EarthmcApiTest {

    @Test
    fun `test Get request`() {
        runBlocking {
            val result = Fetch.getRequest<StaffList>(Main.config.staffRepoUrl)
            assertNotNull(result)
            assertIs<StaffList>(result)
        }
    }

}