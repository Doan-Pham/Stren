package com.haidoan.android

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivityResultRegistryOwner
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.facebook.CallbackManager
import com.haidoan.android.stren.app.LocalFacebookCallbackManager
import com.haidoan.android.stren.app.StrenApp
import com.haidoan.android.stren.app.StrenAppViewModel
import com.haidoan.android.stren.core.service.FakeAuthenticationServiceImpl
import com.haidoan.android.stren.designsystem.theme.StrenTheme
import dagger.hilt.android.AndroidEntryPoint

private const val TAG = "NavigationTestActivity"

@AndroidEntryPoint
class NavigationTestActivity : ComponentActivity() {
    private var facebookCallbackManager = CallbackManager.Factory.create()
    var isUserSignedIn by mutableStateOf(false)
    private val fakeAuthenticationServiceImpl = FakeAuthenticationServiceImpl()

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()

        super.onCreate(savedInstanceState)
        val viewModel: StrenAppViewModel by viewModels {
            StrenAppViewModel.Factory(
                fakeAuthenticationServiceImpl
            )
        }
        setContent {
            StrenTheme {
                CompositionLocalProvider(
                    LocalFacebookCallbackManager provides facebookCallbackManager,
                    LocalActivityResultRegistryOwner provides this
                ) {
                    Log.d(TAG, "isUserSignedIn: $isUserSignedIn")
                    fakeAuthenticationServiceImpl.isUserSignedIn = isUserSignedIn

                    StrenApp(viewModel = viewModel)
                }
            }
        }
    }
}