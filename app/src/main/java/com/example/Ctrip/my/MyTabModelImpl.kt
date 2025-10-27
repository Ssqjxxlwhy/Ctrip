package com.example.Ctrip.my

import android.content.Context
import com.example.Ctrip.model.*
import com.google.gson.Gson
import java.io.IOException

class MyTabModelImpl(private val context: Context) : MyTabContract.Model {
    
    override fun getUserData(): UserData {
        return try {
            val jsonString = context.assets.open("data/user_data.json").bufferedReader().use { it.readText() }
            val result = Gson().fromJson(jsonString, UserData::class.java)
            result ?: getDefaultData()
        } catch (e: Exception) {
            // Return default data if any error occurs
            getDefaultData()
        }
    }
    
    private fun getDefaultData(): UserData {
        return UserData(
            userProfile = UserProfile(
                displayName = "尊敬的会员",
                memberTitle = "主页"
            ),
            membershipInfo = MembershipInfo(
                level = "普通会员",
                levelIcon = "💎",
                benefits = emptyList()
            ),
            statistics = UserStatistics(
                favorites = 0,
                browsingHistory = 0,
                points = 0,
                coupons = 0
            ),
            menuItems = emptyList(),
            wallet = WalletInfo(walletItems = emptyList()),
            promotions = emptyList(),
            myPublish = MyPublishInfo(items = emptyList()),
            bottomPromotion = null
        )
    }
}