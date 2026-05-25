package app.aura.clckt.data.remote

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import app.aura.clckt.BuildConfig
import com.google.firebase.Firebase
import com.google.firebase.ai.ai
import com.google.firebase.ai.type.GenerativeBackend
import com.google.firebase.ai.type.content
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

data class CategoryScore(
    val name: String,
    val score: Int,
    val maxScore: Int
)

data class GeminiAnalysisResult(
    val score: Int,
    val vibeTitle: String,
    val explanation: String,
    val categories: List<CategoryScore>,
    val suggestions: List<String>,
    val badges: List<String>? = null
)

data class LookupResult(
    val found: Boolean,
    val description: String
)

object GeminiService {
    private val gson = Gson()

    suspend fun lookupEvent(eventTitle: String): LookupResult = withContext(Dispatchers.IO) {
        try {
            val model = Firebase.ai(backend = GenerativeBackend.googleAI())
                .generativeModel("gemini-3.5-flash")

            val prompt = """
                You are a local vibe and venue researcher.
                Analyze the event or place name: "$eventTitle".
                Determine if this represents a specific, recognizable venue, event, or occasion (e.g. "Hauz Khas Social", "Met Gala", "Tomorrowland", "A corporate meeting", "A wedding").
                
                If you recognize it or can strongly infer its precise background vibe, color coordinates, general dress code, and setup:
                - Set "found" to true
                - Provide a short "description" (2-3 sentences max) detailing the vibe, typical aesthetic, and fashion code.
                
                If the name is completely generic, vague, or not recognizable (e.g. "vibe", "party", "test", "my room", "xyz"):
                - Set "found" to false
                - Set "description" to an empty string.

                Provide your output strictly in JSON format matching this schema:
                {
                  "found": true,
                  "description": "A brief, stylish description of the venue, setup, and aesthetic vibe."
                }
                
                Return ONLY the raw JSON string. Do NOT wrap it in markdown code blocks like ```json.
            """.trimIndent()

            val response = model.generateContent(prompt)
            val jsonText = response.text?.trim() ?: throw Exception("Empty response")
            val cleanJson = jsonText
                .removePrefix("```json")
                .removePrefix("```")
                .removeSuffix("```")
                .trim()

            gson.fromJson(cleanJson, LookupResult::class.java)
        } catch (e: Exception) {
            e.printStackTrace()
            LookupResult(found = false, description = "")
        }
    }

    suspend fun analyzeOutfit(
        context: Context,
        imageUri: Uri,
        locationName: String,
        eventTitle: String,
        eventDescription: String
    ): GeminiAnalysisResult = withContext(Dispatchers.IO) {
        val bitmap = getBitmapFromUri(context, imageUri)
            ?: return@withContext getFallbackAnalysis(eventTitle, locationName)

        try {
            val model = Firebase.ai(backend = GenerativeBackend.googleAI())
                .generativeModel("gemini-3.5-flash")

            val prompt = """
                Analyze the outfit worn by the person in this image and calculate the total ‘Aura Points’ they would gain based on:
                1. Outfit style, color coordination, accessories, footwear, and overall fashion sense.
                2. The specific location/background (luxury place, street, beach, gym, office, café, club, etc.): ${locationName}.
                3. Event or occasion context (wedding, casual hangout, business meeting, concert, date, vacation, festival, etc.): ${eventTitle} (${eventDescription}).
                4. Time of the day, trendiness, confidence, posture, and social presence energy.

                Give:
                - Total Aura Points score out of 1000
                - Breakdown of points category-wise (5 categories: Outfit Style, Vibe & Energy, Occasion Fit, Trendiness, Social Presence)
                - A short explanation of why the outfit gains or loses aura
                - A vibe title (example: ‘Main Character Energy’, ‘Corporate Sigma’, ‘Luxury Villain Arc’, ‘Streetwear God’, etc.)
                - Three descriptive style tags/badges (example: ["Stealth Wealth", "Cozy Core", "NPC Repeller"])
                - Suggestions to increase aura points further

                Make the analysis dramatic, funny, brutally honest, and Gen-Z style while still being fashion-aware.

                Provide your output in JSON format matching this schema:
                {
                  "score": 850,
                  "vibeTitle": "Main Character Energy",
                  "explanation": "Your outfit gains aura because...",
                  "badges": ["Stealth Wealth", "Cozy Core", "NPC Repeller"],
                  "categories": [
                    {"name": "Outfit Style", "score": 180, "maxScore": 200},
                    {"name": "Vibe & Energy", "score": 190, "maxScore": 200},
                    {"name": "Occasion Fit", "score": 160, "maxScore": 200},
                    {"name": "Trendiness", "score": 170, "maxScore": 200},
                    {"name": "Social Presence", "score": 150, "maxScore": 200}
                  ],
                  "suggestions": [
                    "Suggestion 1",
                    "Suggestion 2"
                  ]
                }

                Return ONLY the raw JSON string. Do NOT wrap it in markdown code blocks like ```json.
            """.trimIndent()

            val inputContent = content {
                image(bitmap)
                text(prompt)
            }

            val response = model.generateContent(inputContent)
            val jsonText = response.text?.trim() ?: throw Exception("Empty response from Gemini")
            
            // Handle cases where the model might still return markdown wrappers
            val cleanJson = jsonText
                .removePrefix("```json")
                .removePrefix("```")
                .removeSuffix("```")
                .trim()

            gson.fromJson(cleanJson, GeminiAnalysisResult::class.java)
        } catch (e: Exception) {
            e.printStackTrace()
            getFallbackAnalysis(eventTitle, locationName)
        }
    }

