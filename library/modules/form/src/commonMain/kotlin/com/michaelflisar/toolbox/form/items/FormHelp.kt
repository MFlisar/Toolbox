package com.michaelflisar.toolbox.form.items

import androidx.compose.ui.text.AnnotatedString

data class FormHelp(
    val text: AnnotatedString,
    val title: String? = null,
) {
    constructor(
        text: String,
        title: String? = null,
    ) : this(AnnotatedString(text), title)
}
