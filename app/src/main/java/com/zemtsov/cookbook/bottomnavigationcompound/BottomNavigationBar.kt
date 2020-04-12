package com.zemtsov.cookbook.bottomnavigationcompound

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ValueAnimator
import android.annotation.TargetApi
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
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
 * Pixel perfect Material.io
 *
 * TODO
 * Animations
 * 3. Handle cancel animation
 * 4. Orchestrate animations
 * 2 pixels correction (5 tab)
 * Restriction: Only 3 to 5 tabs
 * Table method
 * Adapt for tablets
 *
 * @author Viktor Zemtsov
 */
class BottomNavigationBar : LinearLayout {

    private var itemCount = 0

    private val selectedChildWidth =
        resources.getDimensionPixelSize(R.dimen.bottom_nav_bar_width_max)

    /**
     * CAUTION: You should use this only after measure pass, layout pass are completed.
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
            background = ColorDrawable(Color.GREEN)
        }
    }

    fun setItems(
        items: List<Pair<Int /*@DrawableRes*/, Int/*@StringRes*/>>,
        initialChild: Int = 0
    ) {
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

                    unselectedWidth = unselectedChildWidth
                    setIcon(item.first)
                    setLabel(item.second)
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
        var lastSelectedChildInnerAnimatorSet: AnimatorSet? = null
        var lastSelectedChildBoundsAnimator: Animator? = null
        lastSelectedChild?.let {
            lastSelectedChildInnerAnimatorSet =
                it.getStateAnimatorSet(BottomNavigationView.State.UNSELECTED)
            lastSelectedChildBoundsAnimator = getChildAnimator(it, unselectedChildWidth)
        }

        val childInnerAnimatorSet = child.getStateAnimatorSet(BottomNavigationView.State.SELECTED)
        val childBoundsAnimator = getChildAnimator(child, selectedChildWidth)

        val animTime = resources.getInteger(android.R.integer.config_shortAnimTime).toLong()

        if (lastSelectedChild == null) {
            AnimatorSet().apply {
                playTogether(
                    childInnerAnimatorSet,
                    childBoundsAnimator
                )

                duration = animTime
                interpolator = DecelerateInterpolator()
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

                duration = animTime
                interpolator = DecelerateInterpolator()
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