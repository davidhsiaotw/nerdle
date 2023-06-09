package com.example.nerdle

import android.graphics.Color
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import org.junit.Assert.*

import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.mock
import java.util.logging.Logger
import kotlin.random.Random.Default.nextInt

class GridViewModelTest {
    private lateinit var gridViewModel: GridViewModel
    private val logger = Logger.getLogger(GridViewModelTest::class.java.name)

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setUp() {
        gridViewModel = GridViewModel()
    }

//    @After
//    fun tearDown() {
//    }

    @Test
    @Suppress("UNCHECKED_CAST")
    fun `is answer correctly generated`() {
        try {
            val observer: Observer<String> = mock(Observer::class.java) as Observer<String>
            gridViewModel.answer.observeForever(observer)
            val answer = gridViewModel.answer.value
            var pass: Boolean

            // check if answer is in the correct format
            pass = answer?.matches("^\\d[+\\-*/]\\d=\\d".toRegex()) == true

            // check if answer is a valid equation
            if (pass) {
                var number1 = Int.MIN_VALUE
                var operator = ""
                var number2 = Int.MIN_VALUE
                var number3 = Int.MIN_VALUE
                var temp = 0
                for (i in 0 until answer?.length!!) {
                    if (answer[i] == '+' || answer[i] == '-' || answer[i] == '*' || answer[i] == '/') {
                        operator = answer[i].toString()
                        number1 = answer.substring(0, i - 1).toInt()
                        temp = i + 1
                    } else if (answer[i] == '=') {
                        number2 = answer.substring(temp, i - 1).toInt()
                        number3 = answer.substring(i + 1, answer.length).toInt()
                        break
                    }
                }

                pass = when (operator) {
                    "+" -> {
                        number1 + number2 == number3
                    }
                    "*" -> {
                        number1 * number2 == number3
                    }
                    "-" -> {
                        number1 - number2 == number3
                    }
                    "/" -> {
                        number1 / number2 == number3
                    }
                    else -> false
                }
            }

            assertTrue(pass)
        } catch (e: Exception) {
            logger.log(java.util.logging.Level.SEVERE, "Exception: ", e)
        }
    }

    @Test
    fun `is input a valid equation`() {
        var pass = true
        val ans = gridViewModel.answer.value
        val input = mutableListOf<Grid>()
        if (ans != null) {
            for (i in ans.indices) {
                input.add(Grid(i, ans[i].toString(), isSelected = false, selectable = false, null))
            }
        }
        pass = pass && gridViewModel.isValid(input) == null

        // more than one operator
        input[0] = Grid(0, "+", isSelected = false, selectable = false, null)
        pass = pass && gridViewModel.isValid(input) != null

        // wrong answer
        val lastDigit = input[input.size - 1].text.toInt()
        var newDigit = nextInt(0, 10)
        while (newDigit == lastDigit) {
            newDigit = nextInt(0, 10)
        }
        input[input.size - 1].text = newDigit.toString()
        pass = pass && gridViewModel.isValid(input) != null

        assertTrue(pass)
    }

    @Test
    fun isCorrect() {
        var pass = true
        val ans = gridViewModel.answer.value
        val input = mutableListOf<Grid>()
        if (ans != null) {
            for (i in ans.indices) {
                input.add(Grid(i, ans[i].toString(), isSelected = false, selectable = false, null))
            }
        }
        pass = pass && gridViewModel.isCorrect(input)

        // change last digit
        val lastDigit = input[input.size - 1].text.toInt()
        var newDigit = nextInt(0, 10)
        while (newDigit == lastDigit) {
            newDigit = nextInt(0, 10)
        }
        input[input.size - 1].text = newDigit.toString()

        pass = pass && !gridViewModel.isCorrect(input)
        // check if each grid is colored correctly
        for (i in 0..input.size - 2) {
            pass = pass && input[i].color == Color.parseColor("#33AA00")
        }
        pass = if (ans?.contains(newDigit.toString()) == true) {   // color should be red
            pass && input[input.size - 1].color == Color.parseColor("#AA0000")
        } else {   // color should be black
            pass && input[input.size - 1].color == Color.BLACK
        }

        assertTrue(pass)
    }

    @Test
    fun moveToNextRow() {
        var pass = true
        val numColumns = gridViewModel.answer.value?.length ?: -1
        if (numColumns == -1) {
            fail("Answer is null")
        }

        pass = pass && gridViewModel.moveToNextRow()   // move to second row

        // check if first row is disabled
        for (i in 0 until numColumns) {
            pass = pass && !gridViewModel.allGrids.value!![i].selectable
        }

        pass = pass && gridViewModel.selectedIndex.value == null

        // check if second row is enabled
        for (i in numColumns until numColumns * 2) {
            pass = pass && gridViewModel.allGrids.value!![i].selectable
        }

        pass = pass && gridViewModel.moveToNextRow()   // move to third row
        pass = pass && gridViewModel.moveToNextRow()   // move to fourth row
        pass = pass && gridViewModel.moveToNextRow()   // move to fifth row
        pass = pass && gridViewModel.moveToNextRow()   // move to sixth row
        pass = pass && !gridViewModel.moveToNextRow()   // move to seventh row

        pass = pass && gridViewModel.selectedIndex.value == null

        // check if all grids are disabled
        for (i in 0 until numColumns * 6) {
            pass = pass && !gridViewModel.allGrids.value!![i].selectable
        }

        assertTrue(pass)
    }

    @Test
    fun onGridClicked() {
        gridViewModel.initializeGameViewModel()

        var pass = true
        var clickedGrid = gridViewModel.allGrids.value!![0]

        pass = pass && gridViewModel.selectedIndex.value == null
        pass = pass && gridViewModel.onGridClicked(clickedGrid) == null  // select first grid

        gridViewModel.selectedIndex.value?.let {
            pass = pass && it == 0
        }
        gridViewModel.allGrids.value?.let {
            pass = pass && it[0].isSelected
        }

        pass = pass && gridViewModel.onGridClicked(clickedGrid) == null    // deselect first grid

        gridViewModel.selectedIndex.value?.let {
            pass = pass && it == null
        }
        gridViewModel.allGrids.value?.let {
            pass = pass && !it[0].isSelected
        }

        pass = pass && gridViewModel.onGridClicked(clickedGrid) == null    // select first grid
        var previousIndex = clickedGrid.index
        clickedGrid = gridViewModel.allGrids.value!![1]
        pass = pass && gridViewModel.onGridClicked(clickedGrid) == previousIndex    // select second grid

        gridViewModel.selectedIndex.value?.let {
            pass = pass && it == 1
        }
        gridViewModel.allGrids.value?.let {
            pass = pass && it[1].isSelected
            pass = pass && !it[0].isSelected
        }

        assertTrue(pass)
    }
}