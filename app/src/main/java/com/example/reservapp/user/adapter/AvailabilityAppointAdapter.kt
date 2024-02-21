package com.example.reservapp.user.adapter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.reservapp.R



class AvailabilityAppointAdapter(private var availability: ArrayList<String>, private val onClickItem:(String) -> Unit): RecyclerView.Adapter<AvailabilityAppointAdapter.AvailabilityAppointViewHolder>() {

    inner class AvailabilityAppointViewHolder(itemView: View):RecyclerView.ViewHolder(itemView){
        private var txtHourAvailable: TextView = itemView.findViewById(R.id.textViewHourItem)
        private var iconCalendar: ImageView = itemView.findViewById(R.id.imageButtonCalendar)

        fun bind(item: String, onClickItem:(String) -> Unit) {
            txtHourAvailable.text = item
            iconCalendar.setImageResource(R.drawable.icon_calendar)
            itemView.setOnClickListener { onClickItem(item) }
        }
    }
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AvailabilityAppointViewHolder {
        return AvailabilityAppointViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.usr_appointment_detail_row, parent, false))
    }

    override fun getItemCount(): Int {
        return availability.size
    }

    override fun onBindViewHolder(holder: AvailabilityAppointViewHolder, position: Int) {
        val currentItem = availability[position]
        holder.bind(currentItem, onClickItem)
    }

    fun setAvailabilityHours(availabilityHours: ArrayList<String>) {
        this.availability = availabilityHours
        notifyDataSetChanged()
    }
}