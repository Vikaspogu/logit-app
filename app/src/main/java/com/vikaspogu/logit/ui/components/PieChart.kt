package com.vikaspogu.logit.ui.components

import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.vikaspogu.logit.R
import kotlin.random.Random

@Composable
fun PieChart(
    data: Map<String, Int>,
    radiusOuter: Dp = 140.dp,
    animDuration: Int = 1000,
) {

    val totalSum = data.values.sum()
    val floatValue = mutableListOf<Float>()
    val listOfColors = mutableListOf<Color>()

    // To set the value of each Arc according to
    // the value given in the data, we have used a simple formula.
    // For a detailed explanation check out the Medium Article.
    // The link is in the about section and readme file of this GitHub Repository
    data.values.forEachIndexed { index, values ->
        floatValue.add(index, 360 * values.toFloat() / totalSum.toFloat())
        listOfColors.add(getRandomColor())
    }

    // add the colors as per the number of data(no. of pie chart entries)
    // so that each data will get a color


    var animationPlayed by remember { mutableStateOf(false) }

    var lastValue = 0f

    // it is the diameter value of the Pie
    val animateSize by animateFloatAsState(
        targetValue = if (animationPlayed) radiusOuter.value * 2f else 0f, animationSpec = tween(
            durationMillis = animDuration, delayMillis = 0, easing = LinearOutSlowInEasing
        ), label = stringResource(id = R.string.animate_size)
    )

    // if you want to stabilize the Pie Chart you can use value -90f
    // 90f is used to complete 1/4 of the rotation
    val animateRotation by animateFloatAsState(
        targetValue = if (animationPlayed) 90f * 11f else 0f, animationSpec = tween(
            durationMillis = animDuration, delayMillis = 0, easing = LinearOutSlowInEasing
        ), label = stringResource(id = R.string.animate_rotate)
    )

    // to play the animation only once when the function is Created or Recomposed
    LaunchedEffect(true) {
        animationPlayed = true
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 20.dp)
            .background(color = MaterialTheme.colorScheme.background),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        // Pie Chart using Canvas Arc
        Box(
            modifier = Modifier.size(animateSize.dp), contentAlignment = Alignment.Center
        ) {
            Canvas(
                modifier = Modifier
                    .size(radiusOuter * 2f)
                    .rotate(animateRotation)
            ) {
                // draw each Arc for each data entry in Pie Chart
                floatValue.forEachIndexed { index, value ->
                    drawArc(
                        color = listOfColors[index],
                        lastValue,
                        value,
                        useCenter = false,
                        style = Stroke(65f, cap = StrokeCap.Round)
                    )
                    lastValue += value
                }
            }
        }

        // To see the data in more structured way
        // Compose Function in which Items are showing data
        DetailsPieChart(
            data = data, colors = listOfColors, totalSum
        )

    }

}

@Composable
fun DetailsPieChart(
    data: Map<String, Int>, colors: List<Color>, totalSum: Int
) {
    Column(
        modifier = Modifier
            .padding(top = 40.dp)
            .fillMaxWidth()
    ) {
        // create the data items
        data.values.forEachIndexed { index, value ->
            DetailsPieChartItem(
                data = Pair(data.keys.elementAt(index), value), color = colors[index], totalSum
            )
        }

    }
}

fun getRandomColor(): Color {
    val alpha = 255
    return Color(
        Random.nextInt(256), Random.nextInt(256), Random.nextInt(256), alpha = alpha
    )
}

@Composable
fun DetailsPieChartItem(
    data: Pair<String, Int>, color: Color, totalSum: Int
) {
    val percentage = (data.second.toDouble() / totalSum )* 100
    val formattedPercentage = String.format("%.2f", percentage)
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 10.dp, start = 10.dp, end = 10.dp),

        shape = MaterialTheme.shapes.small,
        color = MaterialTheme.colorScheme.primaryContainer,
        shadowElevation = 5.dp,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(color = MaterialTheme.colorScheme.primaryContainer),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Spacer(modifier = Modifier.width(10.dp))
            Box(
                modifier = Modifier
                    .background(
                        color = color, shape = RoundedCornerShape(10.dp)
                    )
                    .size(20.dp)
            )
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    modifier = Modifier.padding(start = 15.dp),
                    text = data.first,
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.inverseSurface,
                )
                Text(
                    modifier = Modifier.padding(start = 15.dp),
                    text = buildAnnotatedString {
                        append(formattedPercentage)
                        append(" %")
                    },
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.inverseSurface,
                )
            }

        }

    }

}