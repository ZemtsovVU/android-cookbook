package com.zemtsov.cookbook.bottomnavigationcompound

import android.annotation.TargetApi
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.util.AttributeSet
import android.view.Gravity
import android.widget.LinearLayout
import com.zemtsov.cookbook.R

/**
 * Developed by Viktor Zemtsov (zemtsovvu@gmail.com)
 * 2020
 *
 * TODO
 * Animations
 * 1. Parent do measure and layout children
 * 2. Parent animate children
 *
 * @author Viktor Zemtsov
 */
class BottomNavigationBar : LinearLayout {

    private var lastSelectedChild: BottomNavigationView? = null

    private var itemCount = 0
    private var calculatedChildWidth = 0

    var onItemSelectedListener: ((index: Int, item: BottomNavigationView) -> Unit)? = null
    var onItemReselectedListener: ((index: Int, item: BottomNavigationView) -> Unit)? = null

    @JvmOverloads
    constructor(
        context: Context?,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0
    ) : super(context, attrs, defStyleAttr)

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    constructor(
        context: Context?,
        attrs: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int
    ) : super(context, attrs, defStyleAttr, defStyleRes)

    init {
        post {
            layoutParams.apply {
                height = resources.getDimensionPixelSize(R.dimen.bottom_nav_bar_height)
            }
            orientation = HORIZONTAL
            gravity = Gravity.CENTER
            background = ColorDrawable(Color.GREEN)
        }
    }

    fun setItems(
        items: List<Pair<Int /*@DrawableRes*/, Int/*@StringRes*/>>,
        initialChild: Int = 0
    ) {
        post {
            itemCount = items.size
            calculatedChildWidth = 0

            for (i in items.indices) {
                val item = items[i]
                val child = BottomNavigationView(context).apply {
                    layoutParams =
                        LayoutParams(calculateChildWidth(), LayoutParams.WRAP_CONTENT)

                    setIcon(item.first)
                    setLabel(item.second)
                    setOnClickListener {
                        if (lastSelectedChild == this) {
                            onItemReselectedListener?.invoke(i, this)
                        } else {
                            toggleChildState(this)
                            onItemSelectedListener?.invoke(i, this)
                        }
                    }
                }
                addView(child)
            }

            setInitialChild(initialChild)
        }
    }

    private fun calculateChildWidth(): Int {
        if (calculatedChildWidth == 0) {
            calculatedChildWidth = width / itemCount
        }

        return calculatedChildWidth
    }

    private fun setInitialChild(index: Int) {
        val child = getChildAt(index)
        if (child is BottomNavigationView) toggleChildState(child)
    }

    private fun toggleChildState(child: BottomNavigationView) {
        lastSelectedChild?.toggleState()
        child.toggleState()
        lastSelectedChild = child
    }
}