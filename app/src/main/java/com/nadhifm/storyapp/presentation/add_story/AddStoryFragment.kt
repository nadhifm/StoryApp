package com.nadhifm.storyapp.presentation.add_story

import android.Manifest
import android.app.Activity.RESULT_CANCELED
import android.app.Activity.RESULT_OK
import android.app.Dialog
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.nadhifm.storyapp.R
import com.nadhifm.storyapp.databinding.FragmentAddStoryBinding
import com.nadhifm.storyapp.utils.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import java.io.File
import java.util.concurrent.TimeUnit

@AndroidEntryPoint
class AddStoryFragment : Fragment() {
    private lateinit var binding: FragmentAddStoryBinding
    private val viewModel: AddStoryViewModel by viewModels()
    private lateinit var loadingDialog: Dialog

    private lateinit var currentPhotoPath: String
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest

    private val requestCameraPermissionResult = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) {
        if (it) {
            startTakePhoto()
        } else {
            showErrorSnackbar(binding.root, getString(R.string.permission_not_granted))
        }
    }
    private val requestLocationPermissionLauncher =
    registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        when {
            permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false -> {
                // Precise location access granted.
                getMyLastLocation()
            }
            permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false -> {
                // Only approximate location access granted.
                getMyLastLocation()
            }
            else -> {
                showErrorSnackbar(binding.root, getString(R.string.permission_not_granted))
                binding.switchShareLocation.isChecked = false
                viewModel.setLocation()
            }
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAddStoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadingDialog = createLoadingDialog(requireContext(), layoutInflater)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())

        binding.buttonCamera.setOnClickListener {
            requestCameraPermissionResult.launch(Manifest.permission.CAMERA)
        }
        binding.buttonGallery.setOnClickListener {
            startGallery()
        }

        binding.switchShareLocation.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                createLocationRequest()
            } else {
                viewModel.setLocation()
            }
        }

        observePhoto()
        observeAddStoryResult()
    }

    private fun checkPermission(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(
            requireContext(),
            permission
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun getMyLastLocation() {
        if (checkPermission(Manifest.permission.ACCESS_FINE_LOCATION) &&
            checkPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
        ){
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                if (location != null) {
                    viewModel.setLocation(location)
                } else {
                    showErrorSnackbar(binding.root, "Location is not found. Try Again")
                }
            }
        } else {
            requestLocationPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    private val resolutionLauncher =
        registerForActivityResult(
            ActivityResultContracts.StartIntentSenderForResult()
        ) { result ->
            when (result.resultCode) {
                RESULT_OK -> createLocationRequest()
                RESULT_CANCELED -> {
                    binding.switchShareLocation.isChecked = false
                    showErrorSnackbar(binding.root, getString(R.string.you_must_enable_gps))
                }
            }
        }

    private fun createLocationRequest() {
        locationRequest = LocationRequest.create().apply {
            interval = TimeUnit.SECONDS.toMillis(1)
            maxWaitTime = TimeUnit.SECONDS.toMillis(1)
            priority = Priority.PRIORITY_HIGH_ACCURACY
        }

        val builder = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest)
        val client: SettingsClient = LocationServices.getSettingsClient(requireActivity())
        client.checkLocationSettings(builder.build())
            .addOnSuccessListener {
                getMyLastLocation()
            }
            .addOnFailureListener { exception ->
                if (exception is ResolvableApiException) {
                    try {
                        resolutionLauncher.launch(
                            IntentSenderRequest.Builder(exception.resolution).build()
                        )
                    } catch (sendEx: IntentSender.SendIntentException) {
                        binding.switchShareLocation.isChecked = false
                        showErrorSnackbar(binding.root, sendEx.message.toString())
                    }
                }
            }
    }

    private fun startGallery() {
        val intent = Intent()
        intent.action = Intent.ACTION_GET_CONTENT
        intent.type = "image/*"
        val chooser = Intent.createChooser(intent, "Choose a Picture")
        launcherIntentGallery.launch(chooser)
    }

    private fun startTakePhoto() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.resolveActivity(requireActivity().packageManager)

        createCustomTempFile(requireContext()).also {
            val photoURI: Uri = FileProvider.getUriForFile(
                requireContext(),
                "com.nadhifm.storyapp",
                it
            )
            currentPhotoPath = it.absolutePath
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
            launcherIntentCamera.launch(intent)
        }
    }

    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == AppCompatActivity.RESULT_OK) {
            val myFile = File(currentPhotoPath)
            val photo = reduceFileImage(myFile)
            viewModel.setPhoto(photo)
        }
    }

    private val launcherIntentGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == AppCompatActivity.RESULT_OK) {
            val selectedImg: Uri = result.data?.data as Uri

            val myFile = uriToFile(selectedImg, requireContext())
            val photo = reduceFileImage(myFile)
            viewModel.setPhoto(photo)
        }
    }

    private fun observePhoto() {
        viewModel.photo
            .flowWithLifecycle(viewLifecycleOwner.lifecycle, Lifecycle.State.STARTED)
            .onEach { photo ->
                photo?.let { file ->
                    val result = BitmapFactory.decodeFile(file.path)
                    binding.ivPreviewPhoto.setImageBitmap(result)
                }

                binding.buttonAddStory.setOnClickListener {
                    val description = binding.edAddDescription.text.toString()
                    if (photo == null) {
                        showErrorSnackbar(binding.root, getString(R.string.choose_image_first))
                    } else if (description.isEmpty()) {
                        binding.edAddDescription.requestFocus()
                        binding.edAddDescription.error = getString(R.string.input_photo_description)
                    } else {
                        viewModel.addStory(description)
                    }
                }
            }
            .launchIn(viewLifecycleOwner.lifecycleScope)
    }

    private fun observeAddStoryResult() {
        viewModel.addStoryResult
            .flowWithLifecycle(viewLifecycleOwner.lifecycle, Lifecycle.State.STARTED)
            .onEach { result ->
                when(result) {
                    is Resource.Error -> {
                        loadingDialog.dismiss()
                        showErrorSnackbar(binding.root, result.message.toString())
                    }
                    is Resource.Loading -> {
                        loadingDialog.show()
                    }
                    is Resource.Success -> {
                        loadingDialog.dismiss()
                        showSuccessSnackbar(binding.root, result.data.toString())
                        findNavController().navigate(R.id.action_addStoryFragment_to_homeFragment)
                    }
                }
            }
            .launchIn(viewLifecycleOwner.lifecycleScope)
    }
}