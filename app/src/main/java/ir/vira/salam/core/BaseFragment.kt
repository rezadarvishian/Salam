package ir.vira.salam.core

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment


open class BaseFragment<T:ViewDataBinding>(@LayoutRes private val resId :Int) : Fragment() {


    private var _binding: T? = null
    val binding get() = _binding!!


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return DataBindingUtil.inflate<T>(inflater, resId, container, false)
            .apply { _binding = this }.root
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

}