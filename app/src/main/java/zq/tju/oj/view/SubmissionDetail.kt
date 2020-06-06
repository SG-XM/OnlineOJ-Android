package zq.tju.oj.view

import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.eudycontreras.calendarheatmaplibrary.framework.data.Date
import com.eudycontreras.calendarheatmaplibrary.framework.data.HeatMapData
import com.eudycontreras.calendarheatmaplibrary.framework.data.TimeSpan
import com.twt.zq.commons.extentions.bindNonNull
import kotlinx.android.synthetic.main.activity_submission_detail.*
import lecho.lib.hellocharts.gesture.ZoomType
import lecho.lib.hellocharts.listener.ViewportChangeListener
import lecho.lib.hellocharts.model.*
import lecho.lib.hellocharts.util.ChartUtils
import lecho.lib.hellocharts.view.ColumnChartView
import lecho.lib.hellocharts.view.PreviewColumnChartView
import org.jetbrains.anko.collections.forEachWithIndex
import org.jetbrains.anko.textColor
import zq.tju.oj.R
import zq.tju.oj.model.SomeViewModel
import zq.tju.oj.service.ServiceModel


class SubmissionDetail : AppCompatActivity() {

    private lateinit var rid: String
    private var mColumnChartView: ColumnChartView? = null
    private var mPreColumnChartView: PreviewColumnChartView? = null

    /*========== 数据相关 ==========*/
    private var mChartData: ColumnChartData? = null
    private var mPreChartData: ColumnChartData? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_submission_detail)
        rid = intent.getStringExtra("rid") ?: ""
        ServiceModel.submissionDetail.value?.pass = false
        initView()
        initListener()
        initData()
    }


    private fun initView() {
        mColumnChartView = findViewById(R.id.ccv_pre_main)
        mPreColumnChartView = findViewById(R.id.pccv_pre_main)
    }

    private fun initData() {
        ServiceModel.apply {
            submissionDetail(rid)
            submissionDetail.bindNonNull(this@SubmissionDetail) {
                card_err.visibility = View.GONE
                tv_testtime_val.visibility = View.VISIBLE
                tv_testtime.visibility = View.VISIBLE
                tv_testmem_val.visibility = View.VISIBLE
                tv_testmem.visibility = View.VISIBLE
                tv_title.text = "${it.title}\n提交记录"
                tv_testcnt.text = "${it.passTestExampleCount}/${it.totalTestExampleCount} 个通过测试用例"
                tv_testtime_val.text = "${it.myTime} ms"
                tv_teststat_val.text = if (it.pass) "通过" else "失败"
                tv_teststat_val.textColor = if (it.pass) getColor(R.color.colorAC2) else getColor(R.color.colorFA2)
                tv_testdate_val.text = it.submitDate
                if (it.pass) {
                    setAllDatas(
                        it.timeRankList.map { it.value.toFloat() },
                        it.myRankPercent.toFloat(),
                        it.timeRankList.map { it.key.toFloat() })
                    mColumnChartView!!.columnChartData = mChartData //设置选中区内容
                    mPreColumnChartView!!.columnChartData = mPreChartData //设置预览区内容
                    mColumnChartView!!.isZoomEnabled = false //禁用缩放
                    mColumnChartView!!.isScrollEnabled = false //禁用滚动
                    previewX() //初识只能X方向滑动
                } else {
                    card_err.visibility = View.VISIBLE
                    tv_testtime.visibility = View.GONE
                    tv_testtime_val.visibility = View.GONE
                    tv_testmem.visibility = View.GONE
                    tv_testmem_val.visibility = View.GONE
                    tv_testres.text = it.info
                    tv_testres.textColor = getColor(R.color.colorFA2)
                }
            }
        }

    }

    fun initListener() {
        mPreColumnChartView!!.setViewportChangeListener(ViewportListener())
    }


    /**
     * 设置选区和预览区的所有数据
     */
    private fun setAllDatas(value: List<Float>, mIndex: Float, xs: List<Float>) {
        val columns: MutableList<Column> = ArrayList()
        var values: MutableList<SubcolumnValue?>
        //循环给每个列设置不同的随机值
        value.filterIndexed { index, fl ->
            values = ArrayList()
            values.add(SubcolumnValue(fl, ChartUtils.pickColor()))
            columns.add(Column(values).apply { setHasLabelsOnlyForSelected(true) })
        }
        //设置一些其他属性
        mChartData = ColumnChartData(columns)
        mChartData!!.axisXBottom = Axis().apply {
            val list = mutableListOf<AxisValue>()
            xs.forEachWithIndex { i, fl ->
                list.addAll(xs.map { AxisValue(i.toFloat()).setLabel(fl.toString()) })
            }
            setValues(list)
            textColor = Color.BLACK
//            setHasTiltedLabels(true)
            setName("Runtime (ms)")
        }

        mChartData!!.axisYLeft = Axis().setHasLines(true).apply { textColor = Color.BLACK }
        //预览区数据相同
        mPreChartData = ColumnChartData(mChartData)
        //所有的预览区列都变成灰色 好看一点
        mPreChartData!!.axisXBottom = null
        mPreChartData!!.axisYLeft = Axis()
        mPreChartData!!.columns.forEachIndexed { index, column ->
            for (value in column.values) {
                if (value.value == mIndex) {
                    value.color = Color.BLACK
                } else
                    value.color = ChartUtils.DEFAULT_DARKEN_COLOR

            }
        }
    }

