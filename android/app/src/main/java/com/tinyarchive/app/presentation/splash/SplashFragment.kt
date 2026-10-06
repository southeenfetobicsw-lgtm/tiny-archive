package com.tinyarchive.app.presentation.splash

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.tinyarchive.app.MainActivity
import com.tinyarchive.app.R
import com.tinyarchive.app.databinding.FragmentSplashBinding
import com.tinyarchive.app.presentation.common.ViewModelFactory
import kotlinx.coroutines.launch

class SplashFragment : Fragment() {

    private var _binding: FragmentSplashBinding? = null
    private val binding get() = requireNotNull(_binding)

    private val viewModel: SplashViewModel by viewModels { ViewModelFactory }

    private var animator: SplashAnimator? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentSplashBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val helper = SplashAnimator(resources.displayMetrics.density)
        animator = helper
        helper.playEntrance(binding)
        helper.startLoops(binding)
        observePhase()
        observeHandOff()
        viewModel.start()
    }

    private fun observePhase() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.phase.collect { phase ->
                    renderPhase(phase)
                }
            }
        }
    }

    private fun observeHandOff() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.handOffReady.collect { ready ->
                    if (ready) {
                        handOff()
                    }
                }
            }
        }
    }

    private fun renderPhase(phase: SplashPhase) {
        val binding = _binding ?: return
        val label = when (phase) {
            SplashPhase.OPENING -> R.string.splash_loading
            SplashPhase.INDEXING -> R.string.splash_phase_index
            SplashPhase.READY -> R.string.splash_phase_ready
        }
        binding.txtSplashPhase.setText(label)
    }

    private fun handOff() {
        if (!isAdded || view == null) {
            return
        }
        val host = activity as? MainActivity ?: return
        if (!host.navigator.showMenu()) {
            return
        }
        viewModel.consumeHandOff()
    }

    override fun onDestroyView() {
        animator?.cancel()
        animator = null
        _binding = null
        super.onDestroyView()
    }
}
