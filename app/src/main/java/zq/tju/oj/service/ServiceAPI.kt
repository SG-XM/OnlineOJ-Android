package zq.tju.oj.service

import android.app.Activity
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.twt.zq.commons.common.*
import com.twt.zq.commons.extentions.Item
import com.twt.zq.commons.extentions.MyToast.toast
import com.twt.zq.commons.extentions.coroutineHandler
import com.twt.zq.commons.extentions.dealFetch
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.jetbrains.anko.startActivity
import retrofit2.http.*
import zq.tju.oj.LoginActivity
import zq.tju.oj.MainActivity
import zq.tju.oj.view.*

/**
 * Created by SGXM on 2020/3/24.
 */
interface ServiceAPI {

    @POST("/auth/login")
    @Headers("Content-type:application/json;charset=UTF-8")
    fun login(@Body body: RequestBody): Deferred<CommonBody<Any>>

    @GET("/code/sms")
    fun sms(@Query("mobile") mobile: String): Deferred<CommonBody<Any>>

    @GET("/room")
    fun room(): Deferred<CommonBody<MutableList<RoomBean>>>

    @GET("/api/back/quiz/rank/me")
    fun quizRank(): Deferred<CommonBody<QuizRankData>>

    @GET("/api/back/oj/rank/me")
    fun ojRank(): Deferred<CommonBody<OjRankData>>

    @GET("/api/back/oj/progress")
    fun ojProgress(@Query("pageNum") pageNum: Int): Deferred<CommonBody<List<OJProcessBean>>>

    @POST("/room/cover")
    @Multipart
    fun cover(@Part file: MultipartBody.Part): Deferred<CommonBody<MutableList<RoomBean>>>

    @GET("/api/back/quiz/rank/error")
    fun errorRank(@Query("pageNum") pageNum: Int): Deferred<CommonBody<List<ErrorRankBean>>>

    @GET("/api/back/quiz/rank/error/{pid}")
    fun quizDetail(@Path("pid") pid: String): Deferred<CommonBody<QuizDetailBean>>


    companion object : ServiceAPI by ServiceFactory()
}

object ServiceModel {
    val quizRankLiveData = MutableLiveData<QuizRankData>()
    val ojProcessLiveData = MutableLiveData<MutableList<Item>>()
    val quizErrorLiveData = MutableLiveData<MutableList<Item>>()
    val ojRankLiveData = MutableLiveData<OjRankData>()
    //    var token: String = "1ccc45bc-5404-4a70-9e6d-7dea10b9938b"
    val rooms = MutableLiveData<MutableList<RoomBean>>()
    val quizDetailLiveData = MutableLiveData<QuizDetailBean>()
    val isOjProcessLoading = MutableLiveData<Boolean>().apply { value = false }
    val isQuizErrorLoading = MutableLiveData<Boolean>().apply { value = false }

    fun login(body: RequestBody) {
        GlobalScope.launch(Dispatchers.Main + coroutineHandler) {
            ServiceAPI.login(body).await().dealOrNull {
                toast("登录成功")
                CommonContext.application.startActivity<MainActivity>()
            }
        }
    }

    fun quizDetail(pid: String) {
        GlobalScope.launch(Dispatchers.Main + coroutineHandler) {

            ServiceAPI.quizDetail(pid).await().dealOrNull {
                quizDetailLiveData.value = it
            }
        }
    }

    fun ojRank(ac: Activity) {
        GlobalScope.launch(Dispatchers.Main + coroutineHandler) {
            val data = ServiceAPI.ojRank().await()
            if (data.status == 10004) {
                toast("信息过期，请重新登录")
                CommonPreferences.token=""
                ac.startActivity<LoginActivity>()
                ac.finish()
                return@launch
            }
            data.dealOrNull {
                ojRankLiveData.value = it
            }
        }
    }

    fun ojProcess() {
        GlobalScope.launch(Dispatchers.Main + coroutineHandler) {
            ServiceAPI.ojProgress(1).await().deal {
                val list = mutableListOf<Item>()
                val ojdata = ojRankLiveData.value
                list.add(
                    OJHeaderItem(
                        mutableListOf(
                            ojdata!!.ojProblemPassCountDTO.easy.toFloat(),
                            ojdata.ojProblemPassCountDTO.medium.toFloat(),
                            ojdata.ojProblemPassCountDTO.hard.toFloat()
                        )
                    )
                )
                it.forEach { list.add(OJRecordItem(it)) }
                ojProcessLiveData.value = list
            }
        }.invokeOnCompletion {
            isOjProcessLoading.value = false
        }
    }

    fun quizError() {
        GlobalScope.launch(Dispatchers.Main + coroutineHandler) {

            ServiceAPI.errorRank(1).await().deal {
                val list = mutableListOf<Item>()
                list.add(QuizHeaderItem(quizRankLiveData.value?.scoreList?.map { it.toFloat() } ?: emptyList()))
                list.addAll(it.map { QuizErrorItem(it) })
                quizErrorLiveData.value = list
            }
        }.invokeOnCompletion {
            isQuizErrorLoading.value = false
        }
    }


    fun fetchMoreQuizError(page: Int) {
        GlobalScope.launch(Dispatchers.Main + coroutineHandler) {
            ServiceAPI.errorRank(page).await().deal {

                quizErrorLiveData.value =
                    quizErrorLiveData.value?.dealFetch(OjDetailActivity, it.map { QuizErrorItem(it) })
            }
        }.invokeOnCompletion {
            isQuizErrorLoading.value = false
        }
    }

    fun fetchMoreojProcess(page: Int) {
        GlobalScope.launch(Dispatchers.Main + coroutineHandler) {
            ServiceAPI.ojProgress(page).await().deal {
                val list = mutableListOf<Item>()
                it.forEach { list.add(OJRecordItem(it)) }
                ojProcessLiveData.value = ojProcessLiveData.value?.dealFetch(OjDetailActivity, list)
            }
        }.invokeOnCompletion {
            isOjProcessLoading.value = false
        }
    }

    fun quizRank(ac:Activity) {
        GlobalScope.launch(Dispatchers.Main + coroutineHandler) {
            val data = ServiceAPI.quizRank().await()
            if (data.status == 10004) {
                toast("信息过期，请重新登录")
                CommonPreferences.token=""
                ac.startActivity<LoginActivity>()
                ac.finish()
                return@launch
            }
            data.dealOrNull {
                quizRankLiveData.value = it
            }
        }
    }


    fun cover(file: MultipartBody.Part) {
        GlobalScope.launch(Dispatchers.Main + coroutineHandler) {
            ServiceAPI.cover(file).await().dealOrNull {
                Log.e("woggle", "sss")
            }
        }
    }

    fun getRoom() {
        GlobalScope.launch(Dispatchers.Main + coroutineHandler) {
            ServiceAPI.room().await().dealOrNull {
                rooms.value = it
            }
        }
    }

    fun send(mobile: String) {
        GlobalScope.launch(Dispatchers.Main + coroutineHandler) {
            ServiceAPI.sms(mobile).await().dealOrNull {
                toast("验证码已发送")
            }
        }
    }
}

data class QuizDetailBean(
    val description: String,
    val optionList: List<Option>
)

data class Option(
    val chooseRate: Double,
    val description: String,
    val optionId: Int
)

data class ErrorRankBean(
    val description: String,
    val errorRate: Double,
    val pid: Int,
    val totalCount: Int,
    val typeId: Int = 2
)

data class OJProcessBean(
    val id: Int = 0,
    val daysFromNow: Int,
    val pass: Boolean,
    val title: String
)

data class OjRankData(
    val ojProblemPassCountDTO: OjProblemPassCountDTO,
    val rank: Int,
    val totalUser: Int
)

data class OjProblemPassCountDTO(
    val easy: Int,
    val hard: Int,
    val medium: Int
)

data class QuizRankData(
    val rank: Int,
    val scoreList: List<Int>,
    val totalUser: Int
)

data class RoomBean(
    val active: Boolean,
    val cover: String,
    val createTime: String,
    val id: Int,
    val onlineUserCount: Int,
    val updateTime: String,
    val user: User
)

data class User(
    val authorities: Any,
    val createTime: String,
    val id: Int,
    val mobile: String,
    val updateTime: String,
    val username: String
)

data class LoginBean(
    val access_token: String,
    val expires_in: Int,
    val refresh_token: String,
    val scope: String,
    val token_type: String
)