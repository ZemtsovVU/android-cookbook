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
import com.zemtsov.cookbook.databinding.ViewBottomNavigationBinding

/**
 * Developed by Viktor Zemtsov (zemtsovvu@gmail.com)
 * 2020
 *
 * @author Viktor Zemtsov
 */
class BottomNavigationView : FrameLayout {

    private val viewBinding: ViewBottomNavigationBinding

    private var lastState: State = State.Inactive

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

    private fun setState(state: State) {
        val animTime = resources.getInteger(android.R.integer.config_longAnimTime).toLong()
        val alpha = if (state is State.Active) 1f else 0f

        val shadowAnimator = ObjectAnimator.ofFloat(
            viewBinding.shadowView,
            "alpha",
            alpha
        ).apply {
            duration = animTime
            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationStart(animation: Animator?) {
                    if (state is State.Active) {
                        viewBinding.shadowView.visibility = View.VISIBLE
                    }
                }

                override fun onAnimationEnd(animation: Animator?) {
                    if (state is State.Inactive) {
                        viewBinding.shadowView.visibility = View.GONE
                    }
                }
            })
        }

        val labelAnimator = ObjectAnimator.ofFloat(
            viewBinding.labelTextView,
            "alpha",
            alpha
        ).apply {
            duration = animTime
            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationStart(animation: Animator?) {
                    if (state is State.Active) {
                        viewBinding.labelTextView.visibility = View.VISIBLE
                    }
                }

                override fun onAnimationEnd(animation: Animator?) {
                    if (state is State.Inactive) {
                        viewBinding.labelTextView.visibility = View.GONE
                    }
                }
            })
        }

        AnimatorSet().apply {
            playTogether(shadowAnimator, labelAnimator)
            start()
        }


        lastState = state
    }

    fun toggleState() {
        setState(if (lastState is State.Active) State.Inactive else State.Active(this))
    }

    fun setIcon(@DrawableRes iconRes: Int) {
        viewBinding.iconImageView.setImageResource(iconRes)
    }

    fun setLabel(@StringRes labelRes: Int) {
        viewBinding.labelTextView.setText(labelRes)
    }

    sealed class State {
//        abstract val rootParams: LinearLayout.LayoutParams
//        abstract val iconLabelParams: LayoutParams
//        abstract val shadowVisibility: Int
//        abstract val labelVisibility: Int

        class Active(private val view: View) : State() {
//            override val rootParams: LinearLayout.LayoutParams
//                get() = LinearLayout.LayoutParams(
//                    view.resources.getDimensionPixelSize(R.dimen.bottom_nav_bar_width_max),
//                    LayoutParams.WRAP_CONTENT
//                )
//
//            override val iconLabelParams: LayoutParams
//                get() = LayoutParams(
//                    LayoutParams.WRAP_CONTENT,
//                    LayoutParams.WRAP_CONTENT
//                ).apply {
//                    gravity = Gravity.CENTER_VERTICAL
//                    marginStart =
//                        view.resources.getDimensionPixelSize(R.dimen.bottom_nav_bar_margin_start_end)
//                }
//
//            override val shadowVisibility: Int
//                get() = View.VISIBLE
//
//            override val labelVisibility: Int
//                get() = View.VISIBLE
        }

        object Inactive : State() {
//            override val rootParams: LinearLayout.LayoutParams
//                get() = LinearLayout.LayoutParams(
//                    LayoutParams.WRAP_CONTENT,
//                    LayoutParams.WRAP_CONTENT
//                ).apply {
//                    weight = 1f
//                }
//
//            override val iconLabelParams: LayoutParams
//                get() = LayoutParams(
//                    LayoutParams.WRAP_CONTENT,
//                    LayoutParams.WRAP_CONTENT
//                ).apply {
//                    gravity = Gravity.CENTER
//                }
//
//            override val shadowVisibility: Int
//                get() = View.GONE
//
//            override val labelVisibility: Int
//                get() = View.GONE
        }
    }
}