package com.github.gunin_igor75.onlineshopapp.presentation.component

import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBarApp(
    titleResId: Int
) {
    CenterAlignedTopAppBar(
        title = {
            Text(
                text = stringResource(titleResId),
                style = MaterialTheme.typography.titleMedium.copy(
                    fontSize = 16.sp
                )
            )
        }
    )
}