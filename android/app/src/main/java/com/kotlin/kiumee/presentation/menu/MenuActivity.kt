package com.kotlin.kiumee.presentation.menu

import android.content.Intent
import android.view.View
import androidx.core.view.get
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView
import com.kotlin.kiumee.R
import com.kotlin.kiumee.core.base.BindingActivity
import com.kotlin.kiumee.databinding.ActivityMenuBinding
import com.kotlin.kiumee.presentation.menu.cart.Cart
import com.kotlin.kiumee.presentation.menu.cart.CartAdapter
import com.kotlin.kiumee.presentation.menu.cart.CartItemDecorator
import com.kotlin.kiumee.presentation.menu.chat.Chat
import com.kotlin.kiumee.presentation.menu.chat.ChatAdapter
import com.kotlin.kiumee.presentation.menu.chat.ChatItemDecorator
import com.kotlin.kiumee.presentation.menu.menuviewpager.MenuViewPagerAdapter
import com.kotlin.kiumee.presentation.menu.tab.TabAdapter
import com.kotlin.kiumee.presentation.menu.tab.TabItemDecorator
import com.kotlin.kiumee.presentation.orderfinish.OrderFinishActivity
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MenuActivity : BindingActivity<ActivityMenuBinding>(R.layout.activity_menu) {
    private val smoothScroller1: RecyclerView.SmoothScroller by lazy {
        object : LinearSmoothScroller(this) {
            override fun getVerticalSnapPreference() = SNAP_TO_START
        }
    }
    private val smoothScroller2: RecyclerView.SmoothScroller by lazy {
        object : LinearSmoothScroller(this) {
            override fun getVerticalSnapPreference() = SNAP_TO_START
        }
    }
    private val smoothScroller3: RecyclerView.SmoothScroller by lazy {
        object : LinearSmoothScroller(this) {
            override fun getVerticalSnapPreference() = SNAP_TO_START
        }
    }

    private val cartList = mutableListOf<Cart>()
    private val chatList = mutableListOf(Chat(1, "키우미에게 대화를 시작해주세요!"))
    var clicked = false

    override fun initView() {
        initChatAdapter()
        addChatItem()
        initLayoutState()
        initTabAdapter()

        initMoveRvBtnClickListener()
        initOrderBtnClickListener()
        initSpeakBtnClickListener()
    }

    private fun initSpeakBtnClickListener() {
        binding.btnMenuSpeak.setOnClickListener {
            // 여기에 음성 인식 추가
            if (clicked) {
                clicked = false
                binding.btnMenuSpeak.setBackgroundResource(R.drawable.shape_secondary_fill_circle)
                binding.btnMenuSpeak.text = "대화\n끄기"
            } else {
                clicked = true
                binding.btnMenuSpeak.setBackgroundResource(R.drawable.shape_point_fill_circle)
                binding.btnMenuSpeak.text = "대화\n켜기"
            }
        }
    }

    private fun initTabAdapter() {
        val tabTitles = listOf("직원 호출", "스테이크류", "덮밥류", "면류", "사이드 메뉴", "음료 메뉴", "주류 메뉴")
        with(binding) {
            vpMenu.adapter = MenuViewPagerAdapter(supportFragmentManager, lifecycle, tabTitles)

            rvMenuTabContent.adapter = TabAdapter(click = { tab, position ->
                vpMenu.currentItem = position
            }).apply {
                submitList(tabTitles)
            }

            vpMenu.isUserInputEnabled = false // 스와이프해서 탭 아이템 넘어가는 것을 허용할 것인지?
            rvMenuTabContent.addItemDecoration(TabItemDecorator(this@MenuActivity))
        }
    }

    private fun initMoveRvBtnClickListener() {
        binding.btnMenuRvChat.setOnClickListener {
            binding.rvMenuRvChat?.layoutManager?.startSmoothScroll(
                smoothScroller1.apply {
                    targetPosition = 2
                }
            )
        }
    }

    private fun initOrderBtnClickListener() {
        binding.btnMenuCartOrder.setOnClickListener {
            if (cartList.isNotEmpty()) {
                startActivity(Intent(this, OrderFinishActivity::class.java))
            }
        }
    }

    private fun initLayoutState() {
        // 추후 코드 변경 필요
        if (cartList.isNotEmpty()) {
            with(binding) {
                rvMenuCart.visibility = View.VISIBLE
                tvMenuCartEmpty.visibility = View.GONE
            }
            initCartAdapter()
        } else {
            initEmptyLayout()
        }
        initCartTotalPrice()
    }

    private fun initEmptyLayout() {
        with(binding) {
            rvMenuCart.visibility = View.GONE
            tvMenuCartEmpty.visibility = View.VISIBLE
        }
    }

    fun addCartItem(cartItem: Cart) {
        // 기존 cartList에 새로운 항목 추가
        cartList.add(cartItem)
        initLayoutState()
    }

    private fun initCartAdapter() {
        val cartAdapter = CartAdapter { cartItem ->
            cartList.remove(cartItem)
            initLayoutState()
        }
        binding.rvMenuCart.adapter = cartAdapter.apply {
            submitList(cartList)
        }

        if (binding.rvMenuCart.itemDecorationCount == 0) {
            binding.rvMenuCart.addItemDecoration(CartItemDecorator(this))
        }

        initCartScrollPointer()
        initChatScrollPointer()
    }

    private fun initCartScrollPointer() {
        binding.rvMenuCart?.layoutManager?.startSmoothScroll(
            smoothScroller2.apply {
                targetPosition = cartList.size - 1
            }
        )
    }

    private fun initCartTotalPrice() {
        // 카트 목록의 가격 합계 계산
        val totalPrice = cartList.sumOf { it.price }
        binding.tvMenuCartTotalPrice.text = totalPrice.toString() + "원"
    }

    // 더미 데이터를 위한 함수. 추후 제거해야 함
    private fun addChatItem() {
        lifecycleScope.launch {
            delay(5000)
            chatList.add(Chat(0, "여기 추천 메뉴가 뭐야?"))
            initChatAdapter()

            delay(1000)
            chatList.add(
                Chat(
                    1,
                    "저희 식당의 추천 메뉴는 대창 큐브 스테이크 덮밥입니다."
                )
            )
            initChatAdapter()
            binding.rvMenuTabContent?.layoutManager?.startSmoothScroll(
                smoothScroller3.apply {
                    targetPosition = 2
                }
            )
            binding.rvMenuTabContent[2].performClick()
            delay(6000)
            chatList.add(
                Chat(
                    1,
                    "대창 큐브 스테이크 덮밥은 우리가게 대표 메뉴로 13000원에 판매되고 있습니다."
                )
            )
            initChatAdapter()

            delay(10000)
            chatList.add(Chat(0, "나는 버블티가 먹고싶어."))
            initChatAdapter()

            delay(1000)
            chatList.add(Chat(1, "죄송합니다. 저희 식당에서는 버블티를 판매하고 있지 않습니다."))
            initChatAdapter()
            delay(6000)
            chatList.add(Chat(1, "대신 복숭아 에이드를 추천드립니다."))
            initChatAdapter()
            binding.rvMenuTabContent?.layoutManager?.startSmoothScroll(
                smoothScroller3.apply {
                    targetPosition = 4
                }
            )
            binding.rvMenuTabContent[4].performClick()

            delay(7000)
            chatList.add(Chat(0, "복숭아 에이드 먹을래."))
            initChatAdapter()

            delay(1000)
            chatList.add(Chat(1, "복숭아 에이드 1잔을 장바구니에 담겠습니다."))
            initChatAdapter()
            addCartItem(Cart("복숭아 에이드", 1, 7000))

            delay(7000)
            chatList.add(Chat(0, "떡볶이 어디있어?"))
            initChatAdapter()

            delay(1000)
            chatList.add(
                Chat(
                    1,
                    "미도인에서는 Side 메뉴로 '미도인 곱창 떡볶이'와 '미도인 우실장 떡볶이'를 판매하고 있습니다."
                )
            )
            initChatAdapter()
            binding.rvMenuTabContent?.layoutManager?.startSmoothScroll(
                smoothScroller3.apply {
                    targetPosition = 3
                }
            )
            binding.rvMenuTabContent[3].performClick()
            delay(7000)
            chatList.add(
                Chat(
                    1,
                    "어떤 메뉴를 장바구니에 담아드릴까요?"
                )
            )
            initChatAdapter()

            delay(8000)
            chatList.add(Chat(0, "결제해줘."))
            initChatAdapter()

            delay(1000)
            chatList.add(
                Chat(
                    1,
                    "복숭아 에이드 1개와 미도인 곱창 떡볶이 1개를 주문하셨습니다."
                )
            )
            initChatAdapter()
            delay(6000)
            chatList.add(
                Chat(
                    1,
                    "총 결제 금액은 16,500원입니다. 결제를 진행하겠습니다."
                )
            )
            initChatAdapter()
            delay(6000)
            startActivity(Intent(this@MenuActivity, OrderFinishActivity::class.java))
        }
    }

    private fun initChatAdapter() {
        binding.rvMenuRvChat.adapter = ChatAdapter().apply {
            submitList(chatList)
        }

        if (binding.rvMenuRvChat.itemDecorationCount == 0) {
            binding.rvMenuRvChat.addItemDecoration(ChatItemDecorator(this))
        }
        initChatScrollPointer()
    }

    private fun initChatScrollPointer() {
        binding.rvMenuRvChat?.layoutManager?.startSmoothScroll(
            smoothScroller1.apply {
                targetPosition = chatList.size - 1
            }
        )
    }
}
