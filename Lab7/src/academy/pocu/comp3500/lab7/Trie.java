package academy.pocu.comp3500.lab7;

import java.util.ArrayList;
import java.util.HashMap;

public final class Trie {
    private final TrieNode[] roots;
    private int size;
    private int wordSize;

    public Trie() {
        this.roots = new TrieNode[TrieNode.ALPHABET_COUNT];
    }

    public int getSize() {
        return size;
    }

    public int getWordSize() {
        return wordSize;
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
                    ++this.wordSize;
                } else {
                    newNode = new TrieNode(character, isEndChar, null);
                }

                if (parentOrNull == null) {
                    assert (nodes == this.roots);
                    nodes[charIndex] = newNode;
                } else {
                    parentOrNull.addChild(newNode);
                }
                ++this.size;
            } else {
                if (isEndChar && nodes[charIndex].isWordEnd() == false) {
                    nodes[charIndex].setWord(lowWord);
                    ++this.wordSize;
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
        final ArrayList<String> candidates = new ArrayList<String>(this.wordSize);

        final FixedStack<TrieNode> nodeOrNullStack = new FixedStack<TrieNode>(2 * this.size + 1);
        final FixedStack<Integer> accessIndexOrNullStack = new FixedStack<Integer>(2 * this.size + 1);

        int wordAccessCount = 0;
        final boolean[] wordAccess = new boolean[lowWord.length()];

        final HashMap<Character, Integer[]> accessArrayStartIndex = new HashMap<Character, Integer[]>(lowWord.length());
        {
            final char[] wordArray = lowWord.toCharArray();
            Sort.radixSort(wordArray);

            for (int i = 0; i < lowWord.length(); ++i) {
                if (accessArrayStartIndex.containsKey(wordArray[i]) == false) {
                    accessArrayStartIndex.put(wordArray[i], new Integer[]{i, 1}); // Integer[] : first -> wordAccessArray index / second -> same char count
                } else {
                    ++accessArrayStartIndex.get(wordArray[i])[1];
                }
            }
        }

        accessIndexOrNullStack.push(null);
        for (final TrieNode root : this.roots) {
            if (root != null && accessArrayStartIndex.containsKey(root.getCharacter())) {
                nodeOrNullStack.push(root);
            }
        }

        while (nodeOrNullStack.isEmpty() == false) {
            final TrieNode nodeOrNull = nodeOrNullStack.pop();
            if (nodeOrNull == null) {
                assert (accessIndexOrNullStack.peek() == null);
                accessIndexOrNullStack.pop();

                while (accessIndexOrNullStack.peek() != null) {
                    wordAccess[accessIndexOrNullStack.pop()] = false;
                    --wordAccessCount;
                }
                continue;
            }
            final TrieNode node = nodeOrNull;
            assert (node != null);

            assert (accessArrayStartIndex.containsKey(node.getCharacter()));
            boolean isDifferent = true;
            {
                final int iLimit = accessArrayStartIndex.get(node.getCharacter())[0] + accessArrayStartIndex.get(node.getCharacter())[1];
                for (int i = accessArrayStartIndex.get(node.getCharacter())[0]; i < iLimit; ++i) {
                    if (wordAccess[i] == false) {
                        wordAccess[i] = true;

                        accessIndexOrNullStack.push(i);
                        ++wordAccessCount;

                        isDifferent = false;
                        break;
                    }
                }

                if (isDifferent) {
                    while (accessIndexOrNullStack.peek() != null) {
                        wordAccess[accessIndexOrNullStack.pop()] = false;
                        --wordAccessCount;
                    }
                    continue;
                }
            }

            if (node.isWordEnd() && wordAccessCount == lowWord.length()) {
                final String candidate = node.getWordOrNull();
                assert (candidate != null);
                candidates.add(candidate);

                while (accessIndexOrNullStack.peek() != null) {
                    wordAccess[accessIndexOrNullStack.pop()] = false;
                    --wordAccessCount;
                }
                continue;
            }


            accessIndexOrNullStack.push(null);
            nodeOrNullStack.push(null);
            for (final TrieNode nextNodeOrNull : node.getChildren()) {
                if (nextNodeOrNull != null && accessArrayStartIndex.containsKey(nextNodeOrNull.getCharacter())) {
                    nodeOrNullStack.push(nextNodeOrNull);
                }
            }
        }

        assert (accessIndexOrNullStack.size() == 1);
        assert (accessIndexOrNullStack.peek() == null);

        return candidates;
    }
}
