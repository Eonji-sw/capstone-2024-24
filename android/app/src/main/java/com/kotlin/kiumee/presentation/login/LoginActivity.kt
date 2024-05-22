package com.kotlin.kiumee.presentation.login

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.kotlin.kiumee.R
import com.kotlin.kiumee.core.base.BindingActivity
import com.kotlin.kiumee.core.util.context.toast
import com.kotlin.kiumee.core.view.UiState
import com.kotlin.kiumee.databinding.ActivityLoginBinding
import com.kotlin.kiumee.presentation.LoadingActivity
import com.kotlin.kiumee.presentation.store.StoreActivity
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import timber.log.Timber

class LoginActivity : BindingActivity<ActivityLoginBinding>(R.layout.activity_login) {
    private val loginViewModel by viewModels<LoginViewModel>()

    override fun initView() {
        requestPermission()
        initAppbarHomeBtn()
        initTextChanged()
        initObserve()
        initLoginBtnClickListener()
    }

    private fun requestPermission() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.RECORD_AUDIO
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.RECORD_AUDIO),
                0
            )
        }
    }

    private fun initAppbarHomeBtn() {
        binding.appbarLogin.ibLoginHome.isVisible = false
    }

    private fun initTextChanged() {
        binding.etLoginId.addTextChangedListener {
            initBtnChanged()
        }
        binding.etLoginPw.addTextChangedListener {
            initBtnChanged()
        }
    }

    private fun initBtnChanged() {
        with(binding) {
            if (etLoginId.text.isNotEmpty() and etLoginPw.text.isNotEmpty()) {
                btnLogin.setTextColor(resources.getColor(R.color.white))
                btnLogin.setBackgroundResource(R.drawable.shape_primary_fill_15_rect)
                btnLogin.isEnabled = true
            } else {
                btnLogin.setTextColor(resources.getColor(R.color.black))
                btnLogin.setBackgroundResource(R.drawable.shape_gray7_line_20_rect)
                btnLogin.isEnabled = false
            }
        }
    }

    private fun initObserve() {
        loginViewModel.postLogin.flowWithLifecycle(lifecycle).onEach {
            when (it) {
                is UiState.Success -> startActivity(Intent(this, StoreActivity::class.java))

                is UiState.Failure -> {
                    toast("로그인 실패! 다시 입력해주세요.")
                    Timber.d("실패 : $it")
                }

                is UiState.Loading -> {
                    LoadingActivity(this)
                    Timber.d("로딩중")
                }

                is UiState.Empty -> Timber.d("empty")
            }
        }.launchIn(lifecycleScope)
    }

    private fun initLoginBtnClickListener() {
        binding.btnLogin.setOnClickListener {
            loginViewModel.postLogin(
                binding.etLoginId.text.toString(),
                binding.etLoginPw.text.toString()
            )
        }
    }
}
