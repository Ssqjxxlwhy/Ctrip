package com.example.Ctrip.my

import com.example.Ctrip.model.UserData

interface MyTabContract {
    
    interface View {
        fun showUserData(data: UserData)
        fun showLoading()
        fun hideLoading()
        fun showError(message: String)
        fun navigateToProfile()
        fun navigateToMemberCenter()
        fun navigateToMenuItem(menuId: String)
        fun navigateToWalletItem(walletId: String)
        fun navigateToPromotion(promotionId: String)
        fun navigateToPublishItem(publishId: String)
        fun navigateToBottomPromotion()
        fun navigateToSettings()
        fun navigateToSignIn()
        fun navigateToScan()
        fun navigateToCustomerService()
    }
    
    interface Presenter {
        fun attachView(view: View)
        fun detachView()
        fun loadUserData()
        fun onProfileClicked()
        fun onMemberCenterClicked()
        fun onMenuItemClicked(menuId: String)
        fun onWalletItemClicked(walletId: String)
        fun onPromotionClicked(promotionId: String)
        fun onPublishItemClicked(publishId: String)
        fun onBottomPromotionClicked()
        fun onSettingsClicked()
        fun onSignInClicked()
        fun onScanClicked()
        fun onCustomerServiceClicked()
    }
    
    interface Model {
        fun getUserData(): UserData
    }
}