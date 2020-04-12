package com.zemtsov.cookbook.bottomnavigationcompound

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.annotation.TargetApi
import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.zemtsov.cookbook.R
import com.zemtsov.cookbook.databinding.ViewBottomNavigationBinding

/**
 * Developed by Viktor Zemtsov (zemtsovvu@gmail.com)
 * 2020
 *
 * @author Viktor Zemtsov
 */
private const val CALCULATED_VALUES_NOT_INIT_MSG =
    "You must call setUnselectedWidth() right after class creation"

class BottomNavigationView : FrameLayout {

    private val viewBinding: ViewBottomNavigationBinding

    private var lastState: State = State.UNSELECTED

    private lateinit var calculatedValues: CalculatedValues

    @JvmOverloads
    constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0
    ) : super(context, attrs, defStyleAttr)

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    constructor(
        context: Context,
        attrs: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int
    ) : super(context, attrs, defStyleAttr, defStyleRes)

    init {
        val inflater = LayoutInflater.from(context)
        viewBinding = ViewBottomNavigationBinding.inflate(inflater, this)
    }

    fun setIcon(@DrawableRes iconRes: Int) {
        viewBinding.iconImageView.setImageResource(iconRes)
    }

    fun setLabel(@StringRes labelRes: Int) {
        viewBinding.labelTextView.setText(labelRes)
    }

    fun setUnselectedWidth(w: Int) {
        calculatedValues = CalculatedValues(w)
    }

    fun setState(state: State) {
        if (!this::calculatedValues.isInitialized) {
            throw IllegalStateException(CALCULATED_VALUES_NOT_INIT_MSG)
        }

        viewBinding.shadowView.visibility = calculatedValues.visibility(state)
        viewBinding.labelTextView.visibility = calculatedValues.visibility(state)
        viewBinding.iconImageView.translationX = calculatedValues.iconX(state)
        viewBinding.labelTextView.translationX = calculatedValues.labelX(state)

        lastState = state
    }

    fun getStateAnimatorSet(state: State): AnimatorSet {
        if (!this::calculatedValues.isInitialized) {
            throw IllegalStateException(CALCULATED_VALUES_NOT_INIT_MSG)
        }

        val shadowAlphaAnimator = ObjectAnimator.ofFloat(
            viewBinding.shadowView,
            View.ALPHA,
            calculatedValues.alpha(state)
        ).apply {
            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationStart(animation: Animator?) {
                    if (state == State.SELECTED) {
                        viewBinding.shadowView.visibility = View.VISIBLE
                    }
                }

                override fun onAnimationEnd(animation: Animator?) {
                    if (state == State.UNSELECTED) {
                        viewBinding.shadowView.visibility = View.GONE
                    }
                }
            })
        }

        val labelAlphaAnimator = ObjectAnimator.ofFloat(
            viewBinding.labelTextView,
            View.ALPHA,
            calculatedValues.alpha(state)
        ).apply {
            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationStart(animation: Animator?) {
                    if (state == State.SELECTED) {
                        viewBinding.labelTextView.visibility = View.VISIBLE
                    }
                }

                override fun onAnimationEnd(animation: Animator?) {
                    if (state == State.UNSELECTED) {
                        viewBinding.labelTextView.visibility = View.GONE
                    }
                }
            })
        }

        val iconTranslationXAnimator = ObjectAnimator.ofFloat(
            viewBinding.iconImageView,
            View.TRANSLATION_X,
            calculatedValues.iconX(state)
        )

        val labelTranslationXAnimator = ObjectAnimator.ofFloat(
            viewBinding.labelTextView,
            View.TRANSLATION_X,
            calculatedValues.labelX(state)
        )

        return AnimatorSet().apply {
            playTogether(
                shadowAlphaAnimator,
                labelAlphaAnimator,
                iconTranslationXAnimator,
                labelTranslationXAnimator
            )
        }
    }

    inner class CalculatedValues(unselectedWidth: Int) {
        private val iconSize =
            resources.getDimensionPixelSize(R.dimen.bottom_nav_bar_icon_size)
        private val iconMargin =
            resources.getDimensionPixelSize(R.dimen.bottom_nav_bar_margin_start_end)
        private val labelMargin =
            resources.getDimensionPixelSize(R.dimen.bottom_nav_bar_margin_icon_label)

        private var selectedAlpha = 0f
        private var unselectedAlpha = 0f

        private var selectedVisibility = 0
        private var unselectedVisibility = 0

        private var selectedIconX = 0
        private var unselectedIconX = 0

        private var selectedLabelX = 0
        private var unselectedLabelX = 0

        init {
            selectedAlpha = 1f
            unselectedAlpha = 0f

            selectedVisibility = View.VISIBLE
            unselectedVisibility = View.GONE

            selectedIconX = iconMargin
            unselectedIconX = (unselectedWidth / 2) - (iconSize / 2)

            selectedLabelX = iconMargin + labelMargin
            unselectedLabelX = 0
        }

        fun alpha(state: State): Float {
            return if (state == State.SELECTED) selectedAlpha else unselectedAlpha
        }

        fun visibility(state: State): Int {
            return if (state == State.SELECTED) selectedVisibility else unselectedVisibility
        }

        fun iconX(state: State): Float {
            return (if (state == State.SELECTED) selectedIconX else unselectedIconX).toFloat()
        }

        fun labelX(state: State): Float {
            return (if (state == State.SELECTED) selectedLabelX else unselectedLabelX).toFloat()
        }
    }

    enum class State {
        SELECTED,
        UNSELECTED
    }
}