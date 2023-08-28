package com.sunshine.freeform.ui.main

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.sunshine.freeform.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

/**
 * @author KindBrave
 * @since 2023/8/26
 */

@Composable
fun SettingWidget(settingViewModel: SettingViewModel) {
    val enableSideBar by settingViewModel.enableSideBar.observeAsState(false)
    val freeformWidth by settingViewModel.freeformWidth.observeAsState((settingViewModel.screenWidth * 0.8).roundToInt())
    val freeformHeight by settingViewModel.freeformHeight.observeAsState((settingViewModel.screenHeight * 0.5).roundToInt())
    val freeformDpi by settingViewModel.freeformDensityDpi.observeAsState(settingViewModel.screenDensityDpi)
    val coroutineScope = rememberCoroutineScope()
    val snackBarHostState = remember { SnackbarHostState() }
    var isShowingSnackBar by remember { mutableStateOf(false) }
    val warn = stringResource(id = R.string.freeform_width_height_warn)
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        item {
            SettingSwitchOption(
                stringResource(id = R.string.sidebar),
                stringResource(id = R.string.sidebar_message),
                enableSideBar
            ) {
                settingViewModel.saveRemoteSidebar(it)
            }
            SettingSlideBarOption(
                stringResource(id = R.string.freeform_width),
                freeformWidth,
                50,
                100f..settingViewModel.screenWidth.toFloat()
            ) {
                if (it.roundToInt() >= freeformHeight) {
                    if (isShowingSnackBar.not()) {
                        coroutineScope.launch {
                            isShowingSnackBar = true
                            val result = snackBarHostState.showSnackbar(warn, withDismissAction = true)
                            if (result == SnackbarResult.Dismissed) {
                                isShowingSnackBar = false
                            }
                        }
                    }
                } else {
                    settingViewModel.setFreeformWidth(it.roundToInt())
                }
            }
            SettingSlideBarOption(
                stringResource(id = R.string.freeform_height),
                freeformHeight,
                50,
                100f..settingViewModel.screenHeight.toFloat()
            ) {
                if (it.roundToInt() <= freeformWidth) {
                    if (isShowingSnackBar.not()) {
                        coroutineScope.launch {
                            isShowingSnackBar = true
                            val result = snackBarHostState.showSnackbar(warn, withDismissAction = true)
                            if (result == SnackbarResult.Dismissed) {
                                isShowingSnackBar = false
                            }
                        }
                    }
                } else {
                    settingViewModel.setFreeformHeight(it.roundToInt())
                }
            }
            SettingSlideBarOption(
                stringResource(id = R.string.freeform_dpi),
                freeformDpi,
                50,
                100f..500f
            ) {
                settingViewModel.setFreeformDpi(it.roundToInt())
            }
        }
    }

    SnackbarHost(hostState = snackBarHostState)
}

@Composable
fun SettingSwitchOption(
    title: String,
    message: String,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(90.dp)
            .padding(8.dp)
            .clickable { onCheckedChange(!isChecked) },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Column {
            Text(text = title, style = MaterialTheme.typography.titleLarge)
            Text(text = message, style = MaterialTheme.typography.bodyMedium)
        }
        Switch(
            checked = isChecked,
            onCheckedChange = onCheckedChange
        )
    }
}

@Composable
fun SettingSlideBarOption(
    title: String,
    value: Int,
    steps: Int,
    range: ClosedFloatingPointRange<Float>,
    onValueChanged: (Float) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(90.dp)
            .padding(8.dp),
        horizontalAlignment = Alignment.Start,
    ) {
        Text(text = "$title $value", style = MaterialTheme.typography.titleLarge)
        Slider(
            value = value.toFloat(),
            valueRange = range,
            steps = steps,
            onValueChange = onValueChanged,
        )
    }
}