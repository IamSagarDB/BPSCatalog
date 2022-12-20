package `in`.bps.catalog.ui

import `in`.bps.catalog.databinding.ActivityUpsertProductBinding
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import com.squareup.picasso.Picasso
import java.io.FileNotFoundException


class UpsertProductActivity : AppCompatActivity() {
    private lateinit var binding: ActivityUpsertProductBinding
    private val SELECT_PHOTO: Int = 2000
    private lateinit var storage: FirebaseStorage
    private lateinit var downloadImageUrl: Uri
    private var isImageUploaded: Boolean = false
    private val db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUpsertProductBinding.inflate(layoutInflater)
        setContentView(binding.root)

        storage = Firebase.storage

        val productNameBundle = intent.getStringExtra("productName")
        val productNameENBundle = intent.getStringExtra("productNameEN")
        val productNameKNBundle = intent.getStringExtra("productNameKN")
        val productPriceBundle = intent.getIntExtra("productPrice", 0)
        val measuredInBundle = intent.getStringExtra("measuredIn")
        val documentId = intent.getStringExtra("documentId")
        val categoryBundle = intent.getStringExtra("category")
        val imageUrl = intent.getStringExtra("imageUrl")

        if (!documentId.equals("new")) {
            binding.productNameKN.setText(productNameKNBundle)
            binding.productNameEN.setText(productNameENBundle)
            binding.productPrice.setText(productPriceBundle.toString())
            binding.productMeasuredIn.setText(measuredInBundle)
            binding.productCategory.setText(categoryBundle)
            downloadImageUrl = Uri.parse(imageUrl)
            Picasso.get()
                .load(downloadImageUrl)
                .into(binding.productImage)
            isImageUploaded = true
        }

        binding.productImage.setOnClickListener {
            val photoPickerIntent = Intent(Intent.ACTION_PICK)
            photoPickerIntent.type = "image/*"
            startActivityForResult(photoPickerIntent, SELECT_PHOTO)
        }

        binding.updateProduct.setOnClickListener {

            val productNameKn = binding.productNameKN.text!!.trim().toString()
            val productNameEN = binding.productNameEN.text!!.trim().toString()
            val productPrice = binding.productPrice.text!!.trim().toString()
            val measure = binding.productMeasuredIn.text!!.trim().toString()
            val category = binding.productCategory.text!!.trim().toString()

            if (productNameKn.isNotEmpty() && productNameEN.isNotEmpty() && measure.isNotEmpty() && category.isNotEmpty() && isImageUploaded && productPrice.isNotEmpty()) {
                val product = hashMapOf(
                    "category" to category.toLowerCase(),
                    "measureIn" to measure.toUpperCase(),
                    "productImage" to downloadImageUrl,
                    "productName_en" to productNameEN,
                    "productName_kn" to productNameKn,
                    "productPrice" to productPrice.toInt()
                )
                val docRef: DocumentReference = if (documentId.equals("new")) {
                    db.collection("catalog").document()
                } else {
                    db.collection("catalog").document(documentId!!)
                }


                docRef.set(product)
                    .addOnSuccessListener {
                        Toast.makeText(
                            this,
                            "updated successfully",
                            Toast.LENGTH_SHORT
                        ).show()
                        startActivity(Intent(this, MainActivity::class.java))
                        finish()
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(
                            this,
                            "failed to update data",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
            } else {
                Toast.makeText(this, "Enter all details", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            SELECT_PHOTO -> if (resultCode == RESULT_OK) {
                try {
                    val imageUri: Uri? = data?.data
                    val sd = getFileName(applicationContext, imageUri!!)
                    val storageRef = storage.reference
                    val uploadTask = storageRef.child("productImg/$sd").putFile(imageUri)

                    uploadTask.addOnSuccessListener {

                        storageRef.child("productImg/$sd").downloadUrl.addOnSuccessListener {
                            downloadImageUrl = it
                            isImageUploaded = true
                            Picasso.get()
                                .load(it)
                                .into(binding.productImage)

                        }.addOnFailureListener {
                            Toast.makeText(this, "Filed to get image", Toast.LENGTH_SHORT).show()
                        }
                    }.addOnFailureListener {
                        Toast.makeText(this, "Failed to upload image", Toast.LENGTH_SHORT).show()
                    }

                } catch (e: FileNotFoundException) {
                    e.printStackTrace()
                }
            }
        }
    }

    @SuppressLint("Range")
    private fun getFileName(context: Context, uri: Uri): String? {
        if (uri.scheme == "content") {
            val cursor = context.contentResolver.query(uri, null, null, null, null)
            cursor.use {
                if (cursor != null) {
                    if (cursor.moveToFirst()) {
                        return cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                    }
                }
            }
        }
        return uri.path?.lastIndexOf('/')?.let { uri.path?.substring(it) }
    }
}