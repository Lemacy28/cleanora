package com.lemacy.cleanora.data

import android.util.Base64
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import org.json.JSONObject
import java.io.IOException

class MpesaAdminViewModel : ViewModel() {

    private val consumerKey = "H1QXWjwRkOa7J3tVael8YdrMJQrRADRbWpCUxmBlECAfomvQ"
    private val consumerSecret = "MNbIBwG2WYrBVnatbMJD9Yy2Jgq4JBSMlHRLPIaE1j84Xxje4ssgEzSdquD41GSP"
    private val initiatorName = "testapi"
    private val shortCode = "174379"
    private val securityCredential = "Z7WNqISHMddwvpD4TdHm79dDzLxzOnqI+881FzTetht8ZIrPcsgXyoarsYxu1SPhyxFNUEs09G9jB0qkyOUwHikWGmPJqIkDmR9yBHNOy3dfM7AqUjLUMc/Loc/JaC4AiWcPbHZIoicomjfCdVoqaxGTkPTyU3MvXtyznTfHNMep2Iib+8MUWypI3mQtWZ31vs4LolkCOH3DwzhvOj/7ZcCbo8Blfq7vo2uU10F1wyuX6bt5QYgEvtDHzIxBIUldlW2LxRd8QvB4GT7eGsFGJI0AD7tjViF3c2eQdUWdS/pdeNIOG9e4+nbnTRSeR0o1KDjPBnUTrFi3oUfAw82zfQ==" // Encrypted via Safaricom tool
    private val callbackUrl = "https://webhook.site/abcd1234-xxxx-yyyy-zzzz-efgh5678"


    private val client = OkHttpClient()

    fun sendMoneyToCleaner(phoneNumber: String, amount: String) {
        viewModelScope.launch {
            getAccessToken { token ->
                token?.let {
                    performB2CPayment(token, phoneNumber, amount)
                }
            }
        }
    }

    private fun getAccessToken(onResult: (String?) -> Unit) {
        val credentials = "$consumerKey:$consumerSecret"
        val auth = Base64.encodeToString(credentials.toByteArray(), Base64.NO_WRAP)

        val request = Request.Builder()
            .url("https://sandbox.safaricom.co.ke/oauth/v1/generate?grant_type=client_credentials")
            .get()
            .addHeader("Authorization", "Basic $auth")
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("MPESA", "Token fetch failed", e)
                onResult(null)
            }

            override fun onResponse(call: Call, response: Response) {
                val json = JSONObject(response.body?.string() ?: "")
                val token = json.optString("access_token", null)
                onResult(token)
            }
        })
    }

    private fun performB2CPayment(token: String, phoneNumber: String, amount: String) {
        val json = JSONObject().apply {
            put("InitiatorName", initiatorName)
            put("SecurityCredential", securityCredential)
            put("CommandID", "BusinessPayment") // Others: SalaryPayment, PromotionPayment
            put("Amount", amount)
            put("PartyA", shortCode)
            put("PartyB", phoneNumber)
            put("Remarks", "Cleaner Payment")
            put("QueueTimeOutURL", callbackUrl)
            put("ResultURL", callbackUrl)
            put("Occasion", "CleaningJob")
        }

        val requestBody = RequestBody.create(
            "application/json".toMediaTypeOrNull(),
            json.toString()
        )

        val request = Request.Builder()
            .url("https://sandbox.safaricom.co.ke/mpesa/b2c/v1/paymentrequest")
            .post(requestBody)
            .addHeader("Authorization", "Bearer $token")
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("MPESA", "B2C Payment failed", e)
            }

            override fun onResponse(call: Call, response: Response) {
                Log.d("MPESA", "B2C Payment Response: ${response.body?.string()}")
            }
        })
    }
}
