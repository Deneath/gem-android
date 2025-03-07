package com.gemwallet.android.blockchain.clients.aptos

import com.gemwallet.android.blockchain.clients.aptos.services.AptosAccountsService
import com.gemwallet.android.blockchain.clients.aptos.services.AptosFeeService
import com.gemwallet.android.blockchain.rpc.handleError
import com.gemwallet.android.ext.type
import com.gemwallet.android.model.Fee
import com.gemwallet.android.model.GasFee
import com.gemwallet.android.model.TxSpeed
import com.wallet.core.blockchain.aptos.models.AptosAccount
import com.wallet.core.primitives.AssetId
import com.wallet.core.primitives.AssetSubtype
import com.wallet.core.primitives.Chain
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import java.math.BigInteger

internal class AptosFeeCalculator(
    private val chain: Chain,
    private val feeRpcClient: AptosFeeService,
    private val accountsRpcClient: AptosAccountsService,
) {
    suspend fun calculate(assetId: AssetId, destination: String): Fee = withContext(Dispatchers.IO) {
        val gasPriceJob = async { feeRpcClient.feePrice().getOrThrow().prioritized_gas_estimate.toBigInteger() }
        val isNewJob = async {
            val result = accountsRpcClient.accounts(destination).handleError<AptosAccount>()
            result?.sequence_number == null
        }
        val (gasPrice, isNew) = Pair(gasPriceJob.await(), isNewJob.await())
        val gasLimit = when(assetId.type()) {
            AssetSubtype.NATIVE -> BigInteger.valueOf(if (isNew) 676 else 9)
            AssetSubtype.TOKEN -> BigInteger("1000")
        }
        GasFee(
            feeAssetId = AssetId(chain),
            speed = TxSpeed.Normal,
            maxGasPrice = gasPrice,
            limit = gasLimit.multiply(BigInteger("2")) // * 2 for safety
        )
    }
}