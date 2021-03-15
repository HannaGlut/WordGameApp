package se.hannaglut.wordgame

import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.children
import androidx.core.view.forEach
import androidx.databinding.DataBindingUtil
import se.hannaglut.wordgame.databinding.ActivityMainBinding
import kotlin.math.ceil

class MainActivity : AppCompatActivity() {

    private lateinit var game: WordGame
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        initializeGame()

        binding.eraseButton.setOnClickListener {
            game.eraseWord()
            binding.currentWord.text = ""
        }

        binding.backButton.setOnClickListener {
            game.eraseLetter()
            binding.currentWord.text = game.currentWord.toUpperCase()
        }

        binding.enterButton.setOnClickListener {
            if (game.enterWord()) {
                val wordsPerColumn = ceil(game.nbWords.toFloat() / (binding.foundWordsLayout.childCount - 1)).toInt() + 1
//                val i = (game.foundWords.count() * (binding.foundWordsLayout.childCount - 1)) / game.nbWords
                val i = game.foundWords.count() / wordsPerColumn
                val column = binding.foundWordsLayout.getChildAt(i) as TextView
                column.setTextColor(Color.parseColor("#000000"))
                column.append(game.foundWords.last() + "\n")
                binding.outputWordScrollView.scrollTo(0, binding.outputWordScrollView.getChildAt(0).height)
                binding.completedBar.progress = game.foundWords.count()
            }
            binding.currentWord.text = game.currentWord
        }

        binding.solveButton.setOnClickListener {
            if (game.solve()) {
                val i = (game.foundWords.count() * (binding.foundWordsLayout.childCount - 1)) / game.nbWords + 1
                val wordsPerColumn = ceil(game.nbWords.toFloat() / (binding.foundWordsLayout.childCount - 1)).toInt()
                var k = 0
                for (j in i until binding.foundWordsLayout.childCount) {
                    val column = binding.foundWordsLayout.getChildAt(j) as TextView
                    column.setTextColor(Color.parseColor("#d33737"))
                    var m = 0
                    while (k < game.missedWords.count() && m++ < wordsPerColumn) {
                        column.append(game.missedWords[k++] + "\n")
                    }
                }
            }
            binding.currentWord.text = game.currentWord
        }

        binding.resetButton.setOnClickListener {
            binding.currentWord.text = ""
            binding.foundWordsLayout.children.forEach {
                val column = it as TextView
                column.text = ""
            }
            initializeGame()
        }
    }

    private fun initializeGame(){
        val words = applicationContext.assets.open("english_words.txt").bufferedReader().readLines()
        game = WordGame(words)

        setTileLetters()

        binding.completedBar.max = game.nbWords
        binding.completedBar.progress = 0
    }

    private fun setTileLetters(){
        game.gameLetters.shuffle()
        val layout: LinearLayout = binding.tilesLayout
        var i: Int = 0
        layout.forEach {
            if (it is LinearLayout) {
                val nestedTilesLayout: LinearLayout = it as LinearLayout
                nestedTilesLayout.forEach {
                    val tile = it as Button
                    it.text = game.gameLetters[i++].toString()
                    tile.setOnClickListener {
                        val letter = tile.text.toString().toCharArray()[0]
                        game.inputLetter(letter)
                        binding.currentWord.text = game.currentWord.toUpperCase()
                    }
                }
            }
        }
    }
}