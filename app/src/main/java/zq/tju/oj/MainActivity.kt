package zq.tju.oj

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.twt.zq.commons.extentions.bindNonNull
import kotlinx.android.synthetic.main.activity_main.*
import lecho.lib.hellocharts.model.*
import lecho.lib.hellocharts.util.ChartUtils
import org.jetbrains.anko.collections.forEachWithIndex
import zq.tju.oj.service.ServiceModel


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
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
//        chart_oj.isZoomEnabled = false
        chart_oj.columnChartData = columnData
    }

    private fun initData() {

        ServiceModel.apply {
            ojRank()
            quizRank()
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

        }

//        initOj(listOf(20f, 35f, 35f))
//        val list = mutableListOf<Float>()
//        val rand = Random(System.currentTimeMillis())
//        for (i in 0..4) {
//            list.add(rand.nextInt(10, 50).toFloat())
//        }

    }
}
