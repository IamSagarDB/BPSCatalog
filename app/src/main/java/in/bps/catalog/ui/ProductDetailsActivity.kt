package `in`.bps.catalog.ui

import `in`.bps.catalog.databinding.ActivityProductDetailsBinding
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlin.math.ceil
import kotlin.math.floor

class ProductDetailsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProductDetailsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProductDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val productName = intent.getStringExtra("productName")
        val productNameEN = intent.getStringExtra("productNameEN")
        val productNameKN = intent.getStringExtra("productNameKN")
        val productPrice = intent.getIntExtra("productPrice", 0)
        val measuredIn = intent.getStringExtra("measuredIn")
        val documentId = intent.getStringExtra("documentId")
        val category = intent.getStringExtra("category")
        val imageUrl = intent.getStringExtra("imageUrl")

        binding.updateProduct.setOnClickListener {
            val intent = Intent(this, UpsertProductActivity::class.java)
            intent.putExtra("productName", productName)
            intent.putExtra("productNameEN", productNameEN)
            intent.putExtra("productNameKN", productNameKN)
            intent.putExtra("productPrice", productPrice)
            intent.putExtra("measuredIn", measuredIn)
            intent.putExtra("documentId", documentId)
            intent.putExtra("category", category)
            intent.putExtra("imageUrl", imageUrl)
            startActivity(intent)
        }

        binding.productName.text = "Product Name: $productName"
        binding.productPrice.text = "Price: $productPrice Rs / $measuredIn"


        binding.price50gm.text = "${ceil((productPrice * 0.05)).toInt()} Rs"
        binding.price100gm.text = "${ceil((productPrice * 0.1)).toInt()} Rs"
        binding.price250gm.text = "${ceil((productPrice * 0.25)).toInt()} Rs"
        binding.price500gm.text = "${ceil((productPrice * 0.5)).toInt()} Rs"
        binding.price750gm.text = "${ceil((productPrice * 0.75)).toInt()} Rs"
        binding.price1kg.text = "$productPrice Rs"

        binding.getWeightButton.setOnClickListener {
            val amount: String = binding.inputAmountET.text.toString().trim()

            if (amount.isEmpty()) {
                binding.amountToWeightTV.text = "Please Enter The Amount"
            } else {
                val amountDouble: Double = amount.toDouble()
                val actualPrice: Double = productPrice.toDouble()
                val price: Double = amountDouble / actualPrice
                val weightInGram: Double = (price * 1000.0)
                binding.amountToWeightTV.text = "${floor(weightInGram)} gm for $amount Rs"
            }
        }
    }
}