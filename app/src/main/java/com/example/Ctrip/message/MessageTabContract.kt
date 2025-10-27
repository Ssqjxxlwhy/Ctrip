package com.example.Ctrip.message

import com.example.Ctrip.model.MessageData

interface MessageTabContract {
    
    interface View {
        fun showMessageData(data: MessageData)
        fun showLoading()
        fun hideLoading()
        fun showError(message: String)
        fun navigateToOrderTravel()
        fun navigateToInteractiveMessage()
        fun navigateToAccountNotification()
        fun navigateToMemberService()
        fun onSystemMessageClick(messageId: String)
        fun onConversationMessageClick(messageId: String)
        fun onCustomerServiceClick()
        fun onSettingsClick()
    }
    
    interface Presenter {
        fun attachView(view: View)
        fun detachView()
        fun loadMessageData()
        fun onMessageActionClicked(actionId: String)
        fun onSystemMessageClicked(messageId: String)
        fun onConversationMessageClicked(messageId: String)
        fun onCustomerServiceClicked()
        fun onSettingsClicked()
    }
    
    interface Model {
        fun getMessageData(): MessageData
    }
}