package onyx.movil

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import onyx.movil.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        /* TOOLBAR */
        this.setSupportActionBar(binding.toolbar)
    }
}
