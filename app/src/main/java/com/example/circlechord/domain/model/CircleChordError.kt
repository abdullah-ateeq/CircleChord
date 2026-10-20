package com.example.circlechord.domain.model

sealed interface CircleChordError {
    data object EmptyInput : CircleChordError
    data object InvalidNumber : CircleChordError
    data object NonPositiveRadius : CircleChordError
    data object InvalidAngle : CircleChordError
    data object InputTooLong : CircleChordError

    fun toUserMessage(): String = when (this) {
        EmptyInput -> "Input cannot be empty."
        InvalidNumber -> "Enter a valid number."
        NonPositiveRadius -> "Radius must be greater than zero."
        InvalidAngle -> "Angle must be greater than 0° and no greater than 360°."
        InputTooLong -> "Number is too long. Maximum 500 digits."
    }
}
