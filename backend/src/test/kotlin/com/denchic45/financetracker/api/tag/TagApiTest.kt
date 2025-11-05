package com.denchic45.financetracker.api.tag

import com.denchic45.financetracker.api.KtorClientTest
import com.denchic45.financetracker.api.assertedNone
import com.denchic45.financetracker.api.assertedRight
import com.denchic45.financetracker.api.tag.model.TagRequest
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.koin.core.component.inject
import org.koin.core.parameter.parametersOf

class TagApiTest : KtorClientTest() {
    private val tagApi: TagApi by inject { parametersOf(client) }

    @Test
    fun testCrud(): Unit = runBlocking {
        val initialName = "Travel"
        val updatedName = "Business Travel"

        // 1. CREATE
        val createRequest = TagRequest(name = initialName)
        println("Creating Tag: $initialName")
        val createdTag = tagApi.create(createRequest).assertedRight().apply {
            assertEquals(initialName, name, "Created tag name must match request name.")
            assertTrue(id > 0, "Created tag must have a valid ID.")
        }
        println("Tag created with ID: ${createdTag.id}")

        // 2. READ (Get by Id)
        println("Reading Tag by ID: ${createdTag.id}")
        tagApi.getById(createdTag.id).assertedRight().apply {
            assertEquals(createdTag.id, id)
            assertEquals(initialName, name)
        }

        // 3. READ (Get List)
        println("Reading Tag List")
        val tagList = tagApi.getList().assertedRight()
        val foundInList = tagList.find { it.id == createdTag.id }
        assertEquals(createdTag, foundInList, "Created tag must be present in the list.")

        // 4. UPDATE
        println("Updating Tag ID: ${createdTag.id} to: $updatedName")
        val updateRequest = TagRequest(name = updatedName)
        tagApi.update(createdTag.id, updateRequest).assertedRight().apply {
            assertEquals(createdTag.id, id, "Updated tag ID must remain the same.")
            assertEquals(updatedName, name, "Updated tag name must match update request.")
        }

        // 5. DELETE
        println("Deleting Tag ID: ${createdTag.id}")
        tagApi.delete(createdTag.id).assertedNone() // Asserts successful deletion (returns None on success)

        // 6. READ after DELETE (Negative Test)
        println("Attempting to read deleted Tag ID: ${createdTag.id}")
        val getAfterDeleteResult = tagApi.getById(createdTag.id)
        assertTrue(getAfterDeleteResult.isLeft(), "Getting deleted tag should return an error.")
    }
}