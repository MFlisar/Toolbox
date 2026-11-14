package com.michaelflisar.demo.pages

import androidx.navigation3.runtime.NavKey
import androidx.savedstate.serialization.SavedStateConfiguration
import com.michaelflisar.demo.pages.tests.PageTestForm
import com.michaelflisar.demo.pages.tests.PageTestTaskViewer
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass

val NavigationSavedStateConfiguration = SavedStateConfiguration {
    serializersModule = SerializersModule {
        polymorphic(NavKey::class) {
            subclass(Demo1::class)
            subclass(SubPage::class)
            subclass(Demo2::class)
            subclass(Demo3::class)
            subclass(Demo4::class)
            subclass(Demo4Detail::class)
            subclass(PageTestTaskViewer::class)
            subclass(PageTestForm::class)
        }
    }
}
