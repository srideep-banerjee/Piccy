package com.example.piccy.view.main

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
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
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
            "Clickbait Title",
            "abcd",
            "",
            20,
            0
        )
    }.toMutableStateList()
    LazyColumn {
        items(items = postList) {
            HomeScreenItem(postShortened = it)
        }
    }
}

@Composable
fun HomeScreenItem(postShortened: PostShortened) {
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
            style = MaterialTheme.typography.bodySmall
        )
        Text(text = postShortened.title, style = MaterialTheme.typography.titleSmall)
        Image(
            imageVector = Icons.Filled.AccountBox,
            contentDescription = "Image")
    }
}