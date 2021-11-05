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
                    assert (nodes == this.roots);
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

    public boolean contains(final String word) {
        final String lowWord = word.toLowerCase();

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
                assert (nodes[character - 'a'].isWordEnd());
                return true;
            }

            nodes = nodes[character - 'a'].getChildren();
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

        final boolean[] wordAccessArray = new boolean[lowWord.length()];

        while (nodeOrNullStack.empty() == false) {
            final TrieNode nodeOrNull = nodeOrNullStack.pop();
            if (nodeOrNull == null) {
                assert (accessIndexStack.peek() == -1);
                accessIndexStack.pop();

                while (accessIndexStack.peek() != -1) {
                    wordAccessArray[accessIndexStack.pop()] = false;
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

                        isDifferent = false;
                        break;
                    }
                }
            }

            if (isDifferent) {
                while (accessIndexStack.peek() != -1) {
                    wordAccessArray[accessIndexStack.pop()] = false;
                }
                continue;
            }

            if (node.isWordEnd()) {
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
