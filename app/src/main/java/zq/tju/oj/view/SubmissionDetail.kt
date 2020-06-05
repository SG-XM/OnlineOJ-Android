package zq.tju.oj.view

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_submission_detail.*
import zq.tju.oj.R
import zq.tju.oj.model.SomeViewModel

class SubmissionDetail : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_submission_detail)
        intiView()
    }

    private fun intiView() {
        heatmap_cal.apply {
            setCalHeatMapData(SomeViewModel().demoData1)
            setCellColorEmpty(Color.WHITE)
            setCellElevation(4f)
            setCellColorMax(Color.BLACK)
            setCellColorMin(Color.GREEN)
//            setCellInfoView(CellInfo)
            isHapticFeedbackEnabled = true
            setShowCellDayText(true)
            setShowLegend(true)
        }
    }
}