//    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
//        menuInflater.inflate(R.menu.menu_preview_column_chart, menu)
//        return true
//    }

//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        when (item.getItemId()) {
//            R.id.menu_pre_column_reset -> {
//                setAllDatas() //重新设置
//                previewX() //只在X方向预览
//                return true
//            }
//            R.id.menu_pre_column_both -> {
//                previewXY() //X/Y方向都可预览
//                return true
//            }
//            R.id.menu_pre_column_x -> {
//                previewX() //只在X方向预览
//                return true
//            }
//            R.id.menu_pre_column_y -> {
//                previewY() //只在Y方向预览
//                return true
//            }
//            R.id.menu_pre_column_change_color -> {
//                changePreviewBoxColor() //改变预览区选框颜色
//                return true
//            }
//        }
//        return super.onOptionsItemSelected(item)
//    }

    /**
     * X/Y方向都能预览
     */
    private fun previewXY() { /*========== 类似于 PreviewLine Chart ==========*/
        val tempViewport = Viewport(mColumnChartView!!.maximumViewport)
        val dx = tempViewport.width() / 4
        val dy = tempViewport.height() / 4
        tempViewport.inset(dx, dy)
        mPreColumnChartView!!.setCurrentViewportWithAnimation(tempViewport)
        mPreColumnChartView!!.zoomType = ZoomType.HORIZONTAL_AND_VERTICAL
    }

    /**
     * 只在X方向预览
     */
    private fun previewX() { /*========== 类似于 PreviewLine Chart ==========*/
        val tempViewport = Viewport(mColumnChartView!!.maximumViewport)
        val dx = tempViewport.width() / 4
        tempViewport.inset(dx, 0f)
        mPreColumnChartView!!.setCurrentViewportWithAnimation(tempViewport)
        mPreColumnChartView!!.zoomType = ZoomType.HORIZONTAL
    }

    /**
     * 只在Y方向预览
     */
    private fun previewY() { /*========== 类似于 PreviewLine Chart ==========*/
        val tempViewport = Viewport(mColumnChartView!!.maximumViewport)
        val dy = tempViewport.height() / 4
        tempViewport.inset(0f, dy)
        mPreColumnChartView!!.setCurrentViewportWithAnimation(tempViewport)
        mPreColumnChartView!!.zoomType = ZoomType.VERTICAL
    }

    /**
     * 改变预览区选框颜色
     */
    private fun changePreviewBoxColor() {
        var color = ChartUtils.pickColor()
        //必须与当前显示颜色不同
        while (color == mPreColumnChartView!!.previewColor) {
            color = ChartUtils.pickColor()
        }
        mPreColumnChartView!!.previewColor = color //重新设置颜色
    }

    /**
     * 预览区滑动监听
     */
    private inner class ViewportListener : ViewportChangeListener {
        override fun onViewportChanged(newViewport: Viewport) { // 这里切记不要使用动画，因为预览图是不需要动画的
            mColumnChartView?.currentViewport = newViewport //直接设置当前窗口图表
        }
    }
}
