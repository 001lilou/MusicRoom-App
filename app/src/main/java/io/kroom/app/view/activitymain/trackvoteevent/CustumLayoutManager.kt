package io.kroom.app.view.activitymain.trackvoteevent

import android.content.Context
import android.graphics.PointF
import android.util.DisplayMetrics
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView

class CustomLayoutManager(private val _context: Context) : LinearLayoutManager(_context) {
    private var MILLISECONDS_PER_INCH = 1100f
    private var smoothScroller: LinearSmoothScroller? = null

    override fun smoothScrollToPosition(recyclerView: RecyclerView, state: RecyclerView.State, position: Int) {
        super.smoothScrollToPosition(recyclerView, state, position)
        smoothScroller = object : LinearSmoothScroller(_context) {
            override fun computeScrollVectorForPosition(targetPosition: Int): PointF? {
                return this@CustomLayoutManager.computeScrollVectorForPosition(targetPosition)
            }

            protected override fun calculateSpeedPerPixel(displayMetrics: DisplayMetrics): Float {
                return MILLISECONDS_PER_INCH / displayMetrics.densityDpi
            }
        }

        smoothScroller!!.targetPosition = position
        startSmoothScroll(smoothScroller)

    }

    fun setSpeed(speed: Float) {
        this.MILLISECONDS_PER_INCH = speed
    }

}
