package demo.kotilndemo

import android.databinding.BaseObservable
import android.databinding.Bindable
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import com.andview.refreshview.XRefreshView
import demo.kotilndemo.common.OkhttpUtil
import demo.kotilndemo.common.ResultBean
import demo.kotilndemo.databinding.ActivityMainBinding
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.internal.schedulers.IoScheduler
import io.reactivex.rxkotlin.subscribeBy
import kotlinx.android.synthetic.main.activity_main.*
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query


data class OrgBean(val org_id: Int, val name: String, val image: String, val url: String)
class UserBean : BaseObservable {

    var name: String = ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.name)
        }
        @Bindable
        get() = field
    var age: Int = 0
        set(value) {
            field = age
            notifyPropertyChanged(BR.age)
        }
        @Bindable
        get() = field

    constructor(name: String, age: Int) {
        this.name = name
        this.age = age
    }
}

class MainActivity : AppCompatActivity() {
    val data by lazy { mutableListOf<OrgBean>() }
    val presenter by lazy {
        Presenter(action = {
            user.name = it.name
        })
    }
    val adapter by lazy {
        DemoAdapter(dataVariable = BR.data, presenterVariable = BR.presenter,
                layoutId = R.layout.activity_main_item, presenter = presenter, data = data)
    }
    val user by lazy {
        UserBean("测试", 12)
    }

    lateinit var test: () -> String

    private var service = Retrofit.Builder()
            .baseUrl("http://dev.benbun.com/web/proj/meishubao/")
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(MavenSearchService::class.java)


    interface MavenSearchService {
        @GET("index.php?g=App&m=Organization&a=getOrgListApi")
        fun getOrgListApi(@Query("type") type: String): Observable<ResultBean<List<OrgBean>>>
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding: ActivityMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.user = user
        initView()
        setLinsenter()
        test = test(list = list)
    }

    private fun setLinsenter() {
        activity_main_xrefreshview.setXRefreshViewListener(object : XRefreshView.SimpleXRefreshListener() {
            override fun onRefresh(isPullDown: Boolean) {
                super.onRefresh(isPullDown)
                val map = mutableMapOf<String, Any>()
                map.put("type", "1")
                OkhttpUtil.get("http://dev.benbun.com/web/proj/meishubao/index.php?g=App&m=Organization&a=getOrgListApi"
                        , map, onFailure = { any: Any, s: String ->

                }, onSuccess = {
                    Log.e("###",it.toString())
                })
                getOrgList() {
                    data.addAll(it)
                    adapter.notifyDataSetChanged()

                }
            }
        })
    }

    val list = mutableListOf<String>("闭包", "测试", "开始", "1", "2", "3", "结束")

    private fun test(list: MutableList<String>): () -> String {
        var n = 0
        val action = {
            list[n++.apply { if (n == list.size) n = 0 }]
        }
        return action
    }

    private fun initView() {
//        activity_main_recyclerview.adapter = adapter
//        val manager = LinearLayoutManager(this@MainActivity)
//        manager.orientation = LinearLayoutManager.VERTICAL
//        activity_main_recyclerview.layoutManager = manager
//
//        with (activity_main_recyclerview) {
//            adapter = this@MainActivity.adapter
//            layoutManager = LinearLayoutManager(this@MainActivity).apply {
//                orientation = LinearLayoutManager.VERTICAL
//        }

        activity_main_recyclerview.apply {
            adapter = this@MainActivity.adapter
            layoutManager = LinearLayoutManager(this@MainActivity).apply {
                orientation = LinearLayoutManager.VERTICAL
            }
        }

    }


    private fun getOrgList(action: (List<OrgBean>) -> Unit) {

        service.getOrgListApi("1")
                .subscribeOn(IoScheduler())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeBy(
                        onNext = {
                            Log.e("##", it.toString())
                            action(it.data)
                        },
                        onError = {
                            Log.e("##", it.message)
                        },
                        onComplete = {
                            user.name = test.invoke()
                            activity_main_xrefreshview.stopLoadMore()
                            activity_main_xrefreshview.stopRefresh()
                        }
                )
    }
}

