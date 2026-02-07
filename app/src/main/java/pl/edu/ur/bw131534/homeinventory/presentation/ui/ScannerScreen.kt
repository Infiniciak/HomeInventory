package pl.edu.ur.bw131534.homeinventory.presentation.ui

import android.Manifest
import android.content.pm.PackageManager
import android.util.Log
import android.util.Size
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.core.resolutionselector.ResolutionSelector
import androidx.camera.core.resolutionselector.ResolutionStrategy
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel

import androidx.lifecycle.compose.LocalLifecycleOwner
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import pl.edu.ur.bw131534.homeinventory.presentation.viewmodel.ScannerViewModel
import pl.edu.ur.bw131534.homeinventory.utilities.Vibration

@androidx.annotation.OptIn(ExperimentalGetImage::class)
@Composable
fun ScannerScreen(
    onScanSuccess: () -> Unit
) {
    val viewModel: ScannerViewModel = hiltViewModel()
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current




    val saveState by viewModel.saveState.collectAsState()

    LaunchedEffect(saveState) {
        saveState?.let { message ->
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            if (message.startsWith("Dodano")) {
                viewModel.resetSaveState()
                onScanSuccess()
            }
        }
    }

    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
        )
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { granted -> hasCameraPermission = granted }
    )

    LaunchedEffect(key1 = true) {
        if (!hasCameraPermission) {
            launcher.launch(Manifest.permission.CAMERA)
        }
    }

    if (hasCameraPermission) {
        AndroidView(
            factory = { ctx ->
                val previewView = PreviewView(ctx)
                val executor = ContextCompat.getMainExecutor(ctx)
                val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)

                cameraProviderFuture.addListener({
                    val cameraProvider = cameraProviderFuture.get()
                    val preview = Preview.Builder().build()
                    preview.setSurfaceProvider(previewView.surfaceProvider)

                    val resolutionSelector = ResolutionSelector.Builder()
                        .setResolutionStrategy(
                            ResolutionStrategy(
                                Size(1280, 720),
                                ResolutionStrategy.FALLBACK_RULE_CLOSEST_HIGHER_THEN_LOWER
                            )
                        )
                        .build()

                    val imageAnalysis = ImageAnalysis.Builder()
                        .setResolutionSelector(resolutionSelector)
                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                        .build()

                    imageAnalysis.setAnalyzer(executor) { imageProxy ->
                        val mediaImage = imageProxy.image
                        if (mediaImage != null) {
                            val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
                            val scanner = BarcodeScanning.getClient()




                            scanner.process(image)
                                .addOnSuccessListener { barcodes ->
                                    if (barcodes.isNotEmpty()) {
                                        val barcode = barcodes.first()
                                        val rawValue = barcode.rawValue

                                        if (rawValue != null) {
                                            Log.d("SKANER", "Zapisuję kod: $rawValue")
                                            Vibration.triggerVibration(context)
                                            viewModel.saveScannedItem(rawValue)
                                            imageAnalysis.clearAnalyzer()
                                        }
                                    }
                                }
                                .addOnCompleteListener {
                                    imageProxy.close()
                                }
                        } else {
                            imageProxy.close()
                        }
                    }

                    try {
                        cameraProvider.unbindAll()
                        cameraProvider.bindToLifecycle(
                            lifecycleOwner,
                            CameraSelector.DEFAULT_BACK_CAMERA,
                            preview,
                            imageAnalysis
                        )
                    } catch (e: Exception) {
                        Log.e("SKANER", "Błąd kamery", e)
                    }
                }, executor)

                previewView
            },
            modifier = Modifier.fillMaxSize()
        )
    } else {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Wymagany dostęp do kamery")
        }
    }
}