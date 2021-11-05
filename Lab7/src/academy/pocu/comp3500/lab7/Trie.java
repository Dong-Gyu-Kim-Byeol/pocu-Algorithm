package academy.pocu.comp3500.lab7;

import java.util.ArrayList;
import java.util.Stack;

public final class Trie {
    private final TrieNode[] roots;

    public Trie() {
        this.roots = new TrieNode[TrieNode.ALPHABET_COUNT];
    }

    public void addWord(final String word) {
        final String lowWord = word.toLowerCase();

        TrieNode parentOrNull = null;
        TrieNode[] nodes = this.roots;
        int wordIndex = 0;
        while (wordIndex < lowWord.length()) {
            final char character = lowWord.charAt(wordIndex);
            assert ('a' <= character);
            assert (character <= 'z');

            final boolean isEndChar = wordIndex == lowWord.length() - 1;

            if (nodes[character - 'a'] == null) {
                final TrieNode newNode;
                if (isEndChar) {
                    newNode = new TrieNode(character, isEndChar, lowWord);
                } else {
                    newNode = new TrieNode(character, isEndChar, null);
                }

                if (parentOrNull == null) {
                    nodes[character - 'a'] = newNode;
                } else {
                    parentOrNull.addChild(newNode);
                }
            }

            parentOrNull = nodes[character - 'a'];
            nodes = parentOrNull.getChildren();
            ++wordIndex;
        }
    }

    public boolean findWord(final String word) {
        final String lowWord = word.toLowerCase();

        TrieNode parentOrNull = null;
        TrieNode[] nodes = this.roots;
        int wordIndex = 0;
        while (wordIndex < lowWord.length()) {
            final char character = lowWord.charAt(wordIndex);
            assert ('a' <= character);
            assert (character <= 'z');

            final boolean isEndChar = wordIndex == lowWord.length() - 1;

            if (nodes[character - 'a'] == null) {
                return false;
            }

            if (isEndChar) {
                return true;
            }

            parentOrNull = nodes[character - 'a'];
            nodes = parentOrNull.getChildren();
            ++wordIndex;
        }

        assert (false);
        return true;
    }

    public ArrayList<String> findSameAlphabetDifferentOrder(final String word) {
        final String lowWord = word.toLowerCase();
        final ArrayList<String> candidates = new ArrayList<String>();

        final Stack<TrieNode> nodeStack = new Stack<TrieNode>();
        for (int i = 0; i < this.roots.length; ++i) {
            if (this.roots[i] != null) {
                nodeStack.push(this.roots[i]);
            }
        }

        final boolean[] wordAccessArray = new boolean[lowWord.length()];
        while (nodeStack.empty() == false) {
            final TrieNode node = nodeStack.pop();

            boolean isDifferent = true;
            for (int i = 0; i < wordAccessArray.length; ++i) {
                if (node.getCharacter() == lowWord.charAt(i)) {
                    if (wordAccessArray[i] == false) {
                        wordAccessArray[i] = true;
                        isDifferent = false;
                        break;
                    }
                }
            }

            if (isDifferent) {
                for (int i = 0; i < wordAccessArray.length; ++i) {
                    wordAccessArray[i] = false;
                }
                continue;
            }

            if (node.isWordEnd()) {
                boolean isSame = true;
                for (int i = 0; i < wordAccessArray.length; ++i) {
                    if (wordAccessArray[i] == false) {
                        isSame = false;
                        break;
                    }
                }

                if (isSame) {
                    final String candidate = node.getWordOrNull();
                    assert (candidate != null);
                    candidates.add(candidate);
                }
            }


            for (int i = 0; i < node.getChildren().length; ++i) {
                if (node.getChildren()[i] != null) {
                    nodeStack.push(node.getChildren()[i]);
                }
            }
        }


        return candidates;
    }
}