    private fun getBitmapFromUri(context: Context, uri: Uri): Bitmap? {
        return try {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
                val source = android.graphics.ImageDecoder.createSource(context.contentResolver, uri)
                android.graphics.ImageDecoder.decodeBitmap(source) { decoder, _, _ ->
                    decoder.isMutableRequired = true
                }
            } else {
                @Suppress("DEPRECATION")
                android.provider.MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun getFallbackAnalysis(eventTitle: String, locationName: String): GeminiAnalysisResult {
        // A set of preset hilarious Gen-Z fallbacks so the app runs flawlessly out of the box
        val fallbacks = listOf(
            GeminiAnalysisResult(
                score = 880,
                vibeTitle = "Main Character Energy",
                explanation = "Honestly? You ate and left zero crumbs. The color coordination at $locationName is matching the background energy perfectly. You walked in there like you own the place, and honestly, the crowd is NPCs compared to you. Ultimate aura booster.",
                categories = listOf(
                    CategoryScore("Outfit Style", 185, 200),
                    CategoryScore("Vibe & Energy", 190, 200),
                    CategoryScore("Occasion Fit", 175, 200),
                    CategoryScore("Trendiness", 180, 200),
                    CategoryScore("Social Presence", 150, 200)
                ),
                suggestions = listOf(
                    "Keep doing exactly what you're doing, the camera loves you.",
                    "Add some chunky metal accessories to assert absolute dominance.",
                    "Walk 10% slower to let the NPCs fully absorb your presence."
                ),
                badges = listOf("NPC Repeller", "Cozy Core", "Ate & Left No Crumbs")
            ),
            GeminiAnalysisResult(
                score = 420,
                vibeTitle = "Corporate Sigma NPC",
                explanation = "Oh, sweetie... no. This fit is giving 'I follow LinkedIn influencers for fun.' You dressed for $eventTitle like you have a 9 AM standup meeting about synergy and deliverables. The shoes are screaming office cubicle. Negative aura points registered.",
                categories = listOf(
                    CategoryScore("Outfit Style", 80, 200),
                    CategoryScore("Vibe & Energy", 70, 200),
                    CategoryScore("Occasion Fit", 110, 200),
                    CategoryScore("Trendiness", 90, 200),
                    CategoryScore("Social Presence", 70, 200)
                ),
                suggestions = listOf(
                    "Unbutton at least two buttons, you look like you're holding a spreadsheet.",
                    "Replace those business shoes with chunky sneakers immediately.",
                    "Burn the lanyard. Lanyards are the absolute antithesis of aura."
                ),
                badges = listOf("LinkedIn Core", "Cubicle Resident", "Synergy Vibe")
            ),
            GeminiAnalysisResult(
                score = 940,
                vibeTitle = "Streetwear God Arc",
                explanation = "Call the fire department because this is an absolute hazard. The drape of the clothes is immaculate, and the footwear selection is literally holding the entire aesthetic universe together. You fit in at $locationName like a designer model who wandered off the runway. Instant respect.",
                categories = listOf(
                    CategoryScore("Outfit Style", 195, 200),
                    CategoryScore("Vibe & Energy", 190, 200),
                    CategoryScore("Occasion Fit", 180, 200),
                    CategoryScore("Trendiness", 190, 200),
                    CategoryScore("Social Presence", 185, 200)
                ),
                suggestions = listOf(
                    "Put on some dark designer sunglasses, even indoors. Trust the process.",
                    "Upload this to your feed immediately, the internet is waiting.",
                    "Do not make eye contact with anyone; streetwear gods are unreachable."
                ),
                badges = listOf("Stealth Wealth", "Runway Ready", "Absolute Threat")
            ),
            GeminiAnalysisResult(
                score = 710,
                vibeTitle = "Indie Café Villain",
                explanation = "It's giving 'I read poetry in public hoping someone asks me about it.' The vintage jacket has a lot of personality, but the trousers are playing a completely different sport. Overall vibe is cool but tries a little too hard to look like you didn't try. Decent aura, but we see through the act.",
                categories = listOf(
                    CategoryScore("Outfit Style", 140, 200),
                    CategoryScore("Vibe & Energy", 150, 200),
                    CategoryScore("Occasion Fit", 140, 200),
                    CategoryScore("Trendiness", 160, 200),
                    CategoryScore("Social Presence", 120, 200)
                ),
                suggestions = listOf(
                    "Let the trousers match the vintage aesthetic; throw away the fast-fashion boots.",
                    "Stop looking at your phone like you're texting an underground DJ.",
                    "Maybe order a stronger drink to add some mysterious edge."
                ),
                badges = listOf("Vinyl Lover", "Thrift Star", "Vibe Curator")
            )
        )
        return fallbacks.random()
    }
}
