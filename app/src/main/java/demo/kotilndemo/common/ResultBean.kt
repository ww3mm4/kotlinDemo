package demo.kotilndemo.common

/**
 * Created by zhangyao on 17/6/2.
 */
data class ResultBean<T>(val code: Int, val trace: String, val msg: String, val data: T)
