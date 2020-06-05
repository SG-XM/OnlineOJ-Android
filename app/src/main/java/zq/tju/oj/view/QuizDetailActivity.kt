package zq.tju.oj.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import com.ajguan.library.EasyRefreshLayout
import com.twt.zq.commons.extentions.ItemAdapter
import com.twt.zq.commons.extentions.ItemManager
import com.twt.zq.commons.extentions.bindNonNull
import com.twt.zq.commons.extentions.canLoadMore
import kotlinx.android.synthetic.main.activity_quiz_detail.*
import zq.tju.oj.R
import zq.tju.oj.service.ServiceModel

class QuizDetailActivity : AppCompatActivity() {
    private val itm = ItemManager()

    companion object : canLoadMore {
        override var hasMore = MutableLiveData<Boolean>().apply { value = true }
        override var page: Int = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quiz_detail)
        initView()
        initData()
    }

    fun initView() {
        page = 1
        hasMore.value = true
        rec_qz.adapter = ItemAdapter(itm)
        rec_qz.layoutManager = LinearLayoutManager(this)
        easy_qz.config(ServiceModel.isQuizErrorLoading, this)
        easy_qz.addEasyEvent(object : EasyRefreshLayout.EasyEvent {
            override fun onRefreshing() {
                page = 1
                hasMore.value = true
                ServiceModel.isQuizErrorLoading.value = true
                ServiceModel.quizRank(this@QuizDetailActivity)
                ServiceModel.quizError()
            }

            override fun onLoadMore() {
                if (hasMore.value == true) {
                    ServiceModel.isQuizErrorLoading.value = true
                    ServiceModel.fetchMoreQuizError(++page)
                } else {
                    easy_qz.loadMoreComplete()
                }
            }
        })
    }

    fun initData() {
        ServiceModel.apply {
            quizError()
            quizErrorLiveData.bindNonNull(this@QuizDetailActivity) {
                itm.refreshAll(it)
            }
        }

    }
}

