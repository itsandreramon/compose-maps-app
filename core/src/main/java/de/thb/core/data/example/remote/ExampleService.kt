package de.thb.core.data.example.remote

import de.thb.core.domain.ExampleResponse
import retrofit2.http.GET

interface ExampleService {

    @GET("all")
    suspend fun getAll(): List<ExampleResponse>
}
