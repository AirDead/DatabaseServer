package ru.airdead.demo

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.Update
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException

/**
 * A REST controller for generic database operations on a MongoDB collection.
 * @property mongoTemplate The MongoTemplate used for MongoDB interactions.
 * @property gson The Gson instance used for JSON serialization and deserialization.
 */
@RestController
@RequestMapping("/api/{tableName}")
class GenericController(private val mongoTemplate: MongoTemplate, private val gson: Gson) {

    /**
     * Retrieves all documents from the specified table.
     * @param tableName The name of the MongoDB collection.
     * @return A map of document data indexed by document IDs.
     */
    @GetMapping
    fun getAll(@PathVariable tableName: String): Map<String, Map<String, Any>> {
        return try {
            mongoTemplate.findAll(String::class.java, tableName)
                .map { gson.fromJson<Map<String, Any>>(it, object : TypeToken<Map<String, Any>>() {}.type) }
                .associateBy { it["id"] as? String ?: "" }
        } catch (e: Exception) {
            throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error fetching data from MongoDB", e)
        }
    }

    /**
     * Updates multiple documents in the specified table.
     * @param tableName The name of the MongoDB collection to update.
     * @param jsonData The JSON string containing the data to update.
     * @return HTTP status code indicating the outcome of the operation.
     */
    @PutMapping
    fun updateAllData(@PathVariable tableName: String, @RequestBody jsonData: String): Int {
        return try {
            val updateData: Map<String, Map<String, Any>> = gson.fromJson(jsonData, object : TypeToken<Map<String, Map<String, Any>>>() {}.type)
            updateData.forEach { (id, dataMap) ->
                val cleanDataMap = dataMap.filterKeys { it != "_id" } // Ensuring _id is not modified
                val query = Query(Criteria.where("id").`is`(id))
                val update = Update().apply {
                    cleanDataMap.forEach { (key, value) -> set(key, value) }
                }
                mongoTemplate.upsert(query, update, tableName)
            }
            HttpStatus.OK.value()
        } catch (e: Exception) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Error updating MongoDB data", e)
        }
    }
}
