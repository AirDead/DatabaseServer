package ru.airdead.client

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException
import java.util.concurrent.CompletableFuture

/**
 * A client for performing asynchronous database operations over HTTP.
 * @property serverAddress The address of the server hosting the database.
 * @property port The port on which the server is listening.
 */
class DatabaseClient(private val serverAddress: String, private val port: Int) {
    private val client = OkHttpClient()
    private val gson = Gson()

    /**
     * Asynchronously retrieves all data from a specified table.
     * @param tableName The name of the table from which to fetch the data.
     * @return A CompletableFuture that will complete with the map of data or an exception if an error occurs.
     */
    fun getAllDataAsync(tableName: String): CompletableFuture<Map<String, Map<String, Any>>> {
        val url = "http://$serverAddress:$port/api/$tableName"
        val request = Request.Builder().url(url).get().build()

        val future = CompletableFuture<Map<String, Map<String, Any>>>()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                future.completeExceptionally(e)
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    val typeRef = object : TypeToken<Map<String, Map<String, Any>>>() {}.type
                    future.complete(gson.fromJson(response.body?.string(), typeRef))
                } else {
                    future.complete(emptyMap())
                }
            }
        })
        return future
    }

    /**
     * Asynchronously updates data in a specified table.
     * @param tableName The name of the table where data will be updated.
     * @param data The data to update in the form of a map.
     * @return A CompletableFuture that will complete with the HTTP status code of the update operation or an exception if an error occurs.
     */
    fun updateAllDataAsync(tableName: String, data: Map<String, Map<String, Any>>): CompletableFuture<Int> {
        val url = "http://$serverAddress:$port/api/$tableName"
        val json = gson.toJson(data)
        val mediaType = "application/json; charset=utf-8".toMediaType()
        val body = json.toRequestBody(mediaType)
        val request = Request.Builder().url(url).put(body).build()

        return getFutureForUpdate(request)
    }

    /**
     * Private helper function to create a CompletableFuture that handles the HTTP response for an update operation.
     * @param request The prepared HTTP request for the operation.
     * @return A CompletableFuture that processes the response.
     */
    private fun getFutureForUpdate(request: Request): CompletableFuture<Int> {
        val future = CompletableFuture<Int>()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                future.completeExceptionally(e)
            }

            override fun onResponse(call: Call, response: Response) {
                try {
                    val statusCode = response.code
                    future.complete(statusCode)
                } catch (e: Exception) {
                    future.completeExceptionally(e)
                }
            }
        })

        return future
    }
}
