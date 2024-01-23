package se.example.chucknorrisjokes

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import java.net.URL
import javax.net.ssl.HttpsURLConnection
import org.json.JSONObject

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val viewModel = JokeViewModel()
        viewModel.fetchJoke()
        setContent {
            val joke = viewModel.joke.observeAsState()
            JokeComposable(joke = joke.value ?: "Loading joke...")
        }
    }
}

class JokeViewModel : ViewModel() {
    private val _joke = MutableLiveData<String>()
    val joke: LiveData<String> get() = _joke

    fun fetchJoke() {
        viewModelScope.launch {
            try {
                val url = URL("https://api.chucknorris.io/jokes/random")
                with(url.openConnection() as HttpsURLConnection) {
                    requestMethod = "GET"
                    inputStream.bufferedReader().use {
                        val response = it.readText()
                        val jsonObject = JSONObject(response)
                        _joke.postValue(jsonObject.getString("value"))
                    }
                }
            } catch (e: Exception) {
                _joke.postValue("Failed to load joke: ${e.message}")
            }
        }
    }
}

@Composable
fun JokeComposable(joke: String) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.LightGray)
    ) {
        Text(
            text = joke,
            fontSize = 20.sp
        )
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    JokeComposable(joke = "This is a preview of a Chuck Norris joke!")
}
