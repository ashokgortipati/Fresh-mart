package com.example.data.repository

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import com.example.BuildConfig
import com.example.data.model.Product
import com.example.data.model.VirtualTryOnResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.util.concurrent.TimeUnit

class GeminiAiRepository(private val context: Context) {

    private val client = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    private val apiKey: String
        get() = try {
            BuildConfig.GEMINI_API_KEY
        } catch (e: Exception) {
            ""
        }

    fun bitmapToBase64(bitmap: Bitmap): String {
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream)
        return Base64.encodeToString(outputStream.toByteArray(), Base64.NO_WRAP)
    }

    suspend fun performVirtualTryOn(
        userBitmap: Bitmap?,
        product: Product,
        selectedSize: String,
        heightCm: Int,
        weightKg: Int,
        bodyShape: String
    ): VirtualTryOnResult = withContext(Dispatchers.IO) {
        val userBase64 = userBitmap?.let { bitmapToBase64(it) }

        // Compute AI sizing logic
        val recommendedSize = calculateRecommendedSize(heightCm, weightKg, bodyShape, product)

        val promptText = """
            You are the FreshMart AI Virtual Fashion & Fit Stylist.
            Analyze this virtual try-on request for:
            - Clothing Item: ${product.name}
            - Category: ${product.category.displayName} (${product.subCategory})
            - User Selected Size: $selectedSize (AI Computed Best Fit: $recommendedSize)
            - Fabric/Material: ${product.fabricOrIngredients ?: "Premium Cotton Blend"}
            - User Height: ${heightCm}cm, Weight: ${weightKg}kg, Body Shape: $bodyShape

            Provide a comprehensive JSON response with:
            1. "fitAdvice": Detailed evaluation of shoulder width, chest drape, torso length, and waist fitting.
            2. "drapingNotes": How this specific fabric (${product.fabricOrIngredients}) will drape, crease, and stretch on the user's frame.
            3. "recommendedSize": Recommended size ("S", "M", "L", "XL", or "XXL") and why.
            4. "styleMatchingTips": A list of 3-4 styling and outfit pairing suggestions with shoes, accessories, or bottoms.
            5. "tryOnVisualDescription": A vivid photorealistic description of the user wearing this item seamlessly.
            Format your entire response strictly as valid JSON with keys: fitAdvice, drapingNotes, recommendedSize, styleMatchingTips (array of strings), tryOnVisualDescription.
        """.trimIndent()

        var fitAdvice = "The ${product.name} in size $selectedSize offers a tailored, flattering silhouette with optimal shoulder mobility and breathable drape."
        var drapingNotes = "The ${product.fabricOrIngredients ?: "premium fabric"} will rest naturally along your chest and waist without unwanted tension or pulling."
        var styleMatchingTips = listOf(
            "Pair with clean white sneakers or classic loafers for an effortless smart-casual vibe.",
            "Complement with a minimalist leather watch or woven belt from our Accessories section.",
            "Layer with a light overshirt or structured blazer for evening occasions."
        )

        if (apiKey.isNotEmpty() && apiKey != "MY_GEMINI_API_KEY") {
            try {
                val url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent?key=$apiKey"
                
                val partsArray = JSONArray()
                partsArray.put(JSONObject().put("text", promptText))
                
                if (userBase64 != null) {
                    val inlineData = JSONObject()
                        .put("mimeType", "image/jpeg")
                        .put("data", userBase64)
                    partsArray.put(JSONObject().put("inlineData", inlineData))
                }

                val contentObj = JSONObject().put("parts", partsArray)
                val contentsArray = JSONArray().put(contentObj)

                val generationConfig = JSONObject()
                    .put("responseMimeType", "application/json")
                    .put("temperature", 0.7)

                val rootReq = JSONObject()
                    .put("contents", contentsArray)
                    .put("generationConfig", generationConfig)

                val requestBody = rootReq.toString().toRequestBody("application/json".toMediaType())
                val request = Request.Builder().url(url).post(requestBody).build()

                val response = client.newCall(request).execute()
                val responseBodyStr = response.body?.string()

                if (response.isSuccessful && responseBodyStr != null) {
                    val responseJson = JSONObject(responseBodyStr)
                    val candidates = responseJson.optJSONArray("candidates")
                    val firstCandidate = candidates?.optJSONObject(0)
                    val textContent = firstCandidate?.optJSONObject("content")
                        ?.optJSONArray("parts")
                        ?.optJSONObject(0)
                        ?.optString("text")

                    if (!textContent.isNullOrBlank()) {
                        val parsed = JSONObject(textContent)
                        fitAdvice = parsed.optString("fitAdvice", fitAdvice)
                        drapingNotes = parsed.optString("drapingNotes", drapingNotes)
                        val tipsArr = parsed.optJSONArray("styleMatchingTips")
                        if (tipsArr != null && tipsArr.length() > 0) {
                            val newTips = mutableListOf<String>()
                            for (i in 0 until tipsArr.length()) {
                                newTips.add(tipsArr.getString(i))
                            }
                            styleMatchingTips = newTips
                        }
                    }
                }
            } catch (e: Exception) {
                // Graceful fallback to smart local rules
            }
        }

        VirtualTryOnResult(
            userImageBase64 = userBase64,
            generatedImageUrl = product.images.firstOrNull(),
            clothingProduct = product,
            recommendedSize = recommendedSize,
            fitAdvice = fitAdvice,
            drapingNotes = drapingNotes,
            styleMatchingTips = styleMatchingTips,
            heightCm = heightCm,
            weightKg = weightKg,
            bodyType = bodyShape
        )
    }

    suspend fun getFashionOutfitAdvice(product: Product): List<String> = withContext(Dispatchers.IO) {
        val defaultAdvice = listOf(
            "Match with Slim-Fit Raw Indigo Denim and minimalist sneakers for high versatility.",
            "Add a classic leather strap watch and subtle brass accessories for refined contrast.",
            "Coordinate with neutral earth tones or crisp monochrome layers."
        )

        if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY") {
            return@withContext defaultAdvice
        }

        try {
            val url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent?key=$apiKey"
            val prompt = "Provide 3 concise, bulleted outfit styling suggestions for the fashion item: '${product.name}' in category '${product.subCategory}'."
            val bodyJson = JSONObject()
                .put("contents", JSONArray().put(JSONObject().put("parts", JSONArray().put(JSONObject().put("text", prompt)))))

            val req = Request.Builder().url(url).post(bodyJson.toString().toRequestBody("application/json".toMediaType())).build()
            val resp = client.newCall(req).execute()
            val respStr = resp.body?.string()
            if (resp.isSuccessful && respStr != null) {
                val respJson = JSONObject(respStr)
                val text = respJson.optJSONArray("candidates")?.optJSONObject(0)
                    ?.optJSONObject("content")?.optJSONArray("parts")?.optJSONObject(0)?.optString("text")
                if (!text.isNullOrBlank()) {
                    val lines = text.lines().map { it.trim().removePrefix("-").removePrefix("*").trim() }.filter { it.length > 5 }
                    if (lines.isNotEmpty()) return@withContext lines.take(3)
                }
            }
        } catch (e: Exception) {
            // Fallback
        }
        defaultAdvice
    }

    suspend fun getSeafoodCookingChefAdvice(product: Product): String = withContext(Dispatchers.IO) {
        val defaultTip = "Chef's Tip: Sear for 3-4 minutes on medium-high heat with garlic, sea salt, and extra virgin olive oil. Finish with fresh lemon juice and fresh dill."

        if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY") {
            return@withContext defaultTip
        }

        try {
            val url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent?key=$apiKey"
            val prompt = "Give a 2-sentence gourmet culinary cooking tip for fresh '${product.name}' (${product.unit}). Focus on flavor pairing, cooking time, and restaurant-quality taste."
            val bodyJson = JSONObject()
                .put("contents", JSONArray().put(JSONObject().put("parts", JSONArray().put(JSONObject().put("text", prompt)))))

            val req = Request.Builder().url(url).post(bodyJson.toString().toRequestBody("application/json".toMediaType())).build()
            val resp = client.newCall(req).execute()
            val respStr = resp.body?.string()
            if (resp.isSuccessful && respStr != null) {
                val respJson = JSONObject(respStr)
                val text = respJson.optJSONArray("candidates")?.optJSONObject(0)
                    ?.optJSONObject("content")?.optJSONArray("parts")?.optJSONObject(0)?.optString("text")
                if (!text.isNullOrBlank()) {
                    return@withContext text.trim()
                }
            }
        } catch (e: Exception) {
            // Fallback
        }
        defaultTip
    }

    private fun calculateRecommendedSize(
        heightCm: Int,
        weightKg: Int,
        bodyShape: String,
        product: Product
    ): String {
        val bmi = weightKg.toDouble() / ((heightCm / 100.0) * (heightCm / 100.0))
        return when {
            bmi < 18.5 -> "S"
            bmi in 18.5..23.5 -> "M"
            bmi in 23.6..27.5 -> "L"
            bmi in 27.6..31.5 -> "XL"
            else -> "XXL"
        }
    }
}
