package com.zemtsov.cookbook.bottomnavigationcompound

import android.annotation.TargetApi
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.util.AttributeSet
import android.view.Gravity
import android.view.ViewTreeObserver
import android.widget.LinearLayout
import com.zemtsov.cookbook.R

/**
 * Developed by Viktor Zemtsov (zemtsovvu@gmail.com)
 * 2020
 *
 * TODO
 * Double click on child
 * Callback for child clicks (single, double)
 * Animations
 *
 * @author Viktor Zemtsov
 */
class BottomNavigationBar : LinearLayout {

    private var lastSelectedChild: BottomNavigationView? = null

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
        // layoutParams.height = ... не работает в момент инициализации, нужно покопаться, понять почему
        viewTreeObserver.addOnPreDrawListener(object : ViewTreeObserver.OnPreDrawListener {
            override fun onPreDraw(): Boolean {
                layoutParams.apply {
                    height = resources.getDimensionPixelSize(R.dimen.bottom_nav_bar_height)
                }
                orientation = HORIZONTAL
                gravity = Gravity.CENTER
                background = ColorDrawable(Color.GREEN)

                viewTreeObserver.removeOnPreDrawListener(this)
                return true
            }
        })
    }

    fun setItems(
        items: List<Pair<Int /*@DrawableRes*/, Int/*@StringRes*/>>,
        initialChild: Int = 0
    ) {
        for (i in items.indices) {
            val item = items[i]
            val child = BottomNavigationView(context).apply {
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

    fun setInitialChild(index: Int) {
        val child = getChildAt(index)
        if (child is BottomNavigationView) toggleChildState(child)
    }

    private fun toggleChildState(child: BottomNavigationView) {
        lastSelectedChild?.toggleState()
        child.toggleState()
        lastSelectedChild = child
    }
}