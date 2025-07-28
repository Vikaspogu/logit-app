package com.vikaspogu.logit.ui.startup

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowForward
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.vikaspogu.logit.R
import com.vikaspogu.logit.ui.theme.SmallHeadingStyle
import com.vikaspogu.logit.ui.theme.TitleBarStyleLarge
import com.vikaspogu.logit.ui.theme.TitleStyle

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun StartupScreen(
    navController: NavHostController,
    modifier: Modifier,
    viewModel: StartupViewModel = hiltViewModel()
) {
    Scaffold(topBar = {}, floatingActionButton = {
        FloatingActionButton(onClick = {
            viewModel.saveUsername(viewModel.username.value)
            navController.navigate(
                "Summary"
            )
        }, containerColor = MaterialTheme.colorScheme.inverseSurface) {
            Icon(
                imageVector = Icons.AutoMirrored.Outlined.ArrowForward,
                contentDescription = stringResource(id = R.string.continueForward),
            )
        }
    }) {
        Column(
            modifier = modifier.padding(32.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(100.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = stringResource(R.string.app_name),
                    style = TitleBarStyleLarge,
                    modifier = Modifier.padding(start = 15.dp)
                )
                Image(
                    modifier = Modifier
                        .size(dimensionResource(id = R.dimen.image_size))
                        .padding(dimensionResource(id = R.dimen.padding_small)),
                    painter = painterResource(id = R.drawable.syringe_icon),
                    contentDescription = null
                )
            }
            Spacer(modifier = Modifier.height(100.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = stringResource(R.string.welcome),
                    style = TitleStyle
                )
            }
            Spacer(modifier = Modifier.height(25.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Please enter your name to continue",
                    style = SmallHeadingStyle
                )
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Outlined.Person, contentDescription = stringResource(
                        id = R.string.username
                    )
                )
                OutlinedTextField(
                    value = viewModel.username.value,
                    onValueChange = { viewModel.updateUsername(it) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(all = 10.dp),
                    enabled = true,
                    label = {
                        Text(
                            text = stringResource(id = R.string.username),
                            style = SmallHeadingStyle
                        )
                    }
                )
            }
            SingleChoiceSegmentedButtonRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 10.dp, end = 10.dp, top = 5.dp),
            ) {
                SegmentedButton(selected = !viewModel.residentView.value, onClick = {
                    viewModel.updateSelectedView(!viewModel.residentView.value)
                    viewModel.saveViewPreferences(viewModel.residentView.value)
                }, shape = RoundedCornerShape(topStart = 16.dp, bottomStart = 16.dp), label = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            modifier = Modifier.padding(end = 3.dp),
                            painter = painterResource(id = R.drawable.stethoscope_20px),
                            contentDescription = null
                        )
                        Text(
                            text = stringResource(id = R.string.attendant),
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier
                                .padding(5.dp, 0.dp, 0.dp, 0.dp)
                        )
                    }
                })
                SegmentedButton(selected = viewModel.residentView.value, onClick = {
                    viewModel.updateSelectedView(!viewModel.residentView.value)
                    viewModel.saveViewPreferences(viewModel.residentView.value)
                }, shape = RoundedCornerShape(topEnd = 16.dp, bottomEnd = 16.dp), label = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            modifier = Modifier.padding(end = 3.dp),
                            painter = painterResource(id = R.drawable.stethoscope_20px),
                            contentDescription = null
                        )
                        Text(
                            text = stringResource(id = R.string.resident),
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier
                                .padding(5.dp, 0.dp, 0.dp, 0.dp)
                        )
                    }
                })
            }
        }
    }
}
