package com.zemtsov.cookbook.bottomnavigationcompound

import android.animation.AnimatorSet
import android.animation.ValueAnimator
import android.annotation.TargetApi
import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.view.Gravity
import android.view.animation.DecelerateInterpolator
import android.widget.LinearLayout
import com.zemtsov.cookbook.R

/**
 * Developed by Viktor Zemtsov (zemtsovvu@gmail.com)
 * 2020
 *
 * Pixel perfect Material.io (for phones)
 *
 * TODO
 * Style configuration (bar color, shadow color, text appearance, font, etc.)
 * Collapsing behavior
 * Adapt for tablets/landscape
 * Pixels correction (for 5 tab in 1080p)
 *
 * @author Viktor Zemtsov
 */
private const val ITEMS_COUNT_NOT_IN_LEGAL_RANGE_MSG = "Tabs count must be between 3 to 5."

class BottomNavigationBar : LinearLayout {

    private val _duration = resources.getInteger(android.R.integer.config_shortAnimTime).toLong()
    private val _interpolator = DecelerateInterpolator()

    private var itemCount = 0

    private val selectedChildWidth =
        resources.getDimensionPixelSize(R.dimen.bottom_nav_bar_width_max)

    /**
     * CAUTION: You should use this value only after measure pass and layout pass are completed.
     * Use viewTreeObserver or post() for this purpose.
     */
    private var unselectedChildWidth = 0
        get() {
            if (field == 0) {
                val unselectedChildrenWidth = width - selectedChildWidth
                field =
                    unselectedChildrenWidth / (itemCount - 1) // todo после округления получается -2 пикселя (заметно по 5 табу)
            }
            return field
        }

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
        post {
            layoutParams.apply {
                height = resources.getDimensionPixelSize(R.dimen.bottom_nav_bar_height)
            }
            orientation = HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
        }
    }

    fun setItems(
        items: List<Pair<Int /*@DrawableRes*/, Int/*@StringRes*/>>,
        initialChild: Int = 0
    ) {
        if (items.size > 5 || items.size < 3) {
            throw IllegalArgumentException(ITEMS_COUNT_NOT_IN_LEGAL_RANGE_MSG)
        }

        post {
            itemCount = items.size
            unselectedChildWidth = 0

            for (i in items.indices) {
                val item = items[i]
                val child = BottomNavigationView(context).apply {
                    layoutParams =
                        LayoutParams(
                            if (i == initialChild) selectedChildWidth else unselectedChildWidth,
                            LayoutParams.WRAP_CONTENT
                        )

                    setIcon(item.first)
                    setLabel(item.second)
                    setUnselectedWidth(unselectedChildWidth)
                    setState(BottomNavigationView.State.UNSELECTED)

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

    private fun setInitialChild(index: Int) {
        val child = getChildAt(index)
        if (child is BottomNavigationView) toggleChildState(child)
    }

    private fun toggleChildState(child: BottomNavigationView) {
        val lastSelectedChildInnerAnimatorSet =
            lastSelectedChild?.getStateAnimatorSet(BottomNavigationView.State.UNSELECTED)
        val lastSelectedChildBoundsAnimator =
            lastSelectedChild?.let { getChildAnimator(it, unselectedChildWidth) }

        val childInnerAnimatorSet = child.getStateAnimatorSet(BottomNavigationView.State.SELECTED)
        val childBoundsAnimator = getChildAnimator(child, selectedChildWidth)

        if (lastSelectedChild == null) {
            AnimatorSet().apply {
                playTogether(
                    childInnerAnimatorSet,
                    childBoundsAnimator
                )

                duration = _duration
                interpolator = _interpolator
                start()
            }
        } else {
            AnimatorSet().apply {
                playTogether(
                    lastSelectedChildInnerAnimatorSet,
                    lastSelectedChildBoundsAnimator,
                    childInnerAnimatorSet,
                    childBoundsAnimator
                )

                duration = _duration
                interpolator = _interpolator
                start()
            }
        }

        lastSelectedChild = child
    }

    private fun getChildAnimator(child: BottomNavigationView, newW: Int): ValueAnimator {
        val oldW = child.width
        return ValueAnimator.ofInt(oldW, newW).apply {
            addUpdateListener {
                val w: Int = it.animatedValue as Int
                child.layoutParams.apply {
                    width = w
                    requestLayout()
                    invalidate()
                }
            }
        }
    }
}