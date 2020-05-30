package zq.tju.oj.view

import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.widget.LinearLayout
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView
import androidx.core.view.marginTop
import com.twt.zq.commons.extentions.bindNonNull
import kotlinx.android.synthetic.main.activity_main.*
import zq.tju.oj.R

import kotlinx.android.synthetic.main.activity_problem_detail.*
import lecho.lib.hellocharts.model.Column
import lecho.lib.hellocharts.model.ColumnChartData
import lecho.lib.hellocharts.model.SubcolumnValue
import lecho.lib.hellocharts.util.ChartUtils
import lecho.lib.hellocharts.view.ColumnChartView
import org.jetbrains.anko.*
import org.jetbrains.anko.collections.forEachWithIndex
import zq.tju.oj.service.ServiceModel

class ProblemDetailActivity : AppCompatActivity() {

    lateinit var pid: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_problem_detail)
        pid = intent.getStringExtra("pid")
        println(pid)
        initData()
    }

    private fun initData() {
        ServiceModel.apply {
            quizDetail(pid)
            quizDetailLiveData.bindNonNull(this@ProblemDetailActivity) {
                val columnData = ColumnChartData()
                val columns = mutableListOf<Column>()
                it.optionList.forEach {
                    columns.add(
                        Column(
                            listOf(
                                SubcolumnValue(
                                    (it.chooseRate).toFloat(),
                                    ChartUtils.pickColor()
                                )
                            )
                        ).apply {
                            setHasLabels(true)
                            setHasLabelsOnlyForSelected(false)
                        })
                }
                columnData.setColumns(columns)

                layout_container.removeAllViews()
                layout_container.verticalLayout {
                    gravity = Gravity.CENTER_HORIZONTAL
                    //addView(CardView(this@ProblemDetailActivity).apply {
                    textView {
                        text = "题目： " + it.description
                        textColor = Color.BLACK
                        textSize = 19f
                    }.lparams {
                        setMargins(0, 0, 0, 24)
                    }
                    it.optionList.forEachWithIndex { i, option ->
                        addView(CardView(this@ProblemDetailActivity).apply {
                            verticalLayout {
                                textView {
                                    textSize = 17f
                                    text = ('A' + i).toString() + ". " + option.description
                                }.lparams {
                                    setMargins(24, 16, 24, 16)
                                }
                            }
                            elevation = 11f
                            radius = 14f
                            setPadding(12, 8, 12, 8)
//                            setCardBackgroundColor(resources.getColor(R.color.darkGrey))
                        }.lparams {

                            width = -1

                            setMargins(8, 16, 8, 24)
                        })

                    }
                    addView(ColumnChartView(this@ProblemDetailActivity).apply {
                        columnChartData = columnData
                        layoutParams = LinearLayout.LayoutParams(-1, 350).apply {
                            setMargins(0, 48, 0, 0)
                        }

                    })
                }
            }
        }
    }
}
