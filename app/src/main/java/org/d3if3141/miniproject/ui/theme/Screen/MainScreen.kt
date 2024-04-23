package org.d3if3141.miniproject.ui.theme.Screen

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember

import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import org.d3if3141.miniproject.R
import org.d3if3141.miniproject.navigation.Screen


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(navController: NavHostController) {
    val context = LocalContext.current
    Scaffold(
        topBar = {
            TopAppBar(title = {
                Text(text = stringResource(id = R.string.app_name))
            },
                colors = TopAppBarDefaults.mediumTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary
                ),
                actions = {
                    IconButton(
                        onClick = {
                            navController.navigate(Screen.About.route)
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Info,
                            contentDescription = stringResource(id = R.string.tentang_aplikasi),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            )
        }
    ) {padding ->
        ScreenContent(Modifier.padding(padding), context)
    }
}


@Composable
fun ScreenContent(modifier: Modifier, context: Context) {
    var nominalPinjaman by remember { mutableStateOf("")}
    var nominalPinjamanError by remember { mutableStateOf(false)}

    val radioOptions = listOf(
        stringResource(id = R.string.tiga_bulan),
        stringResource(id = R.string.enam_bulan)
    )

    var tenorIndex by remember { mutableStateOf(0) }

    var bunga by remember { mutableStateOf(0.0f) }

    var totalPembayaran by remember { mutableStateOf(0.0f) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(id = R.string.bmi_intro),
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = nominalPinjaman,
            onValueChange = {nominalPinjaman = it},
            label = { Text(text = stringResource(id = R.string.nominal_pinjaman))},
            trailingIcon = { IconPicker(isError = nominalPinjamanError, unit = "Rp")},
            isError = nominalPinjamanError,
            supportingText = { ErrorHint(isError = nominalPinjamanError)},
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Done
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Row(
            modifier = Modifier
                .padding(top = 6.dp)
                .border(1.dp, Color.Gray, RoundedCornerShape(4.dp))
        ) {
            radioOptions.forEachIndexed { index, text ->
                TenorOption(label = text,
                    isSelected = tenorIndex == index,
                    modifier = Modifier
                        .selectable(
                            selected = tenorIndex == index,
                            onClick = { tenorIndex = index },
                            role = Role.RadioButton
                        )
                        .weight(1f)
                        .padding(16.dp)
                )
            }
        }

        Button(
            onClick = {
                nominalPinjamanError = (nominalPinjaman == "" || nominalPinjaman == "0")
                if(nominalPinjamanError) return@Button

                val bungaPersen = if (tenorIndex == 0) 0.15f else 0.10f
                bunga = bungaPersen * 100 // Convert to percentage for display
                val tenor = if (tenorIndex == 0) 3 else 6
                totalPembayaran = hitungTotalPembayaran(nominalPinjaman.toFloat(), bungaPersen, tenor)
            },
            modifier = Modifier.padding(top = 8.dp),
            contentPadding = PaddingValues(horizontal = 32.dp, vertical = 16.dp)
        ) {
            Text(text = stringResource(id = R.string.hitung))
        }

        if(totalPembayaran != 0.0f) {
            Divider(
                modifier = Modifier.padding(vertical = 8.dp),
                thickness = 1.dp
            )
            Text(
                text = stringResource(id = R.string.total_pelunasan, totalPembayaran),
                style = MaterialTheme.typography.titleLarge
            )

            // Informasi tentang nominal yang dipinjam
            Text(
                text = stringResource(id = R.string.nominal_dipinjam, nominalPinjaman.toFloatOrNull() ?: 0.0f),
                style = MaterialTheme.typography.titleLarge
            )

            // Informasi tentang jumlah bulan pinjaman
            Text(
                text = stringResource(id = R.string.jumlah_bulan, if (tenorIndex == 0) 3 else 6),
                style = MaterialTheme.typography.titleLarge
            )

            // Informasi tentang persentase bunga
            Text(
                text = stringResource(id = R.string.persen_bunga, if (tenorIndex == 0) 15 else 10),
                style = MaterialTheme.typography.titleLarge
            )

            // Informasi tentang nominal total bunga
            val nominalPinjamanFloat = nominalPinjaman.toFloatOrNull() ?: 0.0f
            val totalBunga = totalPembayaran - nominalPinjamanFloat

            Text(
                text = stringResource(id = R.string.total_bunga, totalBunga),
                style = MaterialTheme.typography.titleLarge
            )


            Button(
                onClick = {
                    val nominalPinjamanFloat = nominalPinjaman.toFloatOrNull() ?: 0.0f // Parse String to Float
                    shareData(
                        context = context,
                        message = context.getString(
                            R.string.bagikan_template,
                            nominalPinjamanFloat,
                            radioOptions[tenorIndex],
                            bunga,
                            totalPembayaran
                        )
                    )
                },
                modifier = Modifier.padding(top = 8.dp),
                contentPadding = PaddingValues(horizontal = 32.dp, vertical = 16.dp)
            ) {
                Text(text = stringResource(id = R.string.bagikan))
            }
        }
    }
}


@Composable
fun TenorOption(label: String, isSelected: Boolean, modifier: Modifier) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = isSelected,
            onClick = null
        )
        Text(text = label,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(start = 8.dp)
        )
    }
}

@Composable
fun IconPicker(isError: Boolean, unit: String) {
    if (isError) {
        Icon(imageVector = Icons.Filled.Warning, contentDescription = null)
    } else {
        Text(text = unit)
    }
}

@Composable
fun ErrorHint(isError: Boolean) {
    if (isError) {
        Text(text = stringResource(id = R.string.input_invalid))
    }
}

private fun shareData(context: Context, message: String) {
    val shareIntent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, message)
    }
    if (shareIntent.resolveActivity(context.packageManager) != null) {
        context.startActivity(shareIntent)
    }
}

private fun hitungTotalPembayaran(nominalPinjaman: Float, bungaPersen: Float, tenor: Int): Float {
    val bungaTotal = nominalPinjaman * bungaPersen * tenor
    return nominalPinjaman + bungaTotal
}

@Preview(showBackground = true)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Composable
fun GreetingPreview() {
    MaterialTheme {
        MainScreen(rememberNavController())
    }
}
