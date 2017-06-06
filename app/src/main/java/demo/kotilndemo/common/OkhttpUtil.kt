package demo.kotilndemo.common

import android.os.Handler
import android.os.Message
import android.util.Log
import okhttp3.*
import java.io.IOException
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * Created by zhangyao on 17/6/2.
 */
object OkhttpUtil {
    val TAG = "OkhttpUtil"

    val clinet: OkHttpClient by lazy {
        val client = OkHttpClient()
        client.newBuilder().connectTimeout(100, TimeUnit.SECONDS)
        client.newBuilder().readTimeout(100, TimeUnit.SECONDS)
        client.newBuilder().writeTimeout(100, TimeUnit.SECONDS)
        client
    }

    val ON_RESPONSE: Int = 1
    val ON_FAILURE: Int = 0

    fun sendMessage(onSuccess: (responseInfo: String) -> Unit,
                    onFailure: (error: Any, msg: String) -> Unit): Handler {
        val handler = object : Handler() {
            override fun handleMessage(msg: Message) {
                super.handleMessage(msg)
                when (msg.what) {
                    ON_RESPONSE -> {
                        onSuccess(msg.obj as String)
                    }

                    ON_FAILURE -> {
                        onFailure(msg.obj, "")
                    }
                }
            }
        }

        return handler
    }

    fun get(url: String, onSuccess: (responseInfo: String) -> Unit,
            onFailure: (error: Any, msg: String) -> Unit) {
        val request: Request = Request.Builder()
                .url(url)
                .build()
        val handler = sendMessage(onSuccess, onFailure)
        clinet.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                val msg = Message()
                msg.what = ON_FAILURE
                msg.obj = e;
                handler.sendMessage(msg)
            }

            override fun onResponse(call: Call, response: Response) {
                Log.i(TAG, ">>>>>>>>>>>>结果值>>>>>>>>>response.message()>>>>" + response.message())
                Log.i(TAG, ">>>>>>>>>>>>结果值>>>>>>response.toString()>>>>>>>" + response.toString())
                val code = response.code()
                val msg = Message()
                if (code == 200) {
                    val result = response.body()?.string() ?: ""
                    msg.what = ON_RESPONSE
                    msg.obj = result;
                } else {
                    msg.what = ON_FAILURE
                    msg.obj = null
                }
                handler.sendMessage(msg)
            }
        })
    }

    fun get(url: String, params: MutableMap<String, Any>, onSuccess: (responseInfo: String) -> Unit,
            onFailure: (error: Any, msg: String) -> Unit) {
        var random = Random(System.currentTimeMillis())
        params.put("_", random.nextInt().toString())
        val sb = buildGetString(url, params = params)
        this.get(sb.toString(), onSuccess, onFailure)
    }

    fun buildGetString(path: String, params: MutableMap<String, Any>): StringBuilder {
        var sb = StringBuilder(path)
        if (path.length > 0 && !sb.endsWith("&")) {
            sb.append("&")
        }
        for ((k,v) in params){
            sb.append(k).append("=").append(v).append("&")
        }
        if (sb.length > 1) {
            sb.deleteCharAt(sb.length - 1)
        }
        return sb
    }

}