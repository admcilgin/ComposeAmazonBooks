package com.acilgin.composetrainingfirestore

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberImagePainter
import com.acilgin.composetrainingfirestore.data.Book
import com.acilgin.composetrainingfirestore.data.CATEGORY
import com.acilgin.composetrainingfirestore.data.viewmodel.BooksViewModel
import com.acilgin.composetrainingfirestore.repo.BooksRepo
import com.acilgin.composetrainingfirestore.ui.components.ExpandableText
import com.acilgin.composetrainingfirestore.ui.theme.ComposeTrainingFireStoreTheme


class MainActivity : ComponentActivity() {
    @ExperimentalAnimationApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            var darkMode by remember { mutableStateOf(false) }

            ComposeTrainingFireStoreTheme(
                darkTheme = darkMode
            ) {
                // A surface container using the 'background' color from the theme
                Surface(color = MaterialTheme.colors.background) {
                    BookList(darkMode = darkMode, onDarkModeChange = { darkMode = it })
                }
            }
        }
    }


    private fun onClickBuyOnAmazone(link: String, context: Context) {
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(link))
        context.startActivity(browserIntent)
    }


    @ExperimentalAnimationApi
    @Composable
    fun BookList(
        booksViewModel: BooksViewModel = viewModel(
            factory = BookViewModelFactory(BooksRepo())
        ),
        darkMode: Boolean,
        onDarkModeChange: (Boolean) -> Unit
    ) {
        var category by remember { mutableStateOf(CATEGORY.FORJUNIOR.get) }
        Column {

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                Text(
                    text = "Technical Books",
                    style = TextStyle(fontSize = 28.sp, fontWeight = FontWeight.Bold),
                    modifier = Modifier.weight(2f)
                )
                IconButton(

                    onClick = { onDarkModeChange(darkMode.not()) },
                ) {
                    Icon(Icons.Default.LightMode, "dark mode")
                }
            }

            LazyRow(

            ) {
                items(CATEGORY.values()) {

                    Button(
                        onClick = { category = it.get },
                        modifier = Modifier.padding(5.dp)
                    ) {
                        Text(text = "${it.get}", color = Color.Black)
                    }


                }
            }


            when (val booksList =
                booksViewModel.getBooksInfo(category = category)
                    .collectAsState(initial = null).value) {
                is OneError -> {
                    Text(text = "Please try after sometime")
                }
                is OnSuccess -> {
                    val listOfBooks = booksList.querySnapshot?.toObjects(Book::class.java)

                    val mapped =
                        listOfBooks?.distinctBy() { it.category.map { it.uppercaseChar() } }

                    listOfBooks?.let {


                        LazyColumn(modifier = Modifier.fillMaxHeight()) {
                            items(listOfBooks) {
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    shape = RoundedCornerShape(16.dp)
                                ) {
                                    BookDetails(it)
                                }
                            }
                        }
                    }
                }
            }

        }
    }

    @ExperimentalAnimationApi
    @Composable
    fun BookDetails(book: Book) {
        var showBookDescription by remember { mutableStateOf(false) }
        val bookCoverImageSize by animateDpAsState(targetValue = if (showBookDescription) 100.dp else 80.dp)

        Column(modifier = Modifier.clickable {
            showBookDescription = showBookDescription.not()
        }) {
            Row(modifier = Modifier.padding(12.dp)) {
                Image(
                    painter = rememberImagePainter(
                        data = book.image,
                        builder = {
                            crossfade(true)
                        }
                    ),
                    contentDescription = "Book Cover page",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.size(bookCoverImageSize)
                )
                Column {
                    Text(
                        text = book.name,
                        style = TextStyle(
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                    )

                    Text(
                        text = book.author,
                        style = TextStyle(
                            fontWeight = FontWeight.Light,
                            fontSize = 12.sp
                        )
                    )
                    AnimatedVisibility(visible = showBookDescription) {
                        Column {

                            ExpandableText(
                                text = book.description,

                                modifier = Modifier.padding(
                                    top = 10.dp,
                                    end = 24.dp,
                                    bottom = 16.dp
                                )
                            )
                            Button(onClick = {
                                showBookDescription = false
                                onClickBuyOnAmazone(book.link, this@MainActivity)
                            }) {
                                Text(text = "Buy on Amazon", color = Color.Black)
                            }
                        }
                    }
                }
            }
        }

    }


    class BookViewModelFactory(private val booksRepo: BooksRepo) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(BooksViewModel::class.java)) {
                return BooksViewModel(booksRepo) as T
            }
            throw IllegalStateException()

        }

    }

    @ExperimentalAnimationApi
    @Preview(showBackground = true)
    @Composable
    fun UserProfilePreview(
        @PreviewParameter(BooksPreviewParameterProvider::class) book: Book
    ) {
        BookDetails(book)
    }

    class BooksPreviewParameterProvider : PreviewParameterProvider<Book> {
        override val values = sequenceOf(
            Book(
                name = "Life Skills for Teens: How to Cook",
                author = "who knows",
                image = "R.dwawable.book_cover",
                description = "The teenage years are an exciting yet ever-changing period of your life. New challenges and tasks seem to pop up almost daily—not to mention all the transitions your body is going through.",
                link = "https://amzn.to/3DYpg0K",
                category = "Juniors"
            )
        )
    }

}


