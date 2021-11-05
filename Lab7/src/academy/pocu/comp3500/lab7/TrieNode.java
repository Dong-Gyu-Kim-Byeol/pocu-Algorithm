package academy.pocu.comp3500.lab7;

public final class TrieNode {
    public final static int ALPHABET_COUNT = 26;

    private final char character;
    private boolean isWordEnd;
    private String wordOrNull;
    private final TrieNode[] children;

    public TrieNode(final char character, final boolean isWordEnd, final String wordOrNull) {
        assert ('a' <= character);
        assert (character <= 'z');

        assert (isWordEnd == true && wordOrNull != null
                || isWordEnd == false && wordOrNull == null);

        this.character = character;
        this.isWordEnd = isWordEnd;
        this.wordOrNull = wordOrNull;
        this.children = new TrieNode[ALPHABET_COUNT];
    }

    public void setWord(final String word) {
        this.isWordEnd = true;
        this.wordOrNull = word;
    }

    public void addChild(final TrieNode node) {
        this.children[node.getCharacter() - 'a'] = node;
    }

    public boolean isWordEnd() {
        return isWordEnd;
    }

    public char getCharacter() {
        return character;
    }

    public TrieNode[] getChildren() {
        return children;
    }

    public String getWordOrNull() {
        return wordOrNull;
    }
}
