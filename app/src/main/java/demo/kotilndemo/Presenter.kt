package demo.kotilndemo

import demo.kotilndemo.OrgBean

/**
 * Created by zhangyao on 17/6/2.
 */
class Presenter(val action:(bean: OrgBean)->Unit) {
    fun onSaveClick(bean: OrgBean){
        action(bean)
    }
}