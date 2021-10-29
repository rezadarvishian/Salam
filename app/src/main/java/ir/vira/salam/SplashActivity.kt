package ir.vira.salam

import android.annotation.SuppressLint
import android.os.Bundle
import ir.vira.salam.Adapters.ViewPagerAdapter
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import ir.vira.network.NetworkInformation
import android.content.Intent
import androidx.activity.viewModels
import dagger.hilt.android.AndroidEntryPoint
import ir.vira.salam.Utiles.ConstVal.IS_ADMIN
import ir.vira.salam.Utiles.observeInLifecycle
import ir.vira.salam.Utiles.setInvisible
import ir.vira.salam.Utiles.setVisible
import ir.vira.salam.Utiles.showToast
import ir.vira.salam.core.BaseActivity
import ir.vira.salam.databinding.SplashActivityBinding
import ir.vira.salam.viewModel.SplashViewModel
import kotlinx.coroutines.flow.onEach
import java.lang.Exception

@SuppressLint("CustomSplashScreen")
@AndroidEntryPoint
class SplashActivity : BaseActivity<SplashActivityBinding>(R.layout.activity_splash) {

    private val viewModel: SplashViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        observeViewModel()
        binding.splashButtonCheckConnection.setOnClickListener { checkNetwork() }
    }

    private fun observeViewModel(){
        viewModel.hasSharedKey.onEach {
            if (it) checkNetwork()
            else initIntroViewPager()
        }.observeInLifecycle(this)
    }

    private fun initIntroViewPager(){
        binding.splashViewPager.adapter = ViewPagerAdapter(supportFragmentManager)
        binding.splashViewPager.addOnPageChangeListener(object : OnPageChangeListener {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {}

            override fun onPageSelected(position: Int) {
                if (position == 1) viewModel.preventDisplayingImageViewHelp()
            }

            override fun onPageScrollStateChanged(state: Int) {}
        })
    }

    private fun checkNetwork() {
        try {
            binding.splashButtonCheckConnection.setInvisible()
            val networkInformation = NetworkInformation(this)
            if (networkInformation.isWifiEnabled) {

                if (networkInformation.isConnectedToNetwork)
                    startActivity(Intent(this,
                        MainActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK))
                else binding.splashButtonCheckConnection.setVisible()

            }
            else if (networkInformation.isWifiAccessPointEnabled) {
                startActivity(
                    Intent(this, ChatActivity::class.java)
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or
                                Intent.FLAG_ACTIVITY_CLEAR_TASK)
                        .putExtra(IS_ADMIN, true)
                )
            }
            else binding.splashButtonCheckConnection.setVisible()
        } catch (e: Exception) {
            showToast("خطا در بررسی وضعیت اتصال اینترنت")
            e.printStackTrace()
        }
    }
}