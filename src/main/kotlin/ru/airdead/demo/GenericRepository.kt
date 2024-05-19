package ru.airdead.demo

import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.stereotype.Repository

@Repository
class GenericRepository(private val mongoTemplate: MongoTemplate) {

    fun save(tableName: String, genericData: GenericData) {
        mongoTemplate.save(genericData, tableName)
    }

    fun findAll(tableName: String): List<GenericData> {
        return mongoTemplate.findAll(GenericData::class.java, tableName)
    }

    fun deleteById(tableName: String, id: String) {
        mongoTemplate.remove(GenericData(id = id, tableName = tableName, data = emptyMap()), tableName)
    }
}