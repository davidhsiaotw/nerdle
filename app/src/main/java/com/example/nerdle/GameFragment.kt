package com.example.nerdle

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.view.setPadding
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.nerdle.databinding.FragmentGameBinding
import com.google.android.material.button.MaterialButton
import com.google.android.material.button.MaterialButtonToggleGroup
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class GameFragment : Fragment() {
    private lateinit var binding: FragmentGameBinding
    private val gridViewModel: GridViewModel by activityViewModels()

    // This property is only valid between onCreateView and
    // onDestroyView.
//    private val binding get() = _binding!
    private lateinit var gameRecyclerView: RecyclerView
    private lateinit var numberButtonToggle: MaterialButtonToggleGroup
    private lateinit var operatorButtonToggle: MaterialButtonToggleGroup
    private lateinit var operatorButtonToggle1: MaterialButtonToggleGroup

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_game, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//
//        // initialize game recycler view
//        gameRecyclerView = view.findViewById(R.id.blocks_recycler_view)
//        val width = answer.length
//        val disableScrollGridLayoutManager = object : GridLayoutManager(requireContext(), width) {
//            override fun canScrollVertically(): Boolean {
//                return false
//            }
//        }
//        gameRecyclerView.layoutManager = disableScrollGridLayoutManager

        initializeLayout(view)
        // create game adapter
//        gameRecyclerView.adapter = GameAdapter(GridListener { grid ->
//            val previousPosition = gridViewModel.onGridClicked(grid)
//            (gameRecyclerView.adapter as GameAdapter).submitList(gridViewModel.allGrids.value)
//            if (previousPosition != null)
//                (gameRecyclerView.adapter as GameAdapter).notifyItemChanged(previousPosition)
//            (gameRecyclerView.adapter as GameAdapter).notifyItemChanged(grid.index)
//        })
//        (gameRecyclerView.adapter as GameAdapter).submitList(gridViewModel.allGrids.value)

//        gridViewModel.allGrids.observe(viewLifecycleOwner) { list ->
//            (gameRecyclerView.adapter as GameAdapter).submitList(list)
//        }

        // initialize number toggle buttons
        numberButtonToggle = view.findViewById(R.id.number_toggle_button)

        // initialize number buttons: 0~9
        for (i in 0..9) {
            val button = MaterialButton(requireContext())
            button.text = i.toString()
            button.setPadding(0)
            val params = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT)
            params.weight = 1F
            button.layoutParams = params
            button.setOnClickListener {
                val index: Int? = gridViewModel.selectedIndex.value
                if (index != null) {
                    val selectedGrid = gridViewModel.allGrids.value?.get(index)
                    selectedGrid?.text = i.toString()
                    gridViewModel.allGrids.value?.set(index, selectedGrid!!)
                    (gameRecyclerView.adapter as GameAdapter).notifyItemChanged(index)
                }
            }
            numberButtonToggle.addView(button)
        }

        // initialize operator toggle buttons
        operatorButtonToggle = view.findViewById(R.id.operator_toggle_button)

        // initialize operator buttons: +, -, *, /
        val operator1 = listOf("+", "-", "*", "/", "=")
        for (operator in operator1) {
            val button = MaterialButton(requireContext())
            val params = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT)
            params.weight = 1F
            button.layoutParams = params
            button.text = operator
            button.setOnClickListener {
                val index = gridViewModel.selectedIndex.value
                if (index != null) {
                    val selectedGrid = gridViewModel.allGrids.value?.get(index)
                    selectedGrid?.text = operator
                    gridViewModel.allGrids.value?.set(index, selectedGrid!!)
                    (gameRecyclerView.adapter as GameAdapter).notifyItemChanged(index)
                }
            }
            operatorButtonToggle.addView(button)
        }

        // initialize operator toggle buttons
        operatorButtonToggle1 = view.findViewById(R.id.operator_toggle_button1)

        // initialize operator buttons: Enter, Delete
        val operator2 = listOf("Enter", "Delete", "Reset")
        for (operator in operator2) {
            val button = MaterialButton(requireContext())
            button.text = operator
            button.setOnClickListener {
                // if enter is pressed, collect buttons' text in a row, check if it is valid and
                // correct
                if (button.text == "Enter") {
                    val start = gridViewModel.startIndex.value!!
                    val end = gridViewModel.endIndex.value!!
                    val input = gridViewModel.allGrids.value!!.subList(start, end + 1)

                    val errorMessage = gridViewModel.isValid(input)
                    if (errorMessage != null)
                        Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show()
                    else {
                        if (gridViewModel.isCorrect(input)) {
                            Toast.makeText(context, "YOU WIN!", Toast.LENGTH_SHORT).show()
                            for (i in start..end) {
                                val grid = gridViewModel.allGrids.value!![i]
                                grid.color = Color.parseColor("#33AA00")
                                grid.selectable = false
                                grid.isSelected = false
                                gridViewModel.allGrids.value!![i] = grid
                            }

                            (gameRecyclerView.adapter as GameAdapter).notifyItemRangeChanged(
                                start, input.size
                            )
                        } else {
                            Toast.makeText(context, "WRONG ANSWER!", Toast.LENGTH_SHORT).show()
                            gridViewModel.moveToNextRow()
                            (gameRecyclerView.adapter as GameAdapter).notifyItemRangeChanged(
                                start, input.size * 2
                            )

                            if (gridViewModel.chance.value == 0) {
                                Toast.makeText(
                                    context,
                                    "YOU LOST! Answer is ${gridViewModel.answer.value}",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }
                    }
                }

                // if delete is pressed, delete the selected button's text
                if (button.text == "Delete") {
                    val index = gridViewModel.selectedIndex.value
                    if (index != null) {
                        val selectedGrid = gridViewModel.allGrids.value?.get(index)
                        selectedGrid?.text = ""
                        gridViewModel.allGrids.value?.set(index, selectedGrid!!)
                        (gameRecyclerView.adapter as GameAdapter).notifyItemChanged(index)
                    }
                    // TODO: make current material button unchecked

                    // TODO: make previous material button checked
                }

                // if reset is pressed, reset the recycler view
                if (button.text == "Reset") {
                    gridViewModel.initializeGameViewModel()
                    initializeLayout(view)
                }
            }
            operatorButtonToggle1.addView(button)
        }

        MaterialAlertDialogBuilder(requireContext()).setMessage("This is instruction")
            .setTitle("How to play mini Nerdle")
    }

    private fun initializeLayout(view: View) {
        val answer = gridViewModel.answer.value.toString()
        println("==========answer is ${answer}==========")
        // initialize game recycler view
        gameRecyclerView = view.findViewById(R.id.blocks_recycler_view)
        val width = answer.length
        val disableScrollGridLayoutManager = object : GridLayoutManager(requireContext(), width) {
            override fun canScrollVertically(): Boolean {
                return false
            }
        }
        gameRecyclerView.layoutManager = disableScrollGridLayoutManager

        // create game adapter
        gameRecyclerView.adapter = GameAdapter(GridListener { grid ->
            val previousPosition = gridViewModel.onGridClicked(grid)
            (gameRecyclerView.adapter as GameAdapter).submitList(gridViewModel.allGrids.value)
            if (previousPosition != null)
                (gameRecyclerView.adapter as GameAdapter).notifyItemChanged(previousPosition)
            (gameRecyclerView.adapter as GameAdapter).notifyItemChanged(grid.index)
        })
        (gameRecyclerView.adapter as GameAdapter).submitList(gridViewModel.allGrids.value)
    }

}