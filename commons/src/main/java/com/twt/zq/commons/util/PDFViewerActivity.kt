package com.eshine.kslife.check

import android.Manifest
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import com.liulishuo.filedownloader.BaseDownloadTask
import com.liulishuo.filedownloader.FileDownloadListener
import com.liulishuo.filedownloader.FileDownloader
import com.twt.zq.commons.R
import com.twt.zq.commons.common.CommonContext
import com.twt.zq.commons.extentions.bindNonNull
import com.twt.zq.commons.util.exCheckPermission
import kotlinx.android.synthetic.main.activity_pdfviewer.*
import org.jetbrains.anko.toast
import java.io.*
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL

class PDFViewerActivity : AppCompatActivity() {

    private lateinit var url: String
    private lateinit var name: String
    private var path: String? = null

    companion object {
        val loading = MutableLiveData<Boolean>().apply {
            value = false
        }
        val finished = MutableLiveData<Boolean>().apply {
            value = false
        }
    }

    override fun onResume() {
        super.onResume()
        CommonContext.registerContext(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        com.twt.zq.commons.util.StatusBarUtil.setTextDark(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pdfviewer)
        finished.value = false
        loading.bindNonNull(this) {
            prgbar.visibility = if (it) View.VISIBLE else View.GONE
        }
        loading.value = true
        finished.bindNonNull(this) {
            if (it) {
                if (path.isNullOrBlank()) {
                    pdfView.fromFile(File(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + name))
                        .enableSwipe(true)
                        .enableDoubletap(true)
                        .load()
                } else {
                    try {
                        toast("load" + path)
                        if (
                            File(path).exists()) {
//                            toast("cunzai")
                        }
                        pdfView.fromFile(File(path))
                            .enableSwipe(true)
                            .enableDoubletap(true)
                            .load()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
//                webView.loadUrl("file:///android_asset/pdfjs/web/viewer.html?file=" + Environment.getExternalStorageDirectory()
//                    .getAbsolutePath() + File.separator + name)
        }
        url = intent.getStringExtra("url")
        name = intent.getStringExtra("name")
        path = intent.getStringExtra("path")
        if (!path.isNullOrBlank()) {
            finished.value = true
            loading.value = false
            return
        }
//        displayFromFile1(url,name)
//        webView.settings.apply {
//            setJavaScriptEnabled(true)
//            setAllowFileAccess(true)
//            setAllowFileAccessFromFileURLs(true)
//            setAllowUniversalAccessFromFileURLs(true)
////            setJavaScriptEnabled(true)
////            setPluginState(WebSettings.PluginState.ON)
//        }
//        val FileLoad = "cxstatus/"
        exCheckPermission(
            Manifest.permission.WRITE_EXTERNAL_STORAGE, 10
        ) {
            FileDownloader.setup(this)
            FileDownloader.getImpl()
                .create(url)
                .setPath(
                    Environment.getExternalStorageDirectory()
                        .getAbsolutePath() + File.separator + name
                ).setForceReDownload(true).setListener(
                    object : FileDownloadListener() {
                        override fun warn(task: BaseDownloadTask?) {
                            Log.e("woggle", "warn")
                        }

                        override fun completed(task: BaseDownloadTask?) {
                            Log.e("woggle", "com")
                            loading.value = false
                            finished.value = true
                        }

                        override fun pending(task: BaseDownloadTask?, soFarBytes: Int, totalBytes: Int) {
                            Log.e("woggle", "pend")
                        }

                        override fun error(task: BaseDownloadTask?, e: Throwable?) {
                            loading.value = false
                            Log.e("woggle", "err")
                        }

                        override fun progress(task: BaseDownloadTask?, soFarBytes: Int, totalBytes: Int) {
                            Log.e("woggle", "pro")
                        }

                        override fun paused(task: BaseDownloadTask?, soFarBytes: Int, totalBytes: Int) {
                            Log.e("woggle", "pas")
                        }

                    }
                ).start()
        }
//        Environment.getExternalStorageDirectory()
//        loading.value = true
//        //判断SDcard是否存在并且可读写
//        thread {
//            createFile(this.getFilesDir().absolutePath + name)
//            if (downloadFile(this.getFilesDir().absolutePath, name, url) == 0) {
//                loading.postValue(false)
//                finished.postValue(true)
//            }
//        }
    }

    private fun downloadFile(dirName: String, fileName: String, urlStr: String): Int {
        var output: OutputStream? = null
        var input: InputStream? = null
        try {
            //将字符串形式的path,转换成一个url
            val url = URL(urlStr)
            //得到url之后，将要开始连接网络，以为是连接网络的具体代码
            //首先，实例化一个HTTP连接对象conn
            val conn = url.openConnection() as HttpURLConnection
            //定义请求方式为GET，其中GET的大小写不要搞错了。
            conn.setRequestMethod("GET")
            //定义请求时间，在ANDROID中最好是不好超过10秒。否则将被系统回收。
            conn.setConnectTimeout(6 * 1000)
            //请求成功之后，服务器会返回一个响应码。如果是GET方式请求，服务器返回的响应码是200，post请求服务器返回的响应码是206（貌似）。
            if (conn.getResponseCode() == 200) {
                //返回码为真
                //从服务器传递过来数据，是一个输入的动作。定义一个输入流，获取从服务器返回的数据
                input = conn.getInputStream()
                val file = createFile(dirName + fileName)
                output = FileOutputStream(file)
                //读取大文件
                var buffer = ByteArray(1024)
                //记录读取内容
                var n = input.read(buffer)
                //写入文件
                while (n != -1) {
                    output.write(buffer, 0, n)
                    n = input.read(buffer)
                    Log.e("woggle", n.toString())
                }
            }
            output?.flush()
            input?.close()
        } catch (e: MalformedURLException) {
            e.printStackTrace()
            Log.e("woggle", "fail")
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e("woggle", "fail")
        } finally {
            try {
                output?.close()
                Log.e("woggle", "suss")
                //  System.out.println("success")
                return 0
            } catch (e: IOException) {
                Log.e("woggle", "fail")
                e?.printStackTrace()
            }
        }
        return 1
    }


    /**
     * 在SD卡的指定目录上创建文件
     *
     * @param fileName
     */
    public fun createFile(fileName: String): File {
        val file = File(fileName)
        try {
            file.createNewFile()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return file
    }

}
