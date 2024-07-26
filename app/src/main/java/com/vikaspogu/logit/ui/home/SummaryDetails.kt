package com.vikaspogu.logit.ui.home

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.vikaspogu.logit.R
import com.vikaspogu.logit.data.model.EntryType
import com.vikaspogu.logit.ui.NavigationDestinations
import com.vikaspogu.logit.ui.entry.formatDate


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SummaryDetailsScreen(
    navController: NavHostController, modifier: Modifier, viewModel: SummaryDetailsViewModel
) {
    val entryTypeUiState by viewModel.entryTypeUiState.collectAsState()
    Scaffold(topBar = {
        CenterAlignedTopAppBar(title = {}, navigationIcon = {
            IconButton(onClick = {
                navController.navigate(NavigationDestinations.Summary.name)
            }) {
                Icon(
                    imageVector = Icons.Filled.Close,
                    contentDescription = stringResource(id = R.string.back)
                )
            }
        })
    }, modifier = modifier.fillMaxSize()) {
        SummaryDetails(entryList = entryTypeUiState.entryTypeList, modifier = modifier)
    }
}

@Composable
fun SummaryDetails(entryList: List<EntryType>, modifier: Modifier) {
    LazyColumn {
        item {
            SummaryDetailsColumn(entryList, modifier)
        }
    }
}

@Composable
fun SummaryDetailsColumn(entryList: List<EntryType>, modifier: Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(0.dp, 100.dp, 0.dp, 5.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        for (entry in entryList) {
            SummaryDetailsCard(
                entry, modifier
            )
        }
        Spacer(Modifier.height(16.dp))
    }
}

@Composable
fun SummaryDetailsCard(entry: EntryType, modifier: Modifier) {
    var expanded by remember {
        mutableStateOf(false)
    }
    Card(
        modifier = modifier.padding(10.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 10.dp),
        shape = MaterialTheme.shapes.large
    ) {
        Column(
            modifier = Modifier.padding(dimensionResource(id = R.dimen.padding_medium)),
            verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.padding_small)),
        ) {
            Text(
                text = entry.type,
                modifier = Modifier.padding(0.dp, 0.dp, 0.dp, 0.dp),
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.headlineSmall
            )
            Row(verticalAlignment = Alignment.Bottom) {
                Icon(
                    imageVector = Icons.Filled.Face,
                    contentDescription = stringResource(id = R.string.attending)
                )
                Text(
                    text = entry.attendingName,
                    modifier = Modifier.padding(5.dp, 12.dp, 12.dp, 0.dp),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                )
            }
            Row(verticalAlignment = Alignment.Bottom) {
                Icon(
                    imageVector = Icons.Outlined.DateRange, contentDescription = stringResource(
                        id = R.string.date
                    )
                )
                Text(
                    text = buildString {
                        append(entry.entryDate.formatDate())
                    },
                    modifier = Modifier.padding(5.dp, 12.dp, 12.dp, 5.dp),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            Text(
                text = buildString {
                    append(entry.age)
                    append(" yrs | ")
                    append(entry.gender)
                    append(" | ")
                    append(entry.quantity)
                    append(" quantity")
                },
                modifier = Modifier.padding(5.dp, 5.dp, 12.dp, 10.dp),
                style = MaterialTheme.typography.titleMedium
            )
            Box(
                modifier = Modifier
                    .wrapContentWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.inversePrimary)
                    .clickable(onClick = { expanded = !expanded }),
            ) {
                Text(
                    text = if (!expanded) stringResource(id = R.string.show_notes) else stringResource(
                        id = R.string.hide_notes
                    ),
                    modifier = Modifier.padding(12.dp, 6.dp, 12.dp, 6.dp),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            if (expanded) {
                Text(
                    text = stringResource(id = R.string.notes),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(5.dp, 5.dp, 0.dp, 5.dp),
                )
                Text(
                    text = entry.notes,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(5.dp, 5.dp, 0.dp, 0.dp),
                )
            }
        }
    }
}