package com.hogumiwarts.lumos.ui.screens.Routine.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImagePainter.State.Empty.painter

@Composable
fun RoutineIconList(
    selectedIcon: RoutineIconType
) {
    val allIcons = RoutineIconType.values()

    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {

        items(allIcons) { icon ->
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .width(64.dp)
                        .height(44.dp)
                        .border(
                            border = BorderStroke(1.5.dp, Color(0xFFE1E1E1)),
                            shape = RoundedCornerShape(10.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = icon.iconResId),
                        contentDescription = icon.iconName,
                        modifier = Modifier
                            .size(32.dp)
                    )
                }
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun RoutineIconListPreview() {
    RoutineIconList(selectedIcon = RoutineIconType.MOON_SLEEP)
}
