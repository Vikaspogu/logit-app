package com.vikaspogu.logit.ui.home

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.vikaspogu.logit.R
import com.vikaspogu.logit.data.model.EntryType
import com.vikaspogu.logit.ui.NavigationDestinations
import com.vikaspogu.logit.ui.entry.formatDate


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SummaryDetailsScreen(
    navController: NavHostController, modifier: Modifier, viewModel: SummaryDetailsViewModel = hiltViewModel()
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
        LazyColumn(modifier=Modifier.padding(top = 20.dp)) {
            item {
                SummaryDetailsColumn(entryTypeUiState.entryTypeList, modifier)
            }
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
                entry
            )
        }
        Spacer(Modifier.height(16.dp))
    }
}

@Composable
fun SummaryDetailsCard(entry: EntryType) {
    var expanded by remember {
        mutableStateOf(false)
    }
    var showMoreButton by remember {
        mutableStateOf(false)
    }
    var hideOverFlowingText by remember {
        mutableStateOf(false)
    }
    Surface(
        shape = MaterialTheme.shapes.large,
        color = MaterialTheme.colorScheme.primaryContainer,
        shadowElevation = 5.dp,
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 5.dp, bottom = 5.dp, start = 10.dp, end = 10.dp)
    ) {
        Column(
            modifier = Modifier.padding(dimensionResource(id = R.dimen.padding_medium)),
            verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.padding_small)),
        ) {
            Text(
                text = entry.type,
                modifier = Modifier.padding(0.dp, 0.dp, 0.dp, 0.dp),
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.inverseSurface,
            )
            Row(verticalAlignment = Alignment.Bottom) {
                Icon(
                    imageVector = Icons.Filled.Face,
                    contentDescription = stringResource(id = R.string.attending)
                )
                Text(
                    text = entry.attendingName,
                    modifier = Modifier.padding(5.dp, 12.dp, 12.dp, 0.dp),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.inverseSurface,
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
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.inverseSurface,
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
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.inverseSurface,
            )
            if(!hideOverFlowingText){
                Text(
                    text = entry.notes,
                    maxLines = 2,
                    onTextLayout = {
                        if(it.hasVisualOverflow){
                            showMoreButton=true
                        }
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(5.dp, 5.dp, 0.dp, 0.dp),
                    color = MaterialTheme.colorScheme.inverseSurface,
                )
            }

            if(showMoreButton){
                AnimatedVisibility(expanded) {
                    Text(
                        text = entry.notes,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(5.dp, 5.dp, 0.dp, 0.dp),
                    )
                }
                Box(
                    modifier = Modifier
                        .wrapContentWidth()
                        .padding(top = 10.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.inversePrimary)
                        .clickable(onClick = {
                            expanded = !expanded
                            hideOverFlowingText = !hideOverFlowingText
                        }),
                ) {
                    Text(
                        text = if (!expanded) stringResource(id = R.string.show_notes) else stringResource(
                            id = R.string.hide_notes
                        ), modifier = Modifier.padding(12.dp, 6.dp, 12.dp, 6.dp),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.inverseSurface,
                    )
                }
            }
        }
    }
}