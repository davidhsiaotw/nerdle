package com.example.nerdle

data class Grid(
    val index: Int, var text: String, var isSelected: Boolean, var selectable:
    Boolean, var color: Int?
)