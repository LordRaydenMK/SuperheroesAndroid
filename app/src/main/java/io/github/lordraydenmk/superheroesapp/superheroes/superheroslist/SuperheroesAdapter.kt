package io.github.lordraydenmk.superheroesapp.superheroes.superheroslist

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import io.github.lordraydenmk.superheroesapp.R
import io.github.lordraydenmk.superheroesapp.databinding.SuperheroesItemBinding
import io.github.lordraydenmk.superheroesapp.superheroes.superheroslist.SuperheroesAdapter.SuperheroViewHolder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import reactivecircus.flowbinding.android.view.clicks

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

class SuperheroesAdapter(
    private val scope: CoroutineScope
) : ListAdapter<SuperheroViewEntity, SuperheroViewHolder>(diffCallback) {

    private val _actions = MutableSharedFlow<Long>()
    val actions: Flow<Long>
        get() = _actions

    init {
        setHasStableIds(true)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SuperheroViewHolder {
        val binding =
            SuperheroesItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        val holder = SuperheroViewHolder(binding, binding.root)
        scope.launch {
            holder.clicks.map { getItem(it).id }
                .onEach { _actions.emit(it) }
                .collect()
        }
        return holder
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

        val clicks: Flow<Int> = itemView.clicks()
            .flowOn(Dispatchers.Main)
            .map { adapterPosition }

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