package io.github.lordraydenmk.superheroesapp.superheroes.presentation

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import io.github.lordraydenmk.superheroesapp.R
import io.github.lordraydenmk.superheroesapp.databinding.SuperheroesItemBinding
import io.github.lordraydenmk.superheroesapp.superheroes.presentation.SuperheroesAdapter.SuperheroViewHolder

private val diffCallback = object : DiffUtil.ItemCallback<SuperheroViewEntity>() {

    override fun areItemsTheSame(
        oldItem: SuperheroViewEntity,
        newItem: SuperheroViewEntity
    ): Boolean = oldItem.id == newItem.id

    override fun areContentsTheSame(
        oldItem: SuperheroViewEntity,
        newItem: SuperheroViewEntity
    ): Boolean = oldItem == newItem

}

class SuperheroesAdapter : ListAdapter<SuperheroViewEntity, SuperheroViewHolder>(diffCallback) {

    init {
        setHasStableIds(true)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SuperheroViewHolder {
        val binding =
            SuperheroesItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SuperheroViewHolder(binding, binding.root)
    }

    override fun onBindViewHolder(holder: SuperheroViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    override fun getItemId(position: Int): Long {
        return getItem(position).id
    }

    class SuperheroViewHolder(
        private val binding: SuperheroesItemBinding,
        itemView: View
    ) : RecyclerView.ViewHolder(itemView) {

        fun bind(item: SuperheroViewEntity) = with(binding) {
            ivSuperhero.load(item.imageUrl) {
                placeholder(R.drawable.ic_hourglass_bottom_black)
                error(R.drawable.ic_baseline_broken_image)
                crossfade(true)
            }
            tvSuperheroName.text = item.name
        }
    }
}