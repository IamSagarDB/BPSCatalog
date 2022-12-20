package `in`.bps.catalog.model

import com.google.firebase.firestore.Exclude

data class Catalog(
    @Exclude var documentId: String,
    val productPrice: Int,
    val productName_en: String,
    val productName_kn: String,
    val measureIn: String,
    val category: String,
    val productImage: String
) {
    constructor() : this("",0, "", "", "", "", "")
}
