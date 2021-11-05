package academy.pocu.comp3500.lab7;


import java.util.ArrayList;

public final class Decryptor {
    final Trie trie;

    public Decryptor(final String[] codeWords) {
        this.trie = new Trie();
        for (final String word : codeWords) {
            this.trie.addWord(word.toLowerCase());
        }
    }

    public String[] findCandidates(final String word) {
        final ArrayList<String> candidates = this.trie.findSameAlphabetDifferentOrder(word);

        final String[] candidatesArray = new String[candidates.size()];
        for (int i = 0; i < candidates.size(); ++i) {
            candidatesArray[i] = candidates.get(i);
        }

        return candidatesArray;
    }
}