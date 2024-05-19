package ru.airdead.demo

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document
data class GenericData(
    @Id val id: String,
    val tableName: String,
    val data: Map<String, Any>
)
