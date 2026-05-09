package com.example.northstar.data.remote

import retrofit2.http.GET
import retrofit2.http.Path

interface CurrencyApiService {
    // We only use {base}.json because "v1/currencies/" is already in the AppModule Base URL
    @GET("{base}.json")
    suspend fun getExchangeRates(
        @Path("base") base: String
    ): Map<String, Any>
}