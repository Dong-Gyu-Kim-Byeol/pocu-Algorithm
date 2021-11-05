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
            final int charIndex = character - 'a';

            if (nodes[charIndex] == null) {
                final TrieNode newNode;
                if (isEndChar) {
                    newNode = new TrieNode(character, isEndChar, lowWord);
                } else {
                    newNode = new TrieNode(character, isEndChar, null);
                }

                if (parentOrNull == null) {
                    assert (nodes == this.roots);
                    nodes[charIndex] = newNode;
                } else {
                    parentOrNull.addChild(newNode);
                }
            } else {
                if (isEndChar) {
                    nodes[charIndex].setWord(lowWord);
                }
            }

            parentOrNull = nodes[charIndex];
            nodes = parentOrNull.getChildren();
            ++wordIndex;
        }
    }

    public boolean contains(final String word) {
        final String lowWord = word.toLowerCase();

        TrieNode[] nodes = this.roots;
        int wordIndex = 0;

        while (wordIndex < lowWord.length()) {
            final char character = lowWord.charAt(wordIndex);
            assert ('a' <= character);
            assert (character <= 'z');

            final boolean isEndChar = wordIndex == lowWord.length() - 1;
            final int charIndex = character - 'a';

            if (nodes[charIndex] == null) {
                return false;
            }

            if (isEndChar) {
                assert (nodes[charIndex].isWordEnd());
                return true;
            }

            nodes = nodes[charIndex].getChildren();
            ++wordIndex;
        }

        assert (false);
        return true;
    }

    public ArrayList<String> findSameAlphabetDifferentOrder(final String word) {
        final String lowWord = word.toLowerCase();
        final ArrayList<String> candidates = new ArrayList<String>();

        final Stack<TrieNode> nodeOrNullStack = new Stack<TrieNode>();
        final Stack<Integer> accessIndexStack = new Stack<Integer>();

        accessIndexStack.push(-1);
        for (final TrieNode node : this.roots) {
            if (node != null) {
                nodeOrNullStack.push(node);
            }
        }

        int wordAccessCount = 0;
        final boolean[] wordAccessArray = new boolean[lowWord.length()];

        while (nodeOrNullStack.empty() == false) {
            final TrieNode nodeOrNull = nodeOrNullStack.pop();
            if (nodeOrNull == null) {
                assert (accessIndexStack.peek() == -1);
                accessIndexStack.pop();

                while (accessIndexStack.peek() != -1) {
                    wordAccessArray[accessIndexStack.pop()] = false;
                    --wordAccessCount;
                }
                continue;
            }
            final TrieNode node = nodeOrNull;
            assert (node != null);

            boolean isDifferent = true;
            for (int i = 0; i < wordAccessArray.length; ++i) {
                if (node.getCharacter() == lowWord.charAt(i)) {
                    if (wordAccessArray[i] == false) {
                        wordAccessArray[i] = true;

                        accessIndexStack.push(i);
                        ++wordAccessCount;

                        isDifferent = false;
                        break;
                    }
                }
            }

            if (isDifferent) {
                while (accessIndexStack.peek() != -1) {
                    wordAccessArray[accessIndexStack.pop()] = false;
                    --wordAccessCount;
                }
                continue;
            }

            if (node.isWordEnd() && wordAccessCount == lowWord.length()) {
                boolean isSame = true;
                for (final boolean access : wordAccessArray) {
                    if (access == false) {
                        isSame = false;
                        break;
                    }
                }

                if (isSame) {
                    final String candidate = node.getWordOrNull();
                    assert (candidate != null);
                    candidates.add(candidate);

                    while (accessIndexStack.peek() != -1) {
                        wordAccessArray[accessIndexStack.pop()] = false;
                        --wordAccessCount;
                    }
                    continue;
                }
            }


            accessIndexStack.push(-1);
            nodeOrNullStack.push(null);
            for (int i = 0; i < node.getChildren().length; ++i) {
                if (node.getChildren()[i] != null) {
                    nodeOrNullStack.push(node.getChildren()[i]);
                }
            }
        }

        assert (accessIndexStack.size() == 1);
        assert (accessIndexStack.peek() == -1);

        return candidates;
    }
}
