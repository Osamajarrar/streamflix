package com.tanasi.streamflix.fragments.genre

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.tanasi.streamflix.R
import com.tanasi.streamflix.adapters.AppAdapter
import com.tanasi.streamflix.databinding.FragmentGenreBinding
import com.tanasi.streamflix.models.Genre
import com.tanasi.streamflix.models.Movie
import com.tanasi.streamflix.models.TvShow
import com.tanasi.streamflix.utils.viewModelsFactory

class GenreFragment : Fragment() {

    private var _binding: FragmentGenreBinding? = null
    private val binding get() = _binding!!

    private val args by navArgs<GenreFragmentArgs>()
    private val viewModel by viewModelsFactory { GenreViewModel(args.id) }

    private val appAdapter = AppAdapter()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGenreBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initializeGenre()

        viewModel.state.observe(viewLifecycleOwner) { state ->
            when (state) {
                GenreViewModel.State.Loading -> binding.isLoading.root.visibility = View.VISIBLE
                is GenreViewModel.State.SuccessLoading -> {
                    displayGenre(state.genre)
                    binding.isLoading.root.visibility = View.GONE
                }
                is GenreViewModel.State.FailedLoading -> {
                    Toast.makeText(
                        requireContext(),
                        state.error.message ?: "",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    private fun initializeGenre() {
        binding.tvGenreName.text = getString(R.string.genre_header_name, args.name)

        binding.vgvGenre.apply {
            adapter = appAdapter
            setItemSpacing(requireContext().resources.getDimension(R.dimen.genre_spacing).toInt())
        }
    }

    private fun displayGenre(genre: Genre) {
        appAdapter.items.apply {
            clear()
            addAll(genre.shows.onEach {
                when (it) {
                    is Movie -> it.itemType = AppAdapter.Type.MOVIE_GRID_ITEM
                    is TvShow -> it.itemType = AppAdapter.Type.TV_SHOW_GRID_ITEM
                }
            })
        }
        appAdapter.notifyDataSetChanged()
    }
}