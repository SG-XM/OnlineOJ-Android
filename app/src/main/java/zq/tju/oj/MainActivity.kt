package zq.tju.oj

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.updateLayoutParams
import com.eudycontreras.calendarheatmaplibrary.framework.CalHeatMapView
import com.eudycontreras.calendarheatmaplibrary.framework.data.HeatMapData
import com.eudycontreras.calendarheatmaplibrary.framework.data.HeatMapOptions
import com.eudycontreras.calendarheatmaplibrary.framework.data.TimeSpan
import com.twt.zq.commons.extentions.bindNonNull
import kotlinx.android.synthetic.main.activity_main.*
import lecho.lib.hellocharts.model.*
import lecho.lib.hellocharts.util.ChartUtils
import org.jetbrains.anko.collections.forEachWithIndex
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper
import zq.tju.oj.model.SomeViewModel
import zq.tju.oj.service.ServiceModel
import zq.tju.oj.view.OjDetailActivity
import zq.tju.oj.view.QuizDetailActivity
import kotlin.random.Random


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        card_oj.setOnClickListener {
            startActivity<OjDetailActivity>()
        }
        card_option.setOnClickListener {
            startActivity<QuizDetailActivity>()
        }
        initData()
    }

    private fun initQuiz(nums: List<Float>) {
        val data = LineChartData()
        val values = mutableListOf<PointValue>()
        nums.forEachWithIndex { i, fl ->
            values.add(PointValue(i.toFloat(), fl))
        }
        val line: Line =
            Line(values).setColor(ChartUtils.pickColor()).setHasLabels(true).setHasLabelsOnlyForSelected(false)
        val lines: MutableList<Line> = ArrayList()
        lines.add(line)
        data.lines = lines
        chart_op.isZoomEnabled = false
        chart_op.lineChartData = data

    }

    private fun initOj(nums: List<Float>) {
        val columnData = ColumnChartData()
        val columns = mutableListOf<Column>()
        nums.forEach {
            columns.add(Column(listOf(SubcolumnValue(it, ChartUtils.pickColor()))).apply {
                setHasLabels(true)
                setHasLabelsOnlyForSelected(false)
            })
        }
        columnData.setColumns(columns)
        chart_oj.columnChartData = columnData
    }

    private fun initData() {
        ServiceModel.apply {
            ojRank(this@MainActivity)
            quizRank(this@MainActivity)
            submitCal()
            ojRankLiveData.bindNonNull(this@MainActivity) {
                tv_title3.text = "Rank: #${it.rank}/${it.totalUser}"
                initOj(
                    mutableListOf(
                        it.ojProblemPassCountDTO.easy.toFloat(),
                        it.ojProblemPassCountDTO.medium.toFloat(),
                        it.ojProblemPassCountDTO.hard.toFloat()
                    )
                )
            }
            quizRankLiveData.bindNonNull(this@MainActivity) {
                tv_title6.text = "Rank: #${it.rank}/${it.totalUser}"
                initQuiz(it.scoreList.map { it.toFloat() })
            }
            submitCalLiveData.bindNonNull(this@MainActivity) {
                toast("ex")
                val stub = CalHeatMapView(this@MainActivity)

                stub.apply {
                    setCalHeatMapData(
                        HeatMapData(
                            it, HeatMapOptions()
                        )
//                        SomeViewModel().demoData1
                    )
                    setCellColorEmpty(Color.WHITE)
                    setCellElevation(4f)
                    val i = Random(System.currentTimeMillis()).nextInt()
                    setCellColorMax(colors[i % colors.size].second)
                    setCellColorMin(colors[i % colors.size].first)
//            setCellInfoView(CellInfo)
                    isHapticFeedbackEnabled = false
                    setShowCellDayText(true)
                    setShowLegend(true)
                }
                scroll_h.addView(stub, 200, 600)
                scroll_h.post {
                    scroll_h.smoothScrollTo(stub.measuredWidth / 2, 0)
                }
            }
        }
    }

    override fun attachBaseContext(newBase: Context?) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase))
    }

    val colors = listOf(
        Color.parseColor("#a3cf62") to Color.parseColor("#005831"),
        Color.parseColor("#f8aba6") to Color.parseColor("#d71345"),
        Color.parseColor("#afb4db") to Color.parseColor("#8552a1"),
        Color.parseColor("#fcf16e") to Color.parseColor("#e0861a"),
        Color.parseColor("#cd9a5b") to Color.parseColor("#69541b")
    )
}
