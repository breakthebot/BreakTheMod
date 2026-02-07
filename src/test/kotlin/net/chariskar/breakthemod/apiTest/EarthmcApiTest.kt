package net.chariskar.breakthemod.apiTest;

import kotlinx.coroutines.runBlocking
import net.chariskar.breakthemod.TestScope
import net.chariskar.breakthemod.client.api.Fetch
import net.chariskar.breakthemod.client.objects.StaffList
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
            val result = Fetch.getRequest<StaffList>(Fetch.Items.STAFF.url)
            assertNotNull(result)
            assertIs<StaffList>(result)
        }
    }

}