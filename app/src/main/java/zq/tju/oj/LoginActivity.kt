package zq.tju.oj

import android.content.Context
import android.os.Bundle
import android.os.CountDownTimer
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import com.twt.zq.commons.common.CommonContext
import com.twt.zq.commons.common.CommonPreferences
import com.twt.zq.commons.extentions.bindNonNull
import kotlinx.android.synthetic.main.activity_login.*
import okhttp3.MediaType
import okhttp3.RequestBody
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast
import org.json.JSONObject
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper
import zq.tju.oj.service.ServiceModel


class LoginActivity : AppCompatActivity() {
    companion object {
        val vercodeLiveData = MutableLiveData<Boolean>().apply { value = true }
        val vercodetimeLiveData = MutableLiveData<Int>()


    }

    val forCounter = object : CountDownTimer(60 * 1000, 1000) {
        override fun onFinish() {
            vercodeLiveData.value = true
        }

        override fun onTick(millisUntilFinished: Long) {
            vercodetimeLiveData.value = millisUntilFinished.toInt() / 1000
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        CommonContext.registerContext(this)
        if (CommonPreferences.token != "") {
            CommonContext.application.startActivity<MainActivity>()
            finish()
        }
        bt_login.setOnClickListener {

            if (code_input.text.isEmpty()) {
                toast("请输入验证码")
                return@setOnClickListener
            }
            val obj = JSONObject()
            obj.put("email", account_input.text)
            obj.put("password", code_input.text)
            obj.put("rememberMe", true)
            val body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), obj.toString())

            ServiceModel.login(body)
        }
        btn_send.setOnClickListener {
            if (account_input.text.isEmpty()) {
                toast("请输入手机号")
                return@setOnClickListener
            }
            forCounter.start()
            ServiceModel.send(account_input.text.toString())
            btn_send.isEnabled = false
        }
        vercodeLiveData.bindNonNull(this) {
            if (it) {
                btn_send.isEnabled = true
            }
        }
        vercodetimeLiveData.bindNonNull(this) {
            btn_send.text = "请等待($it)"
        }
    }

    override fun attachBaseContext(newBase: Context?) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase))
    }
}
