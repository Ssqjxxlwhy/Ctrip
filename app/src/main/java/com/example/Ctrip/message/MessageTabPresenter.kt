package com.example.Ctrip.message

class MessageTabPresenter(
    private val model: MessageTabContract.Model
) : MessageTabContract.Presenter {
    
    private var view: MessageTabContract.View? = null
    
    override fun attachView(view: MessageTabContract.View) {
        this.view = view
    }
    
    override fun detachView() {
        view = null
    }
    
    override fun loadMessageData() {
        view?.showLoading()
        try {
            val data = model.getMessageData()
            view?.showMessageData(data)
        } catch (e: Exception) {
            view?.showError("加载消息数据失败")
        } finally {
            view?.hideLoading()
        }
    }
    
    override fun onMessageActionClicked(actionId: String) {
        when (actionId) {
            "order_travel" -> view?.navigateToOrderTravel()
            "interactive_message" -> view?.navigateToInteractiveMessage()
            "account_notification" -> view?.navigateToAccountNotification()
            "member_service" -> view?.navigateToMemberService()
        }
    }
    
    override fun onSystemMessageClicked(messageId: String) {
        view?.onSystemMessageClick(messageId)
    }
    
    override fun onConversationMessageClicked(messageId: String) {
        view?.onConversationMessageClick(messageId)
    }
    
    override fun onCustomerServiceClicked() {
        view?.onCustomerServiceClick()
    }
    
    override fun onSettingsClicked() {
        view?.onSettingsClick()
    }
}