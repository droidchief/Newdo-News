package com.example.newdo.customview

import android.content.Context
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlin.math.abs

class CenterZoomLayout : LinearLayoutManager {

    private val mShrinkAmount = 0.15F
    private val mShrinkDistance = 0.9F

    constructor(context: Context) : super(context)
    constructor(context: Context, orientation: Int, reverseLayout: Boolean) :
            super(
                context, orientation, reverseLayout
            )

    //set horizontal zoom scroll function
    override fun scrollHorizontallyBy(
        dx: Int,
        recycler: RecyclerView.Recycler?,
        state: RecyclerView.State?
    ): Int {
        val orientation = orientation
        if (orientation == HORIZONTAL) {
            val scrolled = super.scrollHorizontallyBy(dx, recycler, state)

            val midPoint = width/2F
            val d0 = 0F
            val d1 = mShrinkDistance * midPoint
            val s0 =  1F
            val s1 = 1F - mShrinkAmount

            for (i in 0 until childCount) {
                val child = getChildAt(i)
                val childMidPoint = (getDecoratedRight(child!!) + getDecoratedLeft(child)/2F)
                val d = d1.coerceAtMost(abs(midPoint - childMidPoint))
                val scale = s0+(s1-s0) * (d - d0)/(d1 - d0)
                child.scaleX = scale
                child.scaleY = scale
            }

            return scrolled
        }else {
            return 0
        }
    }

}