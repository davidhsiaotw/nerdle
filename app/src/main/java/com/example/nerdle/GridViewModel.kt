package com.example.nerdle

import android.graphics.Color
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlin.random.Random
import kotlin.streams.toList

class GridViewModel : ViewModel() {
    private val _answer = MutableLiveData<String>()
    val answer: LiveData<String> = _answer

    private val _chance = MutableLiveData<Int>()
    var chance: LiveData<Int> = _chance

    private var _selectedIndex = MutableLiveData<Int?>()
    var selectedIndex: LiveData<Int?> = _selectedIndex

    private var _startIndex = MutableLiveData<Int>()
    var startIndex: LiveData<Int> = _startIndex

    private var _endIndex = MutableLiveData<Int>()
    var endIndex: LiveData<Int> = _endIndex

    private val _allGrids = MutableLiveData<ArrayList<Grid>>()
    var allGrids: LiveData<ArrayList<Grid>> = _allGrids

    init {
        initializeGameViewModel()
    }

    fun initializeGameViewModel() {
        _answer.value = answerGenerator()
        _chance.value = TOTAL_CHANCE
        _startIndex.value = 0
        _endIndex.value = _answer.value!!.length - 1
        _allGrids.value = ArrayList()
        for (i in 0 until TOTAL_CHANCE) {   // 6 rows
            for (j in 0 until _answer.value!!.length) {
                if (i == 0)
                    _allGrids.value!!.add(Grid(j, "", isSelected = false, true, null))
                else
                    _allGrids.value!!.add(
                        Grid(i * _answer.value!!.length + j, "", isSelected = false, false, null)
                    )
            }
        }
    }

    private fun answerGenerator(): String {
        val number1Min = 0
        var number1Max: Int
        var number2Min = 0
        var number2Max: Int

        var number1 = 0
        var number2 = 0
        var number3 = 0
        // https://www.notion.so/wyxiao6/Side-Project-2-Nerdle-a661842b2d07412cafc219d5d6f7ebf5?pvs=4#065219722cdf4f05acde50996e60725e
        // number1 ? number2 = number3
        val ans: StringBuilder = StringBuilder("")

        val operators = listOf("+", "-", "*", "/")
        val size = operators.size
        var operator = ""
        when (operators[Random.nextInt(0, size)]) {
            "+" -> {
                operator = "+"
                number1Max = 50
                number1 = Random.nextInt(number1Min, number1Max + 1)
                number2Max = if (number1 == 50) 49 else 50
                number2 = Random.nextInt(number2Min, number2Max + 1)
                number3 = number1 + number2
            }
            "-" -> {
                operator = "-"
                number1Max = 99
                number1 = Random.nextInt(number1Min, number1Max + 1)
                number2 = Random.nextInt(number2Min, number1 + 1)
                number3 = number1 - number2
            }
            "*" -> {
                operator = "*"
                number1Max = 11
                number1 = Random.nextInt(number1Min, number1Max + 1)
                number2Max = if (number1 <= 9) 11 else 9
                number2 = Random.nextInt(number2Min, number2Max + 1)
                number3 = number1 * number2
            }
            "/" -> {
                operator = "/"
                number1Max = 99
                number1 = Random.nextInt(number1Min, number1Max + 1)
                number2Min = 1
                if (number1 == 0) {
                    number2Max = 99
                    number2 = Random.nextInt(number2Min, number2Max + 1)
                } else if (isPrime(number1)) {
                    number2 = listOf(1, number1).shuffled()[0]
                } else {
                    number2Max = number1
                    number2 = Random.nextInt(number2Min, number2Max + 1)
                    while (number1 % number2 != 0)
                        number2 = Random.nextInt(number2Min, number2Max + 1)
                }
                number3 = number1 / number2
            }
        }

        ans.append(number1).append(operator).append(number2).append("=").append(number3)
        return ans.toString()
    }

    private fun isPrime(integer: Int): Boolean {
        for (i in 2..integer / 2) {
            if (integer % i == 0) {
                return false
            }
        }
        return true
    }

    /**
     * check if user's input is valid arithmetic
     *
     * @param grids
     * @return error message if input is not valid, otherwise null
     */
    @RequiresApi(Build.VERSION_CODES.N)
    fun isValid(grids: List<Grid>): String? {
        val input = convertGridsToString(grids)

        // check if input length equals to answer length
        if (input.length != _answer.value!!.length)
            return "input length MUST be ${_answer.value!!.length}"

        // check if input contains only one operator + - * /
        val operatorAmount =
            input.count { it in "+" } + input.count { it in "-" } + input.count { it in "*" } + input.count { it in "/" }
        if (operatorAmount != 1)
            return "input MUST only has ONE operator"

        // check if input contains equal sign
        if (input.count { it in "=" } != 1)
            return "input with only ONE equal sign is acceptable"

        val number1: Int
        val operator: String
        val number2: Int
        val number3: Int

        when (true) {
            input.contains("+") -> {
                operator = "+"
            }
            input.contains("-") -> {
                operator = "-"
            }
            input.contains("*") -> {
                operator = "*"
            }
            input.contains("/") -> {
                operator = "/"
            }
            else -> {
                return "input MUST be one of those operators +, -, *, /"
            }
        }
        val string1 = input.split(operator)
        if (string1[0] == "")
            return "missing number"
        number1 = string1[0].toInt()
        val string2 = string1[1].split("=")
        if (string2[0] == "" || string2[1] == "")
            return "missing number"
        number2 = string2[0].toInt()
        number3 = string2[1].toInt()

        when (operator) {
            "+" -> {
                if (number1 + number2 != number3) return "arithmetic is wrong/invalid"
            }
            "-" -> {
                if (number1 - number2 != number3) return "arithmetic is wrong/invalid"
            }
            "*" -> {
                if (number1 * number2 != number3) return "arithmetic is wrong/invalid"
            }
            "/" -> {
                if (number2 == 0)
                    return "divisor MUST not be 0"
                else if (number1 / number2 != number3)
                    return "arithmetic is wrong/invalid"
            }
        }

        return null
    }

    /**
     * check if user's input matches the answer<br>
     * change grids' color
     *
     * @param grids
     * @return true if user's input completely matches the answer, otherwise false
     */
    @RequiresApi(Build.VERSION_CODES.N)
    fun isCorrect(grids: List<Grid>): Boolean {
        val input = convertGridsToString(grids)
        val correct = input == _answer.value
        if (input.length != _answer.value!!.length)
            throw java.lang.RuntimeException("length of $input does not match length of answer")
        if (!correct) {
            // give user hint by changing button's color
            for (i in grids.indices) {
                val grid = grids[i]

                if (grid.text == _answer.value!![i].toString()) {
                    grid.color = Color.parseColor("#33AA00")
                } else {
                    if (!_answer.value!!.contains(grid.text)) {
                        grid.color = Color.BLACK
                    } else {
                        grid.color = Color.parseColor("#AA0000")
                    }
                }

                val index = grid.index
                _allGrids.value!![index] = grid
            }
        }
        _selectedIndex.value = null
        return correct
    }

    /**
     * convert list of grids to string
     *
     * @param grids
     * @return
     */
    @RequiresApi(Build.VERSION_CODES.N)
    private fun convertGridsToString(grids: List<Grid>): String {
        return grids.stream().map { grid -> grid.text }.toList().joinToString("")
    }

    /**
     * disable buttons in current row
     * minus chance by 1
     *
     * @return true if game is not over, otherwise false
     */
    fun moveToNextRow(): Boolean {
        // disable buttons in current row
        for (i in _startIndex.value!!.._endIndex.value!!) {
            val grid = _allGrids.value!![i]
            grid.isSelected = false
            grid.selectable = false
            _allGrids.value!![i] = grid
        }

        _chance.value = _chance.value!!.minus(1)
        _selectedIndex.value = null
        if (_chance.value == 0)
            return false
        _startIndex.value = _endIndex.value!!.plus(1)
        _endIndex.value = _startIndex.value!!.plus(_answer.value!!.length - 1)

        // enable buttons in next row
        for (i in _startIndex.value!!.._endIndex.value!!) {
            val grid = _allGrids.value!![i]
            grid.selectable = true
            _allGrids.value!![i] = grid
        }
        return true
    }

    /**
     * update grid list when a button is clicked
     *
     * @param grid bound on the clicked button
     * @return previous clicked button's grid's index
     */
    fun onGridClicked(grid: Grid): Int? {
        var previous: Int? = null
        if (_selectedIndex.value != null) {
            val previousGrid = _allGrids.value!![_selectedIndex.value!!]
            previousGrid.isSelected = false
            _allGrids.value?.set(_selectedIndex.value!!, previousGrid)
            if (previousGrid.index == grid.index) {
                _selectedIndex.value = null
                return null
            }
            previous = _selectedIndex.value
        }
        _selectedIndex.value = grid.index
        grid.isSelected = true
        _allGrids.value?.set(_selectedIndex.value!!, grid)
        return previous
    }

    companion object {
        private const val TOTAL_CHANCE: Int = 6
    }
}