package `in`.bps.catalog.ui

import `in`.bps.catalog.R
import `in`.bps.catalog.`interface`.OnClickCatalog
import `in`.bps.catalog.adapter.CatalogAdapter
import `in`.bps.catalog.databinding.ActivityMainBinding
import `in`.bps.catalog.model.Catalog
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var auth: FirebaseAuth
    private val db = Firebase.firestore
    private val _catalog = ArrayList<Catalog>()
    private val TAG = "MAinActivity"
    private lateinit var catalogAdapter: CatalogAdapter

    override fun onStart() {
        super.onStart()
        auth = Firebase.auth
        val currentUser = auth.currentUser;

        if (currentUser == null) {
            startActivity(Intent(this, AuthActivity::class.java))
            finish()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        catalogAdapter = CatalogAdapter(object : OnClickCatalog {
            override fun onClickedCatalog(item: Catalog) {
                val intent = Intent(this@MainActivity, ProductDetailsActivity::class.java)
                intent.putExtra("productName", item.productName_kn + " / " + item.productName_en)
                intent.putExtra("productNameEN", item.productName_en)
                intent.putExtra("productNameKN", item.productName_kn)
                intent.putExtra("productPrice", item.productPrice)
                intent.putExtra("measuredIn", item.measureIn)
                intent.putExtra("category", item.category)
                intent.putExtra("documentId", item.documentId)
                intent.putExtra("imageUrl", item.productImage)
                startActivity(intent)
            }
        }, _catalog)

        getCatalogData()

        // new Product

        binding.addProduct.setOnClickListener {
            val intent = Intent(this, UpsertProductActivity::class.java)
            intent.putExtra("documentId", "new")
            intent.putExtra("productName", "new")
            intent.putExtra("productNameEN", "new")
            intent.putExtra("productNameKN", "new")
            intent.putExtra("productPrice", "new")
            intent.putExtra("measuredIn", "new")
            intent.putExtra("category", "new")
            intent.putExtra("imageUrl", "new")
            startActivity(intent)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.nav_menu, menu)
        val search : MenuItem? = menu?.findItem(R.id.nav_search)
        val searchView = search?.actionView as SearchView
        searchView.queryHint = "Search Item"

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filterList(newText)
                return true
            }

        })

        return super.onCreateOptionsMenu(menu)
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun filterList(newText: String?) {
        val filterList = ArrayList<Catalog>()
        for (item in _catalog) {
            if (item.productName_kn.toLowerCase().contains(newText!!.toLowerCase())) {
                filterList.add(item)
            }
        }
        if (filterList.isEmpty()) {
            Toast.makeText(this, "No data found", Toast.LENGTH_SHORT).show()
        } else {
            catalogAdapter.setFilterData(filterList)
        }
    }

    private fun getCatalogData() {
        db.collection("catalog").orderBy("productName_kn")
            .get()
            .addOnSuccessListener { result ->
                _catalog.clear()
                for (document in result.documents) {
                    val catalog: Catalog? = document.toObject(Catalog::class.java)
                    if (catalog != null) {
                        catalog.documentId = document.id
                        _catalog.add(catalog)
                    }
                }

                binding.catalogRV.apply {
                    adapter = catalogAdapter
                    setHasFixedSize(true)
                }
            }
            .addOnFailureListener { exception ->
                Log.d(TAG, "Error: " + exception.message)
            }
    }

    override fun onRestart() {
        super.onRestart()
        getCatalogData()
    }
}