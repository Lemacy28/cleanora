package com.lemacy.cleanora.data


import okhttp3.Call
import okhttp3.OkHttpClient
import okhttp3.Request
import android.util.Base64
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import org.json.JSONObject
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class MpesaViewModel : ViewModel() {

    private val consumerKey = "H1QXWjwRkOa7J3tVael8YdrMJQrRADRbWpCUxmBlECAfomvQ"
    private val consumerSecret = "MNbIBwG2WYrBVnatbMJD9Yy2Jgq4JBSMlHRLPIaE1j84Xxje4ssgEzSdquD41GSP"
    private val passkey = "bfb279f9aa9bdbcf158e97dd71a467cd2e0c893059b10f78e6b72ada1ed2c919"
    private val businessShortCode = "174379"
    private val client = OkHttpClient()

    private fun getTimestamp(): String {
        val sdf = SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault())
        return sdf.format(Date())
    }

    private fun getPassword(timestamp: String): String {
        val data = "$businessShortCode$passkey$timestamp"
        return Base64.encodeToString(data.toByteArray(), Base64.NO_WRAP)
    }

    private fun getAccessToken(callback: (String?) -> Unit) {
        val credentials = "$consumerKey:$consumerSecret"
        val auth = "Basic " + Base64.encodeToString(credentials.toByteArray(), Base64.NO_WRAP)

        val request = Request.Builder()
            .url("https://sandbox.safaricom.co.ke/oauth/v1/generate?grant_type=client_credentials")
            .addHeader("Authorization", auth)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) = callback(null)
            override fun onResponse(call: Call, response: Response) {
                val body = response.body?.string()
                val accessToken = JSONObject(body!!).getString("access_token")
                callback(accessToken)
            }
        })
    }

    fun initiatePayment(phone: String, amount: String, onResult: (Boolean) -> Unit) {
        getAccessToken { token ->
            if (token == null) {
                onResult(false)
                return@getAccessToken
            }

            val timestamp = getTimestamp()
            val password = getPassword(timestamp)
            val json = JSONObject().apply {
                put("BusinessShortCode", businessShortCode)
                put("Password", password)
                put("Timestamp", timestamp)
                put("TransactionType", "CustomerPayBillOnline")
                put("Amount", amount)
                put("PartyA", phone)
                put("PartyB", businessShortCode)
                put("PhoneNumber", phone)
                put("CallBackURL", "https://mydomain.com/path")
                put("AccountReference", "Cleanora")
                put("TransactionDesc", "Pay Cleaner")
            }

            val body = RequestBody.create(
                "application/json; charset=utf-8".toMediaTypeOrNull(),
                json.toString()
            )

            val request = Request.Builder()
                .url("https://sandbox.safaricom.co.ke/mpesa/stkpush/v1/processrequest")
                .addHeader("Authorization", "Bearer $token")
                .post(body)
                .build()

            CoroutineScope(Dispatchers.IO).launch {
                client.newCall(request).enqueue(object : Callback {
                    override fun onFailure(call: Call, e: IOException) = onResult(false)
                    override fun onResponse(call: Call, response: Response) {
                        onResult(response.isSuccessful)
                    }
                })
            }
        }
    }
}
