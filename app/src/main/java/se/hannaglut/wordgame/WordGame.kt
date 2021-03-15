package se.hannaglut.wordgame

import kotlin.random.Random

class WordGame (words: List<String>) {
    // Reinitialize instance attributes
    private var gameStatus = "init"
    var currentWord = ""
    var foundWords = mutableListOf<String>()
    var missedWords = listOf<String>()
    lateinit var gameLetters: CharArray
    private lateinit var trie: WordTrie
    var nbWords = 0

    init {
        setupGame(words)
    }

    // Generate a random set of vowels and consonants
    private fun generateLetters(nbVowels: Int = 2, nbConsonants: Int = 5): CharArray {
        var vowels = "aeiou".toCharArray()
        vowels.shuffle()
        vowels = vowels.sliceArray(0 until nbVowels)

        var consonants = "bcdfghjklmnpqrstvwxyz".toCharArray()
        consonants.shuffle()
        consonants = consonants.sliceArray(0 until nbConsonants)

        val letters = (vowels + consonants)
        letters.sort()

        return letters
    }

    // Randomly select from a distribution
    private fun randomLettersWeighted(nbLetters: Int, distribution: MutableMap<Char, Int>): CharArray {
        var selectedLetters = CharArray(nbLetters)
        for (i in 0 until nbLetters) {
            var cumulativeWeights = mutableListOf<Pair<Char, Int>>()
            var total = 0
            for ((letter, weight) in distribution) {
                total += weight
                cumulativeWeights.add(Pair(letter, total))
            }
            var j =0
            var weight = 0
            var letter = ' '
            val randomValue = Random.nextInt(total)
            while (j < cumulativeWeights.count() && cumulativeWeights[j].second <= randomValue) {
                j += 1
            }
            selectedLetters[i] = cumulativeWeights[j].first
            distribution.remove(cumulativeWeights[j].first)
        }
        return selectedLetters
    }

    // Generate a random set of vowels and consonants
    private fun generateLettersWeighted(nbVowels: Int = 2, nbConsonants: Int = 5): CharArray {
        var vowelsProportions: MutableMap<Char, Int> = mutableMapOf(
                'a' to 38769,
                'e' to 59060,
                'i' to 43863,
                'o' to 30004,
                'u' to 18992,
        )
        val vowels = randomLettersWeighted(2, vowelsProportions)

        var consonantsProportions: MutableMap<Char, Int> = mutableMapOf(
                'b' to 9958,
                'c' to 21232,
                'd' to 21241,
                'f' to 7166,
                'g' to 14712,
                'h' to 10817,
                'j' to 899,
                'k' to 4198,
                'l' to 27592,
                'm' to 13473,
                'n' to 38057,
                'p' to 14167,
                'q' to 991,
                'r' to 36354,
                's' to 42674,
                't' to 35174,
                'v' to 5683,
                'w' to 4227,
                'x' to 1410,
                'y' to 9048,
                'z' to 1698,
        )
        val consonants = randomLettersWeighted(5, consonantsProportions)

        val letters = (vowels + consonants)
        letters.sort()

        return letters
    }

    // Setup a game
    private fun setupGame(words: List<String>) {
        nbWords = 0
        // Generate until a fair game is found
        while (nbWords < 30 || nbWords > 99951) {
            // Generate a set of letters for the game
            gameLetters = generateLettersWeighted()
//            gameLetters = generateLetters()
            // Create a trie of all valid words using only the set of letters
            trie = WordTrie(gameLetters, words)
            // Count the number of words
            nbWords = trie.count()
            println(gameLetters)
            println(trie.getWords())
        }
    }

    fun inputLetter(letter: Char) {
        if (gameStatus != "solved") {
            currentWord += letter
            gameStatus = "update_word"
        }
    }

    fun eraseWord() {
        if (gameStatus != "solved") {
            currentWord = ""
            gameStatus = "update_word"
        }
    }

    fun eraseLetter() {
        if (gameStatus != "solved") {
            currentWord = currentWord.dropLast(1)
            gameStatus = "update_word"
        }
    }

    fun enterWord(): Boolean {
        var res = false
        if (gameStatus != "solved") {
            if (currentWord in trie && currentWord !in foundWords) {
                foundWords.add(currentWord)
                currentWord = ""
                gameStatus = "new_word"
                res = true
            }
            else {
                currentWord = ""
                gameStatus = "update_word"
            }
        }
        return res
    }

    fun solve(): Boolean {
        var res = false
        if (gameStatus != "solved") {
            missedWords = trie.getWords().filterNot{word -> foundWords.contains(word)}
            gameStatus = "solved"
            currentWord = ""
            res = true
        }
        return res
    }

    fun restart() {
        // Reinitialize instance attributes
        currentWord = ""
        foundWords = mutableListOf(String())
        missedWords = mutableListOf(String())
        //setupGame()
        gameStatus = "restart"
    }
}
