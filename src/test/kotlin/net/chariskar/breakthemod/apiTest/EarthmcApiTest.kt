package net.chariskar.breakthemod.apiTest;

import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import net.chariskar.breakthemod.Main
import net.chariskar.breakthemod.TestScope
import net.chariskar.breakthemod.client.api.Fetch
import net.chariskar.breakthemod.client.objects.Nation
import net.chariskar.breakthemod.client.objects.Resident
import net.chariskar.breakthemod.client.objects.StaffList
import net.chariskar.breakthemod.client.objects.Town
import org.junit.jupiter.api.Test
import kotlin.test.assertNotNull
import kotlin.test.assertIs

/**
 * Test class for [net.chariskar.breakthemod.client.api.Fetch]
 * */
class EarthmcApiTest {
    val scope = TestScope.scope

    @Test
    fun `test GET request`() {
        scope.launch {
            val result = Fetch.getRequest<StaffList>(Main.config.staffRepoUrl)
            assertNotNull(result)
            assertIs<StaffList>(result)
        }
    }

    @Test
    fun `test towns`() {
        scope.launch {
            val result = Fetch.getTown("paris")
            assertNotNull(result)
            assertIs<Town>(result)
        }
    }

    @Test
    fun `test nations`() {
        scope.launch {
            val result = Fetch.getNation("france")
            assertNotNull(result)
            assertIs<Nation>(result)
        }
    }

    @Test
    fun `test players`() {
        scope.launch {
            val result = Fetch.getResident("charis_k")
            assertNotNull(result)
            assertIs<Resident>(result)
        }
    }

}