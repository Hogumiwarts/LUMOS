package com.hogumiwarts.lumos.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hogumiwarts.lumos.R
import com.hogumiwarts.lumos.ui.theme.nanum_square_neo

@Composable
fun CommonTopBar(
    barTitle: String,
    onBackClick: () -> Unit,
    isAddBtnVisible: Boolean,
    onAddClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp)
            .background(Color.White)
            .padding(horizontal = 28.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_arrow_back),
            contentDescription = null,
            modifier = Modifier.clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onBackClick
            )
        )

        Text(
            text = barTitle,
            fontSize = 18.sp,
            fontWeight = FontWeight.ExtraBold,
            fontFamily = nanum_square_neo
        )

        if (isAddBtnVisible) {
            Icon(
                painter = painterResource(id = R.drawable.ic_add),
                contentDescription = null,
                modifier = Modifier
                    .size(24.dp)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = onAddClick
                    )
            )
        } else {
            Spacer(modifier = Modifier.size(24.dp))
        }


    }
}

@Preview(showBackground = true)
@Composable
private fun BarPreview() {
    CommonTopBar(
        onBackClick = {},
        isAddBtnVisible = false,
        onAddClick = {},
        barTitle = "나의 루틴"
    )
}