package com.kotlin.kiumee.presentation.menu.cart

import androidx.recyclerview.widget.RecyclerView
import com.kotlin.kiumee.databinding.ItemMenuCartBinding

class CartViewHolder(private val binding: ItemMenuCartBinding) :
    RecyclerView.ViewHolder(binding.root) {
    fun bind(data: Cart) {
        with(binding) {
            with(binding) {
                tvItemMenuCartName.text = data.name
                tvItemMenuCartCount.text = "수량 : " + data.count.toString()
                tvItemMenuCartPrice.text = "가격 : " + data.price.toString() + "원"
            }
        }
    }
}