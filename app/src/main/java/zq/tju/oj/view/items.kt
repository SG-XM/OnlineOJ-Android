package zq.tju.oj.view

import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.twt.zq.commons.common.CommonContext
import com.twt.zq.commons.extentions.Item
import com.twt.zq.commons.extentions.ItemController
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.oj_item_header.view.*
import kotlinx.android.synthetic.main.oj_item_record.view.*
import kotlinx.android.synthetic.main.quiz_item_error.view.*
import kotlinx.android.synthetic.main.qz_item_header.view.*
import lecho.lib.hellocharts.model.*
import lecho.lib.hellocharts.util.ChartUtils
import lecho.lib.hellocharts.view.LineChartView
import lecho.lib.hellocharts.view.PieChartView
import org.jetbrains.anko.backgroundDrawable
import org.jetbrains.anko.collections.forEachWithIndex
import org.jetbrains.anko.layoutInflater
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.textColor
import zq.tju.oj.R
import zq.tju.oj.service.ErrorRankBean
import zq.tju.oj.service.OJProcessBean
import java.text.DecimalFormat

class QuizErrorItem(val bean: ErrorRankBean) : Item {
    override fun areItemsTheSame(newItem: Item): Boolean {
        return (newItem as? QuizErrorItem)?.bean?.pid == this.bean.pid
    }

    override fun areContentsTheSame(newItem: Item): Boolean {
        return (newItem as? QuizErrorItem)?.bean?.pid == this.bean.pid
    }

    companion object : ItemController {
        override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
            val view = parent.context.layoutInflater.inflate(R.layout.quiz_item_error, parent, false)
            return ViewHolder(view, view.tv_qz_title, view.tv_qz_rate, view.tv_qz_cnt, view.tv_qz_type)
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, item: Item) {
            holder as ViewHolder
            item as QuizErrorItem
            holder.apply {
                title.text = item.bean.description
                rate.text =
                    if (item.bean.totalCount == 0) " - " else "Accuracy ${DecimalFormat("#0.00").format((item.bean.accuracy) * 100)}%"
                rate.textColor =
                    if (item.bean.totalCount > 0 && item.bean.accuracy > 0.5) view.resources.getColor(R.color.seagreen) else view.resources.getColor(
                        R.color.design_default_color_error
                    )
                cnt.text = "${item.bean.totalCount} submits"
                type.text = when (item.bean.typeId) {
                    2 -> "单选题"
                    3 -> "判断题"
                    else -> "多选题"
                }
                type.backgroundDrawable =
                    view.resources.getDrawable(if (item.bean.typeId == 2) R.drawable.textview_circle_ac else R.drawable.circle_primary_3)

                view.setOnClickListener {
                    view.context.startActivity<ProblemDetailActivity>("pid" to item.bean.pid.toString())
                }
            }
        }
    }

    class ViewHolder(val view: View, val title: TextView, val rate: TextView, val cnt: TextView, val type: TextView) :
        RecyclerView.ViewHolder(view)

    override val controller = Companion
}

class QuizHeaderItem(val nums: List<Float>) : Item {
    companion object : ItemController {
        override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
            val view = parent.context.layoutInflater.inflate(R.layout.qz_item_header, parent, false)
            return ViewHolder(view, view.chart_line_qz)
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, item: Item) {
            holder as ViewHolder
            item as QuizHeaderItem
            holder.apply {
                val data = LineChartData()
                val values = mutableListOf<PointValue>()
                item.nums.forEachWithIndex { i, fl ->
                    values.add(PointValue(i.toFloat(), fl))
                }
                val line: Line =
                    Line(values).setColor(ChartUtils.pickColor()).setHasLabels(true).setHasLabelsOnlyForSelected(false)
                val lines: MutableList<Line> = ArrayList()
                lines.add(line)
                data.lines = lines
                chart.isZoomEnabled = false
                chart.lineChartData = data
            }
        }
    }

    class ViewHolder(val view: View, val chart: LineChartView) :
        RecyclerView.ViewHolder(view)

    override val controller = Companion
}

class OJHeaderItem(val list: List<Float>) : Item {
    companion object : ItemController {
        override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
            val view = parent.context.layoutInflater.inflate(R.layout.oj_item_header, parent, false)
            return ViewHolder(view, view.chart_pie_oj)
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, item: Item) {
            holder as ViewHolder
            item as OJHeaderItem
            holder.apply {
                val values = mutableListOf<SliceValue>()
                item.list.forEachWithIndex { i, fl ->
                    values.add(SliceValue(fl, ChartUtils.pickColor()).apply {
                        setLabel(
                            when (i) {
                                0 -> "easy"
                                1 -> "medium"
                                else -> "hard"
                            }
                        )
                    })
                }
                val chartData = PieChartData(values)
                chartData.setHasLabels(true)
                chartData.setHasCenterCircle(true)
                chartData.setHasLabelsOnlyForSelected(false)
                chartData.setHasLabelsOutside(true)
                chart.pieChartData = chartData
            }
        }
    }

    class ViewHolder(val view: View, val chart: PieChartView) :
        RecyclerView.ViewHolder(view)

    override val controller = Companion
}

class OJRecordItem(val bean: OJProcessBean) : Item {
    override fun areItemsTheSame(newItem: Item): Boolean =
        (newItem as? OJRecordItem)?.bean?.id == this.bean.id//newItem is OJRecordItem

    override fun areContentsTheSame(newItem: Item): Boolean = (newItem as? OJRecordItem)?.bean?.id == this.bean.id

    companion object : ItemController {

        override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
            val view = parent.context.layoutInflater.inflate(R.layout.oj_item_record, parent, false)
            return ViewHolder(view, view.tv_oj_title, view.tv_oj_time, view.tv_oj_pass)
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, item: Item) {
            holder as ViewHolder
            item as OJRecordItem
            holder.apply {
                title.text = item.bean.title
                time.text =
                    if (item.bean.daysFromNow == 0) "Today" else if (item.bean.daysFromNow == 1) "Yesterday" else item.bean.daysFromNow.toString() + " days ago"
                pass.text = if (item.bean.pass) "Accepted" else "Failed"
                pass.backgroundDrawable =
                    pass.context.resources.getDrawable(if (item.bean.pass) R.drawable.textview_circle_ac else R.drawable.textview_circle_fa)
                view.setOnClickListener {
                    view.context.startActivity<SubmissionDetail>()
                }
            }
        }
    }

    class ViewHolder(val view: View, val title: TextView, val time: TextView, val pass: TextView) :
        RecyclerView.ViewHolder(view)

    override val controller = Companion
}
