package com.michaelflisar.toolbox.powershell

class PSResult(
    val outputs: List<String> = emptyList(),
    val errors: List<String> = emptyList()
) {
    val isEmpty = outputs.isEmpty() && errors.isEmpty()

    val hasErrors = errors.isNotEmpty()
    val hasOutputs = outputs.isNotEmpty()

    val info: String by lazy {
        if (isEmpty) "" else {
            var lines = "Outputs: ${outputs.size} | Errors: ${errors.size}"
            outputs.forEach {
                lines += "\n    - $it"
            }
            errors.forEach {
                lines += "\n    - $it"
            }
            lines
        }
    }

    override fun toString() = info
}