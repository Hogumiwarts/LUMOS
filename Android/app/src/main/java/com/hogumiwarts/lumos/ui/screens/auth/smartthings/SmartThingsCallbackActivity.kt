package com.hogumiwarts.lumos.ui.screens.auth.smartthings

import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.hogumiwarts.data.source.remote.SmartThingsApi
import com.hogumiwarts.lumos.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject


@AndroidEntryPoint
class SmartThingsCallbackActivity : ComponentActivity() {

    private val viewModel: SmartThingsViewModel by viewModels()

    @Inject
    lateinit var smartThingsApi: SmartThingsApi

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val code = intent?.data?.getQueryParameter("code")

        if (code != null) {
            viewModel.handleSmartThingsCallback(code)

            Timber.tag("SmartThings").d("받은 인증 코드: " + code)

            lifecycleScope.launch {
                viewModel.authResult.collect { result ->
                    result?.let {
                        if (it.isSuccess) {
                            Toast.makeText(
                                this@SmartThingsCallbackActivity,
                                "연동 완료!",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            Toast.makeText(
                                this@SmartThingsCallbackActivity,
                                "연동 실패",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                        viewModel.clearResult()
                        finish()
                    }
                }
            }
        } else {
            Toast.makeText(this, "인증 코드가 없습니다", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
}
