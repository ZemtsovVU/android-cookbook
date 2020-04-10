package com.zemtsov.cookbook

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.zemtsov.cookbook.databinding.FragmentRecyclerAdapterBinding
import com.zemtsov.cookbook.databinding.ItemForRecyclerAdapterBinding

/**
 * Нужен ли @Synchronized для метода setItems(...) в RecyclerAdapter?
 *
 * Developed by Viktor Zemtsov (zemtsovvu@gmail.com)
 * 2020
 *
 * @author Viktor Zemtsov
 */
class SynchronizedRecyclerAdapterFragment : Fragment() {

    private lateinit var viewBinding: FragmentRecyclerAdapterBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewBinding = FragmentRecyclerAdapterBinding.inflate(inflater, container, false)
        return viewBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = Adapter()
        viewBinding.recyclerView.apply {
            this.adapter = adapter
            this.layoutManager = LinearLayoutManager(context)
        }

        adapter.setItems(generateItems())

        // Crash!
        Thread(Runnable {
            Thread.sleep(3000)
            adapter.setItems(generateItems())
        }).start()
    }

    private fun generateItems(): List<Int> {
        val items = mutableListOf<Int>()
        for (i in 0 until 50) {
            items.add(i)
        }
        return items
    }

    class Adapter : RecyclerView.Adapter<Adapter.Holder>() {

        private lateinit var viewBinding: ItemForRecyclerAdapterBinding

        private val items = mutableListOf<Int>()

        @Synchronized // Bullshit!
        fun setItems(items: List<Int>) {
            this.items.clear()
            this.items.addAll(items)
            notifyDataSetChanged()
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
            val inflater = LayoutInflater.from(parent.context)
            viewBinding = ItemForRecyclerAdapterBinding.inflate(inflater, parent, false)
            return Holder(viewBinding.root)
        }

        override fun onBindViewHolder(holder: Holder, position: Int) {
            holder.bind(items[position])
        }

        override fun getItemCount(): Int {
            return items.size
        }

        inner class Holder(itemView: View) : RecyclerView.ViewHolder(itemView) {

            fun bind(item: Int) {
                viewBinding.textView.text = item.toString()
            }
        }
    }
}