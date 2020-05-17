package zq.tju.oj.service

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.twt.zq.commons.common.*
import com.twt.zq.commons.extentions.MyToast.toast
import com.twt.zq.commons.extentions.coroutineHandler
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.jetbrains.anko.startActivity
import retrofit2.http.*
import zq.tju.oj.MainActivity

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

    @GET(" /api/back/oj/rank/me")
    fun ojRank(): Deferred<CommonBody<OjRankData>>


    @POST("/room/cover")
    @Multipart
    fun cover(@Part file: MultipartBody.Part): Deferred<CommonBody<MutableList<RoomBean>>>

    companion object : ServiceAPI by ServiceFactory()
}

object ServiceModel {
    val quizRankLiveData = MutableLiveData<QuizRankData>()
    val ojRankLiveData = MutableLiveData<OjRankData>()
//    var token: String = "1ccc45bc-5404-4a70-9e6d-7dea10b9938b"
    val rooms = MutableLiveData<MutableList<RoomBean>>()
    fun login(body: RequestBody) {
        GlobalScope.launch(Dispatchers.Main + coroutineHandler) {
            ServiceAPI.login(body).await().dealOrNull {
                toast("登录成功")
                CommonContext.application.startActivity<MainActivity>()
            }
        }
    }

    fun ojRank() {
        GlobalScope.launch(Dispatchers.Main + coroutineHandler) {
            ServiceAPI.ojRank().await().dealOrNull {
                ojRankLiveData.value = it
            }
        }
    }
    fun quizRank() {
        GlobalScope.launch(Dispatchers.Main + coroutineHandler) {
            ServiceAPI.quizRank().await().dealOrNull {
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