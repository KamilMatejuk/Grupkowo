package com.example.myapplication

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.android.volley.*
import com.android.volley.toolbox.BasicNetwork
import com.android.volley.toolbox.DiskBasedCache
import com.android.volley.toolbox.HurlStack
import com.android.volley.toolbox.JsonObjectRequest
import org.json.JSONException
import org.json.JSONObject
import org.xmlpull.v1.XmlPullParserException
import java.net.ConnectException
import java.net.MalformedURLException
import java.net.SocketException
import java.net.SocketTimeoutException

/**
 * ************************************ USAGE INSTRUCTIONS *****************************************
 *
 * ******** This is helper object, create requests using other objects from this package ***********
 * ******** UserRequests / GroupRequests / PostRequests / CommentRequests / ChatRequests ***********
 *
 *
 * If functionality you need is not implemented, create it in correct file in this package
 * (e.g. just like 'register()' function):
 *
 * fun yourFunction(arg1, arg2, ..., argN, requestId: Int, cls: ServerLisener) {
 *          val url = url + "your/relative/path/"
 *          val body = JSONObject("""
 *              {
 *                  "paramName1": "$arg1",
 *                  "paramName2": "$arg2",
 *                  ...
 *                  "paramNameN": "$argN"
 *              }
 *              """.replace(" ", "")
 *              .replace("\n", ""))
 *          sendRequest(url, Request.Method.POST, body, requestId, cls)
 *      }
 *
 * Remember, to create 'body' object the same as server expects (check in server docs)
 * If argument in 'body' is string, surround it with double quotes (")
 * If argument in 'body' is int, don't surround it with anything
 *
 *
 * In your Activity, override onStop function:
 *      override fun onStop() {
 *          super.onStop()
 *          Server.cancelRequests()
 *      }
 *
 *
 * Run your function like e.g.:
 *      UserRequests.yourFunction(arg1, arg2, ..., argN, requestId, this)
 * Remember your requestId
 *
 * Change Activity to implement ServerListener,
 * and override required method onResponseArrived()
 *
 *
 * This method onResponseArrived() will fire, when server will return a response.
 * Here you can use requestId to find response to your specific request, check if
 * everything went correct (error == null) and act accordingly
 */


interface ServerLisener {
    fun onResponseArrived(requestId: Int, error: String?, response: JSONObject?)
}

object Server {
    // 127.0.0.1 odnosi się do samego emulatora,
    // 10.0.2.2 odnosi się do komputera, na którym uruchomiony jest emulator
    var url: String = "http://10.0.2.2:8000/"
    private var tag: String = "TAG"
    private var requestQueue: RequestQueue? = null

    /**
     * Setup requests queue with cache and make sure user granted Internet permissions
     *
     * @param context applicationContext (this)
     * @param activity launcher activity (this)
     */
    fun setUp(context: Context, activity: Activity) {
        // setup cache
        val oneMB = 1024 * 1024 // 1 MB
        val cache = DiskBasedCache(context.cacheDir, oneMB)
        val network = BasicNetwork(HurlStack())
        // create queue for requests
        requestQueue = RequestQueue(cache, network).apply {
            start()
        }
        // check permissions
        if (ContextCompat.checkSelfPermission(
                context.applicationContext,
                Manifest.permission.INTERNET
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                activity,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                1
            )
        } else {
            Toast.makeText(context, "Internet permissions granted", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Send request to server
     *
     * @param url adres
     * @param method Request.Method.GET / POST / PUT / DELETE
     * @param body json to be sent
     * @param id number related to request, used for finding response correlating with request
     *           later, when response arrives
     * @param cls class sending the request, implementing ServerLisener
     * @param token if true adds token into header, to access protected endpoints
     */
    fun sendRequest(
        url: String,
        method: Int,
        body: JSONObject?,
        id: Int,
        cls: ServerLisener,
        token: Boolean = false
    ) {
        val jsonObjectRequest = object : JsonObjectRequest(method, url, body,
            { response ->
                cls.onResponseArrived(id, null, response)
            },
            { error ->
                try {
                    val errorBody = error.networkResponse?.data?.decodeToString() ?: "unknown error"
                    val errorMessage = if (errorBody.contains("detail")) {
                        JSONObject(errorBody).getString("detail")
                    } else {
                        handleError(error)
                    }
                    cls.onResponseArrived(id, errorMessage, null)
                } catch (e: Exception) {
                    cls.onResponseArrived(id, e.message.toString(), null)
                }
            }) {
            override fun getHeaders(): Map<String, String> {
                val params: MutableMap<String, String> = HashMap()
                if (token) {
                    val tokenStr = getToken((cls as Activity))
                    params["Authorization"] = "Bearer $tokenStr"
                }
                return params
            }
        }
        requestQueue?.add(jsonObjectRequest)
    }

    /**
     * Handle errors not returned by server,
     * but rather resulting from external errors
     * (like network error or malformed url)
     */
    private fun handleError(error: VolleyError): String {
        var errorMsg = ""
        if (error is NetworkError || error.cause is ConnectException) {
            errorMsg = "Your device is not connected to internet"
        } else if (error.cause is MalformedURLException) {
            errorMsg = "Bad request URL"
        } else if (error is ParseError || error.cause is IllegalStateException || error.cause is JSONException || error.cause is XmlPullParserException) {
            errorMsg = "Error parsing response data"
        } else if (error.cause is OutOfMemoryError) {
            errorMsg = "Device out of memory"
        } else if (error is AuthFailureError) {
            errorMsg = "Unauthorized request"
        } else if (error is ServerError || error.cause is ServerError) {
            errorMsg = "Internal server error"
        } else if (error is TimeoutError ||
            error.cause is SocketTimeoutException ||
            error.cause is SocketException
        ) {
            errorMsg = "Connection timed out"
        } else {
            errorMsg = "An unknown error occurred during the operation"
        }
        return errorMsg
    }

    /**
     * Save received JWT Token into Shared Preferences
     *
     * @param context applicationContext (this)
     * @param token received token string
     */
    fun saveToken(context: Context, token: String) {
        val sharedPrefsKey = context.getString(R.string.shared_prefs)
        val tokenKey = context.getString(R.string.saved_token_key)
        val sharedPref = context.getSharedPreferences(sharedPrefsKey, Context.MODE_PRIVATE)
        with (sharedPref.edit()) {
            putString(tokenKey, token)
            apply()
        }
    }

    /**
     * Get JWT Token from Shared Preferences
     *
     * @param context applicationContext (this)
     */
    fun getToken(context: Context): String? {
        val sharedPrefsKey = context.getString(R.string.shared_prefs)
        val tokenKey = context.getString(R.string.saved_token_key)
        val sharedPref = context.getSharedPreferences(sharedPrefsKey, Context.MODE_PRIVATE)
        val token = sharedPref.getString(tokenKey, "error")
        if (token.equals("error")) {
            return null
        }
        return token
    }

    /**
     * Remove JWT Token from Shared Preferences
     *
     * @param context applicationContext (this)
     * @param token received token string
     */
    fun deleteToken(context: Context) {
        val sharedPrefsKey = context.getString(R.string.shared_prefs)
        val tokenKey = context.getString(R.string.saved_token_key)
        val sharedPref = context.getSharedPreferences(sharedPrefsKey, Context.MODE_PRIVATE)
        with (sharedPref.edit()) {
            remove(tokenKey)
            apply()
        }
    }

    /**
     * Cancel all requests - should be run in Activity onStop()
     */
    fun cancelRequests() {
        requestQueue?.cancelAll(tag)
    }
}