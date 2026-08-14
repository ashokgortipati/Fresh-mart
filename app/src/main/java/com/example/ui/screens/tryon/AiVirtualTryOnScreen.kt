package com.example.ui.screens.tryon

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.Product
import com.example.data.model.ProductCategory
import com.example.data.seed.SampleData
import com.example.ui.theme.*
import com.example.ui.viewmodel.ShopViewModel
import com.example.ui.viewmodel.TryOnViewMode
import com.example.ui.viewmodel.TryOnViewModel
import java.io.InputStream

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AiVirtualTryOnScreen(
    tryOnViewModel: TryOnViewModel,
    shopViewModel: ShopViewModel,
    onBack: () -> Unit,
    onNavigateToProduct: (Product) -> Unit
) {
    val state by tryOnViewModel.uiState.collectAsState()
    val context = LocalContext.current

    val clothingCatalog = remember {
        SampleData.sampleProducts.filter { it.category == ProductCategory.CLOTHING }
    }

    // Photo pickers
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            try {
                val inputStream: InputStream? = context.contentResolver.openInputStream(it)
                val bitmap = BitmapFactory.decodeStream(inputStream)
                tryOnViewModel.setUserBitmap(bitmap)
            } catch (e: Exception) {
                // handle error
            }
        }
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap ->
        bitmap?.let {
            tryOnViewModel.setUserBitmap(it)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Filled.AutoAwesome,
                            contentDescription = null,
                            tint = Color(0xFF7C3AED),
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("AI Virtual Try-On", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (state.isCompleted) {
                        IconButton(onClick = { tryOnViewModel.shareTryOnLook() }) {
                            Icon(Icons.Outlined.Share, contentDescription = "Share")
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp)
        ) {
            // 1. Try-On Interactive Viewport (Live Simulation / Comparison Slider)
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(340.dp),
                    shape = RoundedCornerShape(20.dp),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        if (state.isLoading) {
                            // Loading state with AI scanning effect
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(
                                        Brush.linearGradient(
                                            listOf(Color(0xFF1E1B4B), Color(0xFF312E81))
                                        )
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    CircularProgressIndicator(
                                        color = Color(0xFFA78BFA),
                                        strokeWidth = 4.dp,
                                        modifier = Modifier.size(48.dp)
                                    )
                                    Spacer(modifier = Modifier.height(16.dp))
                                    Text(
                                        "AI Fitting & Draping Engine...",
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        "Aligning shoulders, body contours & realistic fabric drape",
                                        color = Color.White.copy(alpha = 0.8f),
                                        fontSize = 11.sp
                                    )
                                }
                            }
                        } else if (state.isCompleted && state.selectedProduct != null) {
                            // Split Comparison / Try-On result view
                            BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
                                val maxWidthPx = constraints.maxWidth.toFloat()
                                val splitX = maxWidthPx * state.comparisonSplitRatio

                                // Base product / original look
                                AsyncImage(
                                    model = state.selectedProduct?.images?.firstOrNull(),
                                    contentDescription = "Try-On Apparel",
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )

                                // Overlay badge
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = Color.Black.copy(alpha = 0.65f),
                                    modifier = Modifier
                                        .align(Alignment.TopStart)
                                        .padding(12.dp)
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    ) {
                                        Icon(Icons.Filled.AutoAwesome, contentDescription = null, tint = Color(0xFFA78BFA), modifier = Modifier.size(12.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("AI Fitted Preview (${state.selectedSize})", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                    }
                                }

                                // Interactive comparison slider handle
                                Box(
                                    modifier = Modifier
                                        .align(Alignment.Center)
                                        .offset(x = ((state.comparisonSplitRatio - 0.5f) * 260).dp)
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(Color.White, CircleShape)
                                        .border(2.dp, Color(0xFF7C3AED), CircleShape)
                                        .pointerInput(Unit) {
                                            detectDragGestures { change, dragAmount ->
                                                change.consume()
                                                val newRatio = (state.comparisonSplitRatio + (dragAmount.x / maxWidthPx)).coerceIn(0.1f, 0.9f)
                                                tryOnViewModel.updateComparisonSplit(newRatio)
                                            }
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Filled.CompareArrows, contentDescription = "Drag to compare", tint = Color(0xFF7C3AED), modifier = Modifier.size(20.dp))
                                }

                                // View Mode Indicator
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = Color.White.copy(alpha = 0.9f),
                                    modifier = Modifier
                                        .align(Alignment.BottomEnd)
                                        .padding(12.dp)
                                ) {
                                    Text(
                                        text = state.viewMode.name.replace("_", " "),
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF4C1D95),
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }
                        } else {
                            // Initial Upload State
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(
                                        Brush.linearGradient(
                                            listOf(Color(0xFFEDE9FE), Color(0xFFFCE7F3))
                                        )
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier.padding(20.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(64.dp)
                                            .clip(CircleShape)
                                            .background(Color.White),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            Icons.Filled.Face,
                                            contentDescription = null,
                                            tint = Color(0xFF7C3AED),
                                            modifier = Modifier.size(36.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(12.dp))
                                    Text(
                                        "Select Outfit & Photo",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp,
                                        color = Color(0xFF4C1D95)
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        "Upload a photo or use our 3D model to see realistic fitting, draping, and size recommendations.",
                                        fontSize = 11.sp,
                                        textAlign = TextAlign.Center,
                                        color = Color(0xFF6D28D9)
                                    )
                                    Spacer(modifier = Modifier.height(14.dp))
                                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                        Button(
                                            onClick = { cameraLauncher.launch(null) },
                                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7C3AED)),
                                            shape = RoundedCornerShape(10.dp),
                                            modifier = Modifier.testTag("tryon_camera_btn")
                                        ) {
                                            Icon(Icons.Filled.CameraAlt, contentDescription = null, modifier = Modifier.size(16.dp))
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text("Take Selfie", fontSize = 12.sp)
                                        }
                                        OutlinedButton(
                                            onClick = { galleryLauncher.launch("image/*") },
                                            shape = RoundedCornerShape(10.dp),
                                            modifier = Modifier.testTag("tryon_gallery_btn")
                                        ) {
                                            Icon(Icons.Filled.PhotoLibrary, contentDescription = null, modifier = Modifier.size(16.dp))
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text("Gallery", fontSize = 12.sp)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // 2. View Mode Tabs (Front View, Full Body, Detail Fit)
            item {
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    TryOnViewMode.values().forEach { mode ->
                        val isSel = state.viewMode == mode
                        OutlinedButton(
                            onClick = { tryOnViewModel.setViewMode(mode) },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                containerColor = if (isSel) Color(0xFFEDE9FE) else MaterialTheme.colorScheme.surface,
                                contentColor = if (isSel) Color(0xFF7C3AED) else MaterialTheme.colorScheme.onSurface
                            ),
                            border = ButtonDefaults.outlinedButtonBorder.copy(
                                brush = if (isSel) Brush.linearGradient(listOf(Color(0xFF7C3AED), Color(0xFFEC4899)))
                                else Brush.linearGradient(listOf(MaterialTheme.colorScheme.outline.copy(alpha = 0.5f), MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)))
                            )
                        ) {
                            Text(
                                text = when (mode) {
                                    TryOnViewMode.FRONT_VIEW -> "Front View"
                                    TryOnViewMode.FULL_BODY -> "Full Body"
                                    TryOnViewMode.DETAIL_FIT -> "Fit Detail"
                                },
                                fontSize = 11.sp,
                                fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }
            }

            // 3. Select Clothing Item Carousel
            item {
                Spacer(modifier = Modifier.height(16.dp))
                Text("Select Clothing Item to Try On", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                Spacer(modifier = Modifier.height(8.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    items(clothingCatalog) { prod ->
                        val isSelected = state.selectedProduct?.id == prod.id
                        Card(
                            modifier = Modifier
                                .width(130.dp)
                                .clickable { tryOnViewModel.setSelectedProduct(prod) }
                                .testTag("tryon_select_${prod.id}"),
                            shape = RoundedCornerShape(12.dp),
                            border = if (isSelected) ButtonDefaults.outlinedButtonBorder.copy(
                                brush = Brush.linearGradient(listOf(Color(0xFF7C3AED), Color(0xFFEC4899))),
                                width = 2.dp
                            ) else null
                        ) {
                            Column {
                                AsyncImage(
                                    model = prod.images.firstOrNull(),
                                    contentDescription = prod.name,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(100.dp),
                                    contentScale = ContentScale.Crop
                                )
                                Column(modifier = Modifier.padding(6.dp)) {
                                    Text(
                                        text = prod.name,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Text(
                                        text = "$${String.format("%.2f", prod.price)}",
                                        fontSize = 11.sp,
                                        color = FreshGreenPrimary,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // 4. Size & Body Measurement Recommender
            item {
                Spacer(modifier = Modifier.height(16.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Size Selection", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            if (state.result != null) {
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = Color(0xFFEDE9FE)
                                ) {
                                    Text(
                                        text = "AI Recommended: ${state.result!!.recommendedSize}",
                                        color = Color(0xFF6D28D9),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Sizes row
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            listOf("S", "M", "L", "XL", "XXL").forEach { size ->
                                val isSelected = state.selectedSize == size
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (isSelected) Color(0xFF7C3AED) else MaterialTheme.colorScheme.surfaceVariant)
                                        .clickable { tryOnViewModel.setSize(size) },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = size,
                                        color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Body Measurement Sliders
                        Text("Height: ${state.heightCm} cm", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Slider(
                            value = state.heightCm.toFloat(),
                            onValueChange = { tryOnViewModel.setMeasurements(it.toInt(), state.weightKg, state.bodyType) },
                            valueRange = 140f..210f,
                            colors = SliderDefaults.colors(thumbColor = Color(0xFF7C3AED), activeTrackColor = Color(0xFF7C3AED))
                        )

                        Text("Weight: ${state.weightKg} kg", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Slider(
                            value = state.weightKg.toFloat(),
                            onValueChange = { tryOnViewModel.setMeasurements(state.heightCm, it.toInt(), state.bodyType) },
                            valueRange = 40f..130f,
                            colors = SliderDefaults.colors(thumbColor = Color(0xFF7C3AED), activeTrackColor = Color(0xFF7C3AED))
                        )
                    }
                }
            }

            // 5. Generate AI Try-On Action CTA
            item {
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = { tryOnViewModel.triggerAiTryOn() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("btn_generate_ai_tryon"),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF7C3AED)
                    )
                ) {
                    Icon(Icons.Filled.AutoAwesome, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (state.isCompleted) "Re-Generate Fit Preview ✨" else "Generate Realistic AI Try-On ✨",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                }
            }

            // 6. AI Fit & Fabric Draping Report (When Completed)
            if (state.result != null) {
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC))
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Filled.CheckCircle, contentDescription = null, tint = FreshGreenPrimary, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("AI Fit & Draping Assessment", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color(0xFF0F172A))
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = state.result!!.fitAdvice,
                                fontSize = 12.sp,
                                color = Color(0xFF334155),
                                lineHeight = 17.sp
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Fabric Drape Behavior:", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color(0xFF0F172A))
                            Text(
                                text = state.result!!.drapingNotes,
                                fontSize = 12.sp,
                                color = Color(0xFF334155),
                                lineHeight = 17.sp
                            )

                            if (state.result!!.styleMatchingTips.isNotEmpty()) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Text("AI Outfit Pairing Tips:", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color(0xFF0F172A))
                                state.result!!.styleMatchingTips.forEach { tip ->
                                    Text("• $tip", fontSize = 11.sp, color = Color(0xFF475569), modifier = Modifier.padding(vertical = 1.dp))
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Button(
                                    onClick = {
                                        state.selectedProduct?.let { prod ->
                                            shopViewModel.addToCart(prod, 1, state.selectedSize, state.selectedColor)
                                        }
                                    },
                                    modifier = Modifier.weight(1f),
                                    colors = ButtonDefaults.buttonColors(containerColor = FreshGreenPrimary),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Icon(Icons.Filled.ShoppingCart, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Add Fitted Size", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                                OutlinedButton(
                                    onClick = { tryOnViewModel.saveTryOnLook() },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Icon(if (state.isSaved) Icons.Filled.BookmarkAdded else Icons.Outlined.BookmarkBorder, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(if (state.isSaved) "Saved!" else "Save Look", fontSize = 12.sp)
                                }
                            }
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }
}
