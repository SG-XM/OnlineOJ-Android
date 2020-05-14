package zq.tju.oj


//import com.iflytek.cloud.SpeechConstant
//import com.iflytek.cloud.SpeechUtility
import android.app.Application
import com.orhanobut.hawk.Hawk
import com.twt.zq.commons.common.CommonContext


/**
 * Created by SGXM on 2018/7/30.
 */

//在Manifest中注册，不需要手动创建
class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()


        Hawk.init(this).build()
        CommonContext.apply {
            registerActivity("login", LoginActivity::class.java)
        }

    }
}

