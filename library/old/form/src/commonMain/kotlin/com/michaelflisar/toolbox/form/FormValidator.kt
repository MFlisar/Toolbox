package com.michaelflisar.toolbox.form

object FormValidator {

    fun <T : Number?> isNumberInRange(
        min: T,
        max: T,
        errorTooSmall: String = "Number is too small",
        errorTooBig: String = "Number is too big",
        errorInvalid: String = "Invalid Number"
    ): (value: T?) -> String {
        return { value: T? ->
            if (value == null) {
                errorInvalid
            } else if (min != null && value.toDouble() < min.toDouble()) {
                errorTooSmall
            } else if (max != null && value.toDouble() > max.toDouble()) {
                errorTooBig
            } else ""
        }
    }

    fun isNotEmpty(
        name: String = "Field",
        errorIsEmpty: String = "$name must be not empty"
    ): (value: String) -> String {
        return { value: String ->
            if (value.isEmpty()) {
                errorIsEmpty
            } else ""
        }
    }
}