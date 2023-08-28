package com.example.currency.ui.fragments

import android.graphics.Color
import android.graphics.DashPathEffect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.SavedStateViewModelFactory
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.navArgs
import com.example.currency.R
import com.example.currency.data.model.pojo.HistoricalRates
import com.example.currency.databinding.FragmentDetailsBinding
import com.example.currency.ui.adapters.HistoricalRatesAdapter
import com.example.currency.ui.base.BaseFragment
import com.example.currency.ui.viewmodels.DetailsViewModel
import com.example.currency.ui.viewmodels.HistoricalRatesUiState
import com.example.currency.ui.viewmodels.LatestRatesForOtherCurrenciesUiState
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IFillFormatter
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import timber.log.Timber

@AndroidEntryPoint
class DetailsFragment : BaseFragment() {

    private lateinit var binding: FragmentDetailsBinding

    override val _viewModel by viewModels<DetailsViewModel>()

    private lateinit var historicalRatesAdapter: HistoricalRatesAdapter
    private lateinit var latestRatesAdapter: HistoricalRatesAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate<FragmentDetailsBinding?>(
                inflater,
                R.layout.fragment_details, container, false
            ).apply {
                viewModel = _viewModel
            }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupAdapters()

        loadHistoricalRatesForLast3Days()
        loadLatestRatesForOtherCurrencies()
    }

    private fun setupAdapters() {
        val viewModel = binding.viewModel
        if (viewModel != null) {
            historicalRatesAdapter =
                HistoricalRatesAdapter(HistoricalRatesAdapter.OnClickListener {
                    // handle on click
                })
            latestRatesAdapter =
                HistoricalRatesAdapter(HistoricalRatesAdapter.OnClickListener {
                    // handle on click
                })
            binding.historicalRecyclerView.adapter = historicalRatesAdapter
            binding.otherCurrenciesRecyclerView.adapter = latestRatesAdapter
        } else {
            Timber.w("ViewModel not initialized when attempting to set up adapter.")
        }
    }

    private fun initLineChart(
        historicalRates: List<HistoricalRates>
    ) {
        binding.lineChart.apply {
            description.isEnabled = false
            setTouchEnabled(true)
            legend.apply {
                isEnabled = false
                form = Legend.LegendForm.LINE
            }
            setDrawGridBackground(false)
            isDragEnabled = true
            setScaleEnabled(true)
            setPinchZoom(true)

            xAxis.apply {
                enableGridDashedLine(10f, 10f, 0f)
            }

            axisRight.apply {

            }
            axisLeft.apply {
                axisRight.isEnabled = false
                // horizontal grid lines
                enableGridDashedLine(10f, 10f, 0f)

                // axis range
                axisMaximum = 200f
                axisMinimum = -50f
            }
            animateX(1500)
        }

        setData(binding.lineChart, historicalRates)
    }

    private fun setData(lineChart: LineChart, historicalData: List<HistoricalRates>) {
        val values = ArrayList<Entry>()
        val lineDataSet = arrayListOf<ILineDataSet>()

        historicalData.forEachIndexed { index, historicalRates ->
            values.add(
                Entry(index.toFloat(), historicalRates.rate.toFloat())
            )
        }

        lineDataSet.add(
            LineDataSet(values, "DataSet 1").apply {
                setDrawIcons(false)
                enableDashedLine(10f, 5f, 0f)
                // black lines and points
                color = Color.BLACK
                setCircleColor(Color.BLACK)

                // line thickness and point size
                lineWidth = 1f
                circleRadius = 3f

                // draw points as solid circles
                setDrawCircleHole(false)

                // customize legend entry
                formLineWidth = 1f
                formLineDashEffect = DashPathEffect(floatArrayOf(10f, 5f), 0f)
                formSize = 15f

                // text size of values
                valueTextSize = 9f

                // draw selection line as dashed
                enableDashedHighlightLine(10f, 5f, 0f)

                // set the filled area
                fillAlpha = 50
                fillColor = ContextCompat.getColor(requireContext(), R.color.primary_color)
                // set the filled area
                setDrawFilled(true)
                fillFormatter =
                    IFillFormatter { dataSet, dataProvider ->
                        lineChart.axisLeft.axisMinimum
                    }
            }
        )

        val data = LineData(lineDataSet).apply {
            setDrawValues(false)
        }
        lineChart.data = data
    }

    private fun loadHistoricalRatesForLast3Days() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                _viewModel.historicalRatesState.collectLatest {
                    when (it) {
                        is HistoricalRatesUiState.Success -> {
                            binding.historicalProgressBar.isVisible = false
                            initLineChart(it.historicalRates)
                            historicalRatesAdapter.submitList(it.historicalRates)
                        }

                        is HistoricalRatesUiState.Error -> {
                            binding.historicalProgressBar.isVisible = false
                            Snackbar.make(requireView(), it.message, Snackbar.LENGTH_SHORT).show()
                        }

                        is HistoricalRatesUiState.Loading -> {
                            binding.historicalProgressBar.isVisible = true
                        }
                    }
                }
            }
        }
    }

    private fun loadLatestRatesForOtherCurrencies() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                _viewModel.latestRatesForOtherCurrenciesState.collectLatest {
                    when (it) {
                        is LatestRatesForOtherCurrenciesUiState.Success -> {
                            binding.otherCurrenciesProgressBar.isVisible = false
                            latestRatesAdapter.submitList(it.latestRates)
                        }

                        is LatestRatesForOtherCurrenciesUiState.Error -> {
                            binding.otherCurrenciesProgressBar.isVisible = false
                            Snackbar.make(requireView(), it.message, Snackbar.LENGTH_SHORT).show()
                        }

                        is LatestRatesForOtherCurrenciesUiState.Loading -> {
                            binding.otherCurrenciesProgressBar.isVisible = true
                        }
                    }
                }
            }
        }
    }

}