package com.github.gunin_igor75.onlineshopapp.presentation.main.home

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import com.github.gunin_igor75.onlineshopapp.R
import com.github.gunin_igor75.onlineshopapp.presentation.component.TopBarApp

@Composable
fun HomeContent(
    paddingValues: PaddingValues,
    component: HomeComponent
) {
    TopBarApp(titleResId = R.string.title_home)
}