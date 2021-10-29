package ir.vira.salam.Fragments


import android.os.Bundle
import ir.vira.salam.R
import android.view.View
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import android.view.animation.TranslateAnimation
import android.view.animation.Animation
import androidx.fragment.app.activityViewModels
import ir.vira.salam.Utiles.observeInLifecycle
import ir.vira.salam.Utiles.setInvisible
import ir.vira.salam.Utiles.setVisible
import ir.vira.salam.core.BaseFragment
import ir.vira.salam.databinding.SecurityFragmentBinding
import ir.vira.salam.viewModel.SplashViewModel
import kotlinx.coroutines.flow.onEach

class SplashSecurityFragment : BaseFragment<SecurityFragmentBinding>(R.layout.fragment_security) {


    private val viewModel:SplashViewModel by activityViewModels()
    private var isImageViewDisplay = true

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        animateImage()
        observeViewModel()
    }


    private fun animateImage(){
        if (isImageViewDisplay) with(binding) {
            splashImageTouchHelp.viewTreeObserver.addOnGlobalLayoutListener(object : OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    splashImageTouchHelp.setVisible()
                    val fromX = splashImageTouchHelp.width.toFloat()
                    val toX = (splashImageTouchHelp.width * -1).toFloat()

                    val translateAnimation = TranslateAnimation(fromX, toX, 0F, 0F).apply {
                        repeatCount = Animation.INFINITE
                        repeatMode = Animation.REVERSE
                        duration = 1000
                    }

                    splashImageTouchHelp.startAnimation(translateAnimation)
                    splashImageTouchHelp.viewTreeObserver.removeOnGlobalLayoutListener(this)
                }
            })
        }
    }

    /**
     * This method change visibility of image view help when view pager scrolled
     */
    private fun observeViewModel(){
        viewModel.preventDisplay.onEach {
            if (it){
                isImageViewDisplay = false
                binding.splashImageTouchHelp.animation = null
                binding.splashImageTouchHelp.setInvisible()
            }
        }.observeInLifecycle(this)
    }

}