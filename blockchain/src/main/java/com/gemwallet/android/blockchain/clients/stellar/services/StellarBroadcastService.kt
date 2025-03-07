package com.gemwallet.android.blockchain.clients.stellar.services

import com.wallet.core.blockchain.stellar.StellarTransactionBroadcast
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.POST

interface StellarBroadcastService {
    @POST("/transactions")
    suspend fun broadcast(@Body body: RequestBody): Result<StellarTransactionBroadcast?>
}