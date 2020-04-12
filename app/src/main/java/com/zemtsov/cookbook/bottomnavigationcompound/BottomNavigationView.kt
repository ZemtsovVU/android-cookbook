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
class BottomNavigationView : FrameLayout {

    private val viewBinding: ViewBottomNavigationBinding

    private var lastState: State = State.UNSELECTED

    var unselectedWidth = 0

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

    fun setState(state: State) {
        if (unselectedWidth == 0) {
            TODO()
        }

        val iconSize = resources.getDimensionPixelSize(R.dimen.bottom_nav_bar_icon_size)
        val iconMargin = resources.getDimensionPixelSize(R.dimen.bottom_nav_bar_margin_start_end)
        val labelMargin = resources.getDimensionPixelSize(R.dimen.bottom_nav_bar_margin_icon_label)

        val visibility: Int
        val iconX: Int
        val labelX: Int

        when (state) {
            State.SELECTED -> {
                visibility = View.VISIBLE
                iconX = iconMargin
                labelX = iconMargin + labelMargin
            }
            State.UNSELECTED -> {
                visibility = View.GONE
                iconX = (unselectedWidth / 2) - (iconSize / 2)
                labelX = 0
            }
        }

        viewBinding.shadowView.visibility = visibility
        viewBinding.labelTextView.visibility = visibility
        viewBinding.iconImageView.translationX = iconX.toFloat()
        viewBinding.labelTextView.translationX = labelX.toFloat()

        lastState = state
    }

    fun getStateAnimatorSet(state: State): AnimatorSet {
        val iconSize = resources.getDimensionPixelSize(R.dimen.bottom_nav_bar_icon_size)
        val iconMargin = resources.getDimensionPixelSize(R.dimen.bottom_nav_bar_margin_start_end)
        val labelMargin = resources.getDimensionPixelSize(R.dimen.bottom_nav_bar_margin_icon_label)

        val alpha: Float
        val iconX: Int
        val labelX: Int

        when (state) {
            State.SELECTED -> {
                alpha = 1f
                iconX = iconMargin
                labelX = iconMargin + labelMargin
            }
            State.UNSELECTED -> {
                alpha = 0f
                iconX = (unselectedWidth / 2) - (iconSize / 2)
                labelX = 0
            }
        }

        val shadowAlphaAnimator = ObjectAnimator.ofFloat(
            viewBinding.shadowView,
            View.ALPHA,
            alpha
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
            alpha
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
            iconX.toFloat()
        )

        val labelTranslationXAnimator = ObjectAnimator.ofFloat(
            viewBinding.labelTextView,
            View.TRANSLATION_X,
            labelX.toFloat()
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

    fun setIcon(@DrawableRes iconRes: Int) {
        viewBinding.iconImageView.setImageResource(iconRes)
    }

    fun setLabel(@StringRes labelRes: Int) {
        viewBinding.labelTextView.setText(labelRes)
    }

    enum class State {
        SELECTED,
        UNSELECTED
    }
}