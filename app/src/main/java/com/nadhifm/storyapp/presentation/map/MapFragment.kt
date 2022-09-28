package com.nadhifm.storyapp.presentation.map

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.nadhifm.storyapp.R
import com.nadhifm.storyapp.databinding.FragmentMapBinding
import com.nadhifm.storyapp.utils.createLoadingDialog
import com.nadhifm.storyapp.utils.showErrorSnackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@AndroidEntryPoint
class MapFragment : Fragment() {
    private lateinit var binding: FragmentMapBinding
    private val viewModel: MapViewModel by viewModels()
    private lateinit var loadingDialog: Dialog

    private val callback = OnMapReadyCallback { googleMap ->
        googleMap.uiSettings.isZoomControlsEnabled = true
        googleMap.uiSettings.isIndoorLevelPickerEnabled = true
        googleMap.uiSettings.isCompassEnabled = true
        googleMap.uiSettings.isMapToolbarEnabled = true

        observeErrorSnackbar()
        observeStories(googleMap)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMapBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.getStories()

        loadingDialog = createLoadingDialog(requireContext(), layoutInflater)

        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)
    }


    private fun observeErrorSnackbar() {
        viewModel.errorSnackbar
            .flowWithLifecycle(viewLifecycleOwner.lifecycle, Lifecycle.State.STARTED)
            .onEach {
                showErrorSnackbar(binding.root, it)
            }
            .launchIn(viewLifecycleOwner.lifecycleScope)
    }

    private fun observeStories(googleMap: GoogleMap) {
        viewModel.stories
            .flowWithLifecycle(viewLifecycleOwner.lifecycle, Lifecycle.State.STARTED)
            .onEach { state ->
                if (state.isLoading) {
                    loadingDialog.show()
                } else {
                    loadingDialog.dismiss()
                    state.data?.forEach { stories ->
                        val latLng = LatLng(stories.lat, stories.lon)
                        googleMap.addMarker(MarkerOptions().position(latLng).title(stories.name))
                    }
                }
            }
            .launchIn(viewLifecycleOwner.lifecycleScope)
    }
}