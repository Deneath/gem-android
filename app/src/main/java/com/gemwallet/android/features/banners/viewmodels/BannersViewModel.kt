package com.gemwallet.android.features.banners.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gemwallet.android.cases.banners.CancelBannerCase
import com.gemwallet.android.cases.banners.GetBannersCase
import com.gemwallet.android.cases.update.DownloadLatestApkCase
import com.gemwallet.android.cases.update.ObserveUpdateDownloadCase
import com.gemwallet.android.data.repositoreis.session.SessionRepository
import com.gemwallet.android.ext.getReserveBalance
import com.gemwallet.android.model.Crypto
import com.gemwallet.android.model.format
import com.wallet.core.primitives.Asset
import com.wallet.core.primitives.Banner
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.math.BigInteger
import javax.inject.Inject

@HiltViewModel
class BannersViewModel @Inject constructor(
    private val sessionRepository: SessionRepository,
    private val getBannersCase: GetBannersCase,
    private val cancelBannerCase: CancelBannerCase,
    private val observeUpdateDownloadCase: ObserveUpdateDownloadCase,
    private val downloadLatestApkCase: DownloadLatestApkCase
) : ViewModel() {

    val banners = MutableStateFlow<List<Banner>>(emptyList())
    val downloadApkState = observeUpdateDownloadCase.observeUpdateDownload()
        .stateIn(viewModelScope, SharingStarted.Eagerly, null)

    fun init(asset: Asset?, isGlobal: Boolean) {
        val wallet = if (isGlobal) {
            null
        } else {
            sessionRepository.getSession()?.wallet
        }
        viewModelScope.launch {
            val banners = getBannersCase.getActiveBanners(wallet, asset)
            this@BannersViewModel.banners.update { banners }
        }
    }

    fun getActivationFee(asset: Asset?): String {
        asset ?: return ""
        val value = asset.id.chain.getReserveBalance()
        if (value == BigInteger.ZERO) return ""
        return asset.format(Crypto(value))
    }

    fun onCancel(banner: Banner) = viewModelScope.launch {
        cancelBannerCase.cancelBanner(banner)
        init(banner.asset, banner.wallet == null)
    }

    fun onRetryDownload() = viewModelScope.launch {
        downloadLatestApkCase.downloadLatestApk()
    }
}