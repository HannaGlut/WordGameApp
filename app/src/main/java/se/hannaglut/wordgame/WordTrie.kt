package se.hannaglut.wordgame

class WordTrie(private val game_letters: CharArray, private val valid_words: List<String>) {
    // Initialize a trie data structure using the given letters and a provided list of valid words
    private val root = TrieNode()
    private val nbWords: Int = root.count()

    // A class for trie nodes
    inner class TrieNode( prefix: String = "",
                          _n: Int = 0,
                          level: Int = 0,
                          public var isWord: Boolean = false) {
        // Initialize attributes
        val children = mutableMapOf<Char, TrieNode>()

        // Initialize a trie node containing all valid words using only the selected letters
        init {
            var n: Int = _n

            // For each one of the possible children (assumed sorted)
            for (letter in game_letters) {
                // While the prefix is still the same and we have not yet arrived at the next letter
                while (n < valid_words.count()
                    && valid_words[n].count() > level
                    && valid_words[n].substring(0, level) == prefix
                    && valid_words[n][level] < letter) {
                    // Step forward in the list of valid words
                    n += 1
                }
                // Go through the words with this letter at the next position
                while (valid_words.count() > n
                    && valid_words[n].count() > level
                    && valid_words[n].substring(0, level) == prefix
                    && valid_words[n][level] == letter) {
                    val suffix: String = valid_words[n].substring(level + 1)
                    // If any of the words encountered consists of only letters that are in this game
                    if (suffix.all{ game_letters.contains(it) }) {
                        // Verify if the node marks the end of a valid word:
                        // If the current letter is the last letter of the current word
                        var endOfWord: Boolean = false
                        var offset: Int = 0
                        if (valid_words[n].count() == level + 1) {
                            endOfWord = true
                            offset = 1
                        }
                        // Create a new trie node
                        children[letter] = TrieNode(prefix + letter,n + offset,level + 1, endOfWord)
                        n += 1
                        // No need to go any further
                        break
                    }
                    n += 1
                }
            }
        }

        // Converts the trie structure to a list
        fun getWords (prefix: String = ""): MutableList<String> {
            // Initialize the return variable
            val words = mutableListOf<String>()

            // If the current node terminates a word
            if (isWord) {
                // Add the word to the output
                words.add(prefix)
            }

            // For each one of the children of the node
            for ((letter, node) in children) {
                // Make a recursive call
                words.addAll(node.getWords(prefix + letter))
            }

            // Return the words found
            return words
        }

        // Returns the number of words in the trie
        fun count(): Int {
            // Initialize the return variable
            var nbWords: Int = 0

            // If the current node terminates a word
            if (isWord) {
                // Add the word to the output
                nbWords += 1
            }

            // For each one of the children of the node: Make a recursive call
            children.values.forEach{node -> nbWords += node.count()}

            // Return the words found
            return nbWords
        }
    }

    // Implements the in operator : Returns true if word is in the trie
    public operator fun contains(candidate: String): Boolean {
        // Start the search at the root of the trie and at the first letter of the word
        var node: TrieNode = root
        var i: Int = 0
        var res: Boolean = false

        // While the node has a child with that letter
        while (i < candidate.count() && node.children.containsKey(candidate[i])) {
            // Move downwards in the trie and forwards in the word
            node = node.children[candidate[i]]!!
            i += 1
        }

        // If we have reached the end of the word and the node corresponds to the end of a valid word
        if (i == candidate.count() && node.isWord) {
            res = true
        }

        return res
    }

    // Returns all the words in the trie as a list
    public fun getWords(): MutableList<String> {
        // Delegate the work to the recursive function defined for trie nodes
        return root.getWords()
    }

    // Returns the number of words in the trie
    public fun count(): Int {
        // Delegate the work to the recursive function defined for trie nodes
//        return root.count()
        return nbWords
    }
}
