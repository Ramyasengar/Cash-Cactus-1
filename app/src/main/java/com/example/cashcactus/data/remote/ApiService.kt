package com.example.cashcactus.data.remote

import retrofit2.http.Body
import retrofit2.http.POST

data class PredictionRequest(val expenses: List<Double>)
data class PredictionResponse(val prediction: Double, val trend: String)

interface ApiService {

    @POST("predict")
    suspend fun getPrediction(
        @Body request: PredictionRequest
    ): PredictionResponse
}