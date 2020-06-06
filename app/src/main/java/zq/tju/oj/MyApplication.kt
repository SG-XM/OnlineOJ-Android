package zq.tju.oj


//import com.iflytek.cloud.SpeechConstant
//import com.iflytek.cloud.SpeechUtility
import android.app.Application
import com.orhanobut.hawk.Hawk
import com.twt.zq.commons.common.CommonContext
import uk.co.chrisjenx.calligraphy.CalligraphyConfig


/**
 * Created by SGXM on 2018/7/30.
 */

//在Manifest中注册，不需要手动创建
class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        CalligraphyConfig.initDefault(
            CalligraphyConfig.Builder()
                .setDefaultFontPath("")
                .setFontAttrId(R.attr.fontPath)
                .build()
        )

        Hawk.init(this).build()
        CommonContext.apply {
            registerActivity("login", LoginActivity::class.java)
        }

    }
}

