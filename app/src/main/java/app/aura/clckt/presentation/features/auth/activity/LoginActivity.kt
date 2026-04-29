package app.aura.clckt.presentation.features.auth.activity

import android.content.Intent
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import app.aura.clckt.R
import app.aura.clckt.databinding.ActivityLoginBinding
import app.aura.clckt.presentation.features.dashboard.DashboardActivity

class LoginActivity : AppCompatActivity() {
    private lateinit var binding : ActivityLoginBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val text = "Your Aura\nAwaits"
        val spannableString = SpannableString(text)
        val neonColor = ContextCompat.getColor(this, R.color.neon_color)
        spannableString.setSpan(
            ForegroundColorSpan(neonColor),
            5,
            9,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        binding.tvHeading.text = spannableString

        binding.tvSkip.setOnClickListener {
            Intent(this@LoginActivity, DashboardActivity::class.java).also {
                startActivity(it)
                finish()
            }
        }
    }
}