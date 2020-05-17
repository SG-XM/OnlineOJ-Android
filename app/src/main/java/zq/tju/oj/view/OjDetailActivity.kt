package zq.tju.oj.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import com.ajguan.library.EasyRefreshLayout
import com.ajguan.library.LoadModel
import com.twt.zq.commons.extentions.ItemAdapter
import com.twt.zq.commons.extentions.ItemManager
import com.twt.zq.commons.extentions.bindNonNull
import com.twt.zq.commons.extentions.canLoadMore
import kotlinx.android.synthetic.main.activity_oj_detail.*
import zq.tju.oj.R
import zq.tju.oj.service.ServiceModel

class OjDetailActivity : AppCompatActivity() {

    private val itm = ItemManager()

    companion object : canLoadMore {
        override var hasMore = MutableLiveData<Boolean>().apply { value = true }
        override var page: Int = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_oj_detail)
        initView()
        initData()
    }

    fun initView() {
        page = 1
        hasMore.value = true
        rec_oj.adapter = ItemAdapter(itm)
        rec_oj.layoutManager = LinearLayoutManager(this@OjDetailActivity)
        easy_oj.config(ServiceModel.isOjProcessLoading, this)
        easy_oj.addEasyEvent(object : EasyRefreshLayout.EasyEvent {
            override fun onRefreshing() {
                page = 1
                hasMore.value=true
                ServiceModel.isOjProcessLoading.value = true
                ServiceModel.ojProcess()
            }

            override fun onLoadMore() {
                if (hasMore.value == true) {
                    ServiceModel.isOjProcessLoading.value = true
                    ServiceModel.fetchMoreojProcess(++page)
                } else {
                    easy_oj.loadMoreComplete()
                }
            }
        })
    }

    fun initData() {
        ServiceModel.apply {
            ojProcess()
            ojProcessLiveData.bindNonNull(this@OjDetailActivity) {
                itm.refreshAll(it)
            }
        }

    }
}

fun EasyRefreshLayout.config(
    liveData: MutableLiveData<Boolean>,
    lifecycleOwner: LifecycleOwner,
    hasMore: MutableLiveData<Boolean>? = null
) {
    liveData.bindNonNull(lifecycleOwner) {
        if (!it) {
            this.refreshComplete()
            this.loadMoreComplete()
        }
    }
    hasMore?.bindNonNull(lifecycleOwner) {
        //this.refreshComplete()
        this.loadMoreComplete()
        this.loadMoreModel = if (it) LoadModel.COMMON_MODEL else LoadModel.NONE
    }
}

fun ProgressBar.config(liveData: MutableLiveData<Boolean>, lifecycleOwner: LifecycleOwner) {
    liveData.bindNonNull(lifecycleOwner) {
        if (!it) {
            this.visibility = View.GONE
        } else {
            this.visibility = View.VISIBLE
        }

    }
}