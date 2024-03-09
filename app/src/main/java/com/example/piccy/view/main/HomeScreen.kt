package com.example.piccy.view.main

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.piccy.model.PostShortened
import com.example.piccy.view.ui.theme.PiccyTheme

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun HomeScreen() {
    PiccyTheme {
        Scaffold(
            floatingActionButton = {
                HomeAddButton()
            }
        ) {
            PostList()
        }
    }
}

@Composable
fun HomeAddButton() {
    FloatingActionButton(
        containerColor = MaterialTheme.colorScheme.primary,
        contentColor = MaterialTheme.colorScheme.onPrimary,
        shape = CircleShape,
        onClick = { /*TODO*/ }
    ) {
        Icon(
            imageVector = Icons.Filled.Add,
            contentDescription = "Add"
        )
    }
}

@Composable
fun PostList() {
    val postList = MutableList(20){
        PostShortened(
            "Clickbait Title Clickbait Title Clickbait Title Clickbait Title Clickbait Title Clickbait Title Clickbait Title Clickbait Title Clickbait Title Clickbait Title Clickbait Title ",
            "abcd",
            "",
            20,
            0
        )
    }.toMutableStateList()

    val localDensity = LocalDensity.current

    val heightDp = remember {
        mutableStateOf(0.dp)
    }
    LazyColumn(
        modifier = Modifier
            .onSizeChanged {
                heightDp.value = with(localDensity) { it.height.toDp() }
            }
            .fillMaxSize()
    ) {
        items(items = postList) {
            HomeScreenItem(postShortened = it, columnHeight = heightDp)
        }
    }
}

@Composable
fun LazyItemScope.HomeScreenItem(postShortened: PostShortened, columnHeight: MutableState<Dp>) {
    var titleExpanded by remember {
        mutableStateOf(false)
    }
    Column(
        modifier = Modifier
            .padding(8.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(color = MaterialTheme.colorScheme.surfaceVariant)
            .padding(16.dp)
            .fillMaxWidth()
    ) {
        Text(
            text = "Network: ${postShortened.network}",
            style = MaterialTheme.typography.bodyMedium
        )
        Spacer(Modifier.height(8.dp))
//        Text(
//            text = postShortened.title,
//            style = MaterialTheme.typography.titleSmall,
//            onTextLayout = {
//                it.getLineEnd(0)
//                "".dropLast(3).dropLastWhile { true }
//            }
//        )
        ReadMoreText(text = postShortened.title)
        Spacer(modifier = Modifier.height(8.dp))
        Image(
            painter = painterResource(id = com.example.piccy.R.drawable.original_img),
            contentScale = ContentScale.Crop,
            contentDescription = "Image",
            modifier = Modifier
                .fillMaxWidth()
                .height((columnHeight.value.value * 0.5f).dp)
                .clip(RoundedCornerShape(8.dp))
        )
    }
}