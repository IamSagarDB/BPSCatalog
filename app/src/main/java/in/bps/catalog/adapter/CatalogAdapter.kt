package `in`.bps.catalog.adapter

import `in`.bps.catalog.`interface`.OnClickCatalog
import `in`.bps.catalog.databinding.CatalogListItemBinding
import `in`.bps.catalog.model.Catalog
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso

class CatalogAdapter constructor(
    private val onClickCatalog: OnClickCatalog,
    private var catalogResult: List<Catalog>
) : RecyclerView.Adapter<CatalogAdapter.CatalogViewHolder>() {

    inner class CatalogViewHolder(val binding: CatalogListItemBinding) :
        RecyclerView.ViewHolder(binding.root)

    public fun setFilterData(filterData: List<Catalog>) {
        catalogResult = filterData
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CatalogViewHolder {
        return CatalogViewHolder(
            CatalogListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: CatalogViewHolder, position: Int) {
        val catalogItem = catalogResult[position]

        holder.binding.apply {
            productNameListItem.text =
                "${catalogItem.productName_kn} / ${catalogItem.productName_en}"
            productPriceListItem.text = "${catalogItem.productPrice}Rs / ${catalogItem.measureIn}"

            catalogCardView.setOnClickListener {
                onClickCatalog.onClickedCatalog(catalogItem)
            }

            Picasso.get().load(catalogItem.productImage).into(productImageview)
        }
    }

    override fun getItemCount() = catalogResult.size
}