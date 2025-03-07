package com.gemwallet.android.data.repositoreis.tokens

import com.gemwallet.android.blockchain.clients.GetTokenClient
import com.gemwallet.android.cases.tokens.GetTokensCase
import com.gemwallet.android.cases.tokens.SearchTokensCase
import com.gemwallet.android.data.service.store.database.TokensDao
import com.gemwallet.android.data.service.store.database.entities.DbToken
import com.gemwallet.android.data.service.store.database.mappers.AssetInfoMapper
import com.gemwallet.android.data.service.store.database.mappers.TokenMapper
import com.gemwallet.android.data.services.gemapi.GemApiClient
import com.gemwallet.android.ext.assetType
import com.gemwallet.android.ext.toAssetId
import com.gemwallet.android.ext.toIdentifier
import com.gemwallet.android.model.AssetInfo
import com.wallet.core.primitives.Asset
import com.wallet.core.primitives.AssetBasic
import com.wallet.core.primitives.AssetFull
import com.wallet.core.primitives.AssetId
import com.wallet.core.primitives.AssetProperties
import com.wallet.core.primitives.AssetScore
import com.wallet.core.primitives.Chain
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class TokensRepository (
    private val tokensDao: TokensDao,
    private val gemApiClient: GemApiClient,
    private val getTokenClients: List<GetTokenClient>,
) : GetTokensCase, SearchTokensCase {
    private val mapper = TokenMapper()

    override suspend fun getByIds(ids: List<AssetId>): List<Asset> = withContext(Dispatchers.IO) {
        tokensDao.getById(ids.map { it.toIdentifier() }).map(mapper::asEntity)
    }

    override fun getByChains(chains: List<Chain>, query: String): Flow<List<Asset>> {
        return tokensDao.search(chains.mapNotNull { chain -> chain.assetType() }, query)
            .map { assets -> assets.map(mapper::asEntity) }
    }

    override fun swapSearch(
        chains: List<Chain>,
        assetIds: List<AssetId>,
        query: String
    ): Flow<List<Asset>> {
        return tokensDao.swapSearch(query)
        .map { items ->
            items.filter {
                val assetId = it.id.toAssetId()
                chains.contains(assetId?.chain) || assetIds.contains(assetId)
            }
        }
        .map { assets -> assets.map(mapper::asEntity) }
    }

    override suspend fun search(query: String): Boolean = withContext(Dispatchers.IO) {
        if (query.isEmpty()) {
            return@withContext false
        }
        val result = gemApiClient.search(query)
        val tokens = result.getOrNull()
        val assets = if (tokens.isNullOrEmpty()) {
            val assets = getTokenClients.map {
                async {
                    try {
                        if (it.isTokenQuery(query)) {
                            it.getTokenData(query)
                        } else {
                            null
                        }
                    } catch (_: Throwable) {
                        null
                    }
                }
            }
            .awaitAll()
            .mapNotNull { it }
            .map { AssetFull(asset = it, score = AssetScore(0), links = emptyList(), properties = AssetProperties(false, false, false, false, false)) }
            tokensDao.insert(assets.map { it.toEntity() })
            assets
        } else {
            val assets = tokens.filter { it.asset.id != null }
            tokensDao.insert(assets.map { it.toEntity() })
            assets
        }
        assets.isNotEmpty()
    }

    override suspend fun search(assetId: AssetId): Boolean {
        val tokenId = assetId.tokenId ?: return false
        val asset = getTokenClients
            .firstOrNull { it.supported(assetId.chain) && it.isTokenQuery(tokenId) }
            ?.getTokenData(tokenId)
        if (asset == null) {
            return search(tokenId)
        }
        tokensDao.insert(
            DbToken(
                id = asset.id.toIdentifier(),
                name = asset.name,
                symbol = asset.symbol,
                decimals = asset.decimals,
                type = asset.type,
                rank = 0,
            )
        )
        return true
    }

    override suspend fun assembleAssetInfo(assetId: AssetId): Flow<AssetInfo?> {
        return tokensDao.assembleAssetInfo(assetId.chain, assetId.toIdentifier())
            .map { AssetInfoMapper().asDomain(it).firstOrNull() }
    }

    private fun AssetFull.toEntity() =DbToken(
        id = asset.id.toIdentifier(),
        name = asset.name,
        symbol = asset.symbol,
        decimals = asset.decimals,
        type = asset.type,
        rank = score.rank,
    )

    private fun AssetBasic.toEntity() =DbToken(
        id = asset.id.toIdentifier(),
        name = asset.name,
        symbol = asset.symbol,
        decimals = asset.decimals,
        type = asset.type,
        rank = score.rank,
    )
}