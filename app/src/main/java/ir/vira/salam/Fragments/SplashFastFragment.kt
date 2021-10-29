package ir.vira.salam.Fragments


import android.os.Bundle
import ir.vira.salam.R
import android.content.Intent
import ir.vira.salam.MainActivity
import android.view.View
import androidx.fragment.app.activityViewModels
import dagger.hilt.android.AndroidEntryPoint
import ir.vira.salam.core.BaseFragment
import ir.vira.salam.databinding.FastFragmentBinding
import ir.vira.salam.viewModel.SplashViewModel

@AndroidEntryPoint
class SplashFastFragment : BaseFragment<FastFragmentBinding>(R.layout.fragment_fast) {

    private val viewModel:SplashViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.splashButtonEnter.setOnClickListener {
            viewModel.setUserHasSharedKey()
            val intent = Intent(requireContext() , MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
        }
    }
}