package com.example.reservapp.user.adapter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.reservapp.R
import com.example.reservapp.user.model.CustomerObjectItemList

/**
 * Clase con logica de adapter que tiene dos param ArrayList para mostrar y el eventListener (funcion lambda) para ponerle la logica al
 * item selecionado de la lista y contiene una inner class que es el holder que sera el que haga en binding entre los datos a mostrar
 * y el recyclerView
 */
class ListCategoriesAdapter(private var listCat: ArrayList<CustomerObjectItemList>, private val onClickItem:(CustomerObjectItemList) -> Unit): RecyclerView.Adapter<ListCategoriesAdapter.UsrListViewHolder>(){
   inner class UsrListViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){

       private val iconImage: ImageView = itemView.findViewById(R.id.usr_cat_icon_row_list)
       private val itemTitle: TextView = itemView.findViewById(R.id.usr_cat_title_row_list)

       /**
        * fun que hace el binding tiene los params lista de tipo de la ArrayList y un eventListener
        * al item de la lista que es un objeto de tipo CustomerObjectItemList, una funcion lambda
        * que no devuelve nada Unit equivalente a void de java
        */
       fun bind(item: CustomerObjectItemList, onClickItem: (CustomerObjectItemList) -> Unit) {
           itemTitle.text = item.stringSelected
           iconImage.setImageResource(item.img)
           itemView.setOnClickListener { onClickItem(item) }
       }
   }
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): UsrListViewHolder {
        return UsrListViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.usr_cat_row, parent, false))
    }

    /**
     * fun que tiene dos parameters la lista y la posicion en la que se encuentra
     * en el holder se hace el binding entre estos dos y se le pasa el eventListener de la clase que llama a la
     * fun de la inner class
     */
    override fun onBindViewHolder(holder: UsrListViewHolder, position: Int){
        val currentItem = listCat[position]
        holder.bind(currentItem, onClickItem)

    }

    /**
     * fun que devuelve un int que sera el tamaño de la lista a mostrar
     * en el recyclerView
     */
    override fun getItemCount(): Int {
        return listCat.size
    }

}