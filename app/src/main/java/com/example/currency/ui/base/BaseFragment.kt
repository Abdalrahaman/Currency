package com.example.currency.ui.base

import androidx.fragment.app.Fragment

abstract class BaseFragment : Fragment() {

    abstract val _viewModel: BaseViewModel
}