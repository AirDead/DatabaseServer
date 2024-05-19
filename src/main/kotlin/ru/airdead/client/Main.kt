package ru.airdead.client

import java.util.concurrent.ExecutionException

fun main() {
    // Create an instance of the DatabaseClient
    val databaseClient = DatabaseClient("localhost", 33141)

    // Example usage of getAllDataAsync to fetch all data from "exampleTable"
    val getAllDataFuture = databaseClient.getAllDataAsync("players")
    try {
        val allData = getAllDataFuture.get() // This call is blocking and will wait until the operation completes
        println("Data retrieved from exampleTable: $allData")
    } catch (e: InterruptedException) {
        println("Operation was interrupted.")
    } catch (e: ExecutionException) {
        println("An error occurred while fetching data: ${e.cause}")
    }

    // Example usage of updateAllDataAsync to update data in "exampleTable"
    // Creating a sample data map to update
    val dataToUpdate = mapOf(
        "1" to mapOf("name" to "John Doe", "age" to 30),
        "2" to mapOf("name" to "Jane Smith", "age" to 25)
    )

    val updateDataFuture = databaseClient.updateAllDataAsync("exampleTable", dataToUpdate)
    try {
        val responseStatus = updateDataFuture.get() // This call is also blocking
        println("Update operation status code: $responseStatus")
    } catch (e: InterruptedException) {
        println("Operation was interrupted.")
    } catch (e: ExecutionException) {
        println("An error occurred during the update operation: ${e.cause}")
    }
}
