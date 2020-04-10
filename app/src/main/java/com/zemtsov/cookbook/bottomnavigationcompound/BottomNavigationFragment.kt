package com.zemtsov.cookbook.bottomnavigationcompound

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.zemtsov.cookbook.R
import com.zemtsov.cookbook.databinding.FragmentBottomNavigationBinding

/**
 * Developed by Viktor Zemtsov (zemtsovvu@gmail.com)
 * 2020
 *
 * @author Viktor Zemtsov
 */
class BottomNavigationFragment : Fragment() {

    private lateinit var viewBinding: FragmentBottomNavigationBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewBinding = FragmentBottomNavigationBinding.inflate(inflater, container, false)
        return viewBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewBinding.bottomNavigationBar.apply {
            setItems(generateItems(), 0)
            onItemSelectedListener = { index, item ->
                Log.d(
                    this@BottomNavigationFragment.javaClass.simpleName,
                    "Select item with index = $index, item = $item"
                )
            }
            onItemReselectedListener = { index, item ->
                Log.d(
                    this@BottomNavigationFragment.javaClass.simpleName,
                    "Reselect item with index = $index, item = $item"
                )
            }
        }
    }

    private fun generateItems(): List<Pair<Int, Int>> {
        return listOf(
            Pair(R.drawable.ic_queue_music_black_24dp, R.string.bottom_nav_bar_item_1),
            Pair(R.drawable.ic_queue_music_black_24dp, R.string.bottom_nav_bar_item_2),
            Pair(R.drawable.ic_queue_music_black_24dp, R.string.bottom_nav_bar_item_3),
            Pair(R.drawable.ic_queue_music_black_24dp, R.string.bottom_nav_bar_item_4),
            Pair(R.drawable.ic_queue_music_black_24dp, R.string.bottom_nav_bar_item_5)
        )
    }
}