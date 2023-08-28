package com.example.currency.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.example.currency.R
import com.example.currency.databinding.FragmentConvertCurrencyBinding
import com.example.currency.ui.base.BaseFragment
import com.example.currency.ui.viewmodels.ConvertCurrencyViewModel
import com.example.currency.ui.viewmodels.SymbolUiState
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ConvertCurrencyFragment : BaseFragment() {

    private lateinit var binding: FragmentConvertCurrencyBinding

    override val _viewModel by activityViewModels<ConvertCurrencyViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate<FragmentConvertCurrencyBinding?>(
                inflater,
                R.layout.fragment_convert_currency, container, false
            ).apply {
                viewModel = _viewModel
            }
        initActions()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.lifecycleOwner = viewLifecycleOwner

        handleObservers()

        loadSymbols()

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                _viewModel.targetCurrencyAmount.collectLatest {
                    binding.etTargetCurrencyAmount.setText(it.toString())
                }
            }
        }
    }

    private fun initActions() {
        with(binding) {
            autoCompleteBaseCurrency.setOnItemClickListener { adapterView, _, pos, _ ->
                _viewModel.baseCurrencyCode.value = adapterView.getItemAtPosition(pos).toString()
            }
            autoCompleteTargetCurrency.setOnItemClickListener { adapterView, _, pos, _ ->
                _viewModel.targetCurrencyCode.value = adapterView.getItemAtPosition(pos).toString()
            }
            ivSwap.setOnClickListener {
                _viewModel.swapCurrencies()
                autoCompleteBaseCurrency.setText(_viewModel.baseCurrencyCode.value, false)
                autoCompleteTargetCurrency.setText(_viewModel.targetCurrencyCode.value, false)
            }
            btDetails.setOnClickListener {
                findNavController().navigate(
                    ConvertCurrencyFragmentDirections.actionConvertCurrencyFragmentToDetailsFragment(
                        _viewModel.baseCurrencyCode.value
                    )
                )
            }
        }
    }

    private fun handleObservers() {
        _viewModel.baseCurrencyCode.observe(viewLifecycleOwner) {
            it?.let {
                if (_viewModel.targetCurrencyCode.value != null) {
                    _viewModel.convert()
                }
            }
        }
        _viewModel.targetCurrencyCode.observe(viewLifecycleOwner) {
            it?.let {
                if (_viewModel.baseCurrencyCode.value != null) {
                    _viewModel.convert()
                }
            }
        }
        _viewModel.baseCurrencyAmount.observe(viewLifecycleOwner) {
            it?.let {
                if (_viewModel.baseCurrencyCode.value != null && _viewModel.targetCurrencyCode.value != null) {
                    _viewModel.convert()
                }
            }
        }

        _viewModel.errorConvert.observe(viewLifecycleOwner) {
            it?.let {
                Snackbar.make(requireView(), it, Snackbar.LENGTH_SHORT).show()
                _viewModel.onConvertErrorFinished()
            }
        }
    }

    private fun loadSymbols() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                _viewModel.symbolUiState.collectLatest {
                    when (it) {
                        is SymbolUiState.Success -> {
                            binding.autoCompleteBaseCurrency.setAdapter(
                                ArrayAdapter(
                                    requireContext(),
                                    R.layout.item_drop_down,
                                    it.symbols
                                )
                            )
                            binding.autoCompleteTargetCurrency.setAdapter(
                                ArrayAdapter(
                                    requireContext(),
                                    R.layout.item_drop_down,
                                    it.symbols
                                )
                            )
                        }

                        is SymbolUiState.Error -> {
                            Snackbar.make(requireView(), it.message, Snackbar.LENGTH_SHORT).show()
                        }

                        else -> Unit
                    }
                }
            }
        }
    }
}