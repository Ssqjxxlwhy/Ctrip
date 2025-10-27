package com.example.Ctrip.message

import android.content.Context
import com.example.Ctrip.model.MessageData
import com.google.gson.Gson
import java.io.IOException

class MessageTabModelImpl(private val context: Context) : MessageTabContract.Model {
    
    override fun getMessageData(): MessageData {
        return try {
            val jsonString = context.assets.open("data/message_data.json").bufferedReader().use { it.readText() }
            Gson().fromJson(jsonString, MessageData::class.java)
        } catch (e: IOException) {
            // Return default data if file reading fails
            MessageData(
                messageActions = emptyList(),
                systemMessages = emptyList(),
                conversationMessages = emptyList()
            )
        }
    }
}