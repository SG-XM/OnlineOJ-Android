package zq.tju.oj.view

import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.twt.zq.commons.extentions.Item
import com.twt.zq.commons.extentions.ItemController
import kotlinx.android.synthetic.main.oj_item_header.view.*
import kotlinx.android.synthetic.main.oj_item_record.view.*
import lecho.lib.hellocharts.model.PieChartData
import lecho.lib.hellocharts.model.SliceValue
import lecho.lib.hellocharts.util.ChartUtils
import lecho.lib.hellocharts.view.PieChartView
import org.jetbrains.anko.backgroundDrawable
import org.jetbrains.anko.collections.forEachWithIndex
import org.jetbrains.anko.layoutInflater
import zq.tju.oj.R
import zq.tju.oj.service.OJProcessBean

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
    override fun areItemsTheSame(newItem: Item): Boolean = (newItem as? OJRecordItem)?.bean?.id == this.bean.id//newItem is OJRecordItem
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
            }
        }
    }

    class ViewHolder(val view: View, val title: TextView, val time: TextView, val pass: TextView) :
        RecyclerView.ViewHolder(view)

    override val controller = Companion
}
