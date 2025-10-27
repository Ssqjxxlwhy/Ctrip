package com.example.Ctrip.my

class MyTabPresenter(
    private val model: MyTabContract.Model
) : MyTabContract.Presenter {
    
    private var view: MyTabContract.View? = null
    
    override fun attachView(view: MyTabContract.View) {
        this.view = view
    }
    
    override fun detachView() {
        view = null
    }
    
    override fun loadUserData() {
        view?.showLoading()
        try {
            val data = model.getUserData()
            view?.showUserData(data)
        } catch (e: Exception) {
            view?.showError("加载用户数据失败")
        } finally {
            view?.hideLoading()
        }
    }
    
    override fun onProfileClicked() {
        view?.navigateToProfile()
    }
    
    override fun onMemberCenterClicked() {
        view?.navigateToMemberCenter()
    }
    
    override fun onMenuItemClicked(menuId: String) {
        view?.navigateToMenuItem(menuId)
    }
    
    override fun onWalletItemClicked(walletId: String) {
        view?.navigateToWalletItem(walletId)
    }
    
    override fun onPromotionClicked(promotionId: String) {
        view?.navigateToPromotion(promotionId)
    }
    
    override fun onPublishItemClicked(publishId: String) {
        view?.navigateToPublishItem(publishId)
    }
    
    override fun onBottomPromotionClicked() {
        view?.navigateToBottomPromotion()
    }
    
    override fun onSettingsClicked() {
        view?.navigateToSettings()
    }
    
    override fun onSignInClicked() {
        view?.navigateToSignIn()
    }
    
    override fun onScanClicked() {
        view?.navigateToScan()
    }
    
    override fun onCustomerServiceClicked() {
        view?.navigateToCustomerService()
    }
}