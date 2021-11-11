package academy.pocu.comp3500.lab7.app;

import academy.pocu.comp3500.lab7.Decryptor;

public class Program {

    public static void main(String[] args) {
        {
            String[] codeWords = new String[]{"a", "b", "c", "d", "e", "h", "N", "d"};

            Decryptor decryptor = new Decryptor(codeWords);

            String[] candidates = decryptor.findCandidates("n");

            assert (candidates.length == 1);
            assert (candidates[0].equals("n"));
        }

        {
            String[] codeWords = new String[]{"aaaaaaaaaaaaaaaaaaaaaaaa"};

            Decryptor decryptor = new Decryptor(codeWords);

            String[] candidates = decryptor.findCandidates("aaaaaaaaaaaaaaaaaaaaaaaa");

            assert (candidates.length == 1);
            assert (candidates[0].equals("aaaaaaaaaaaaaaaaaaaaaaaa"));
        }

        {
            String[] codeWords = new String[]{"aaaaaaaaaaaaaaaaaaaaaaaa"};

            Decryptor decryptor = new Decryptor(codeWords);

            String[] candidates = decryptor.findCandidates("aaaaaaaaaaaaaaaaaaaaaaaaa");

            assert (candidates.length == 0);
        }

        {
            String[] codeWords = new String[]{"aaaaaaaaaaaaaaaaaaaaaaaa"};

            Decryptor decryptor = new Decryptor(codeWords);

            String[] candidates = decryptor.findCandidates("aaaaaaaaaaaaaaaaaaaaaaaaaaa");

            assert (candidates.length == 0);
        }

        {
            String[] codeWords = new String[]{"cat", "CATS", "AcTS", "SCAN", "acre", "aNTS", "nNn", "N"};

            Decryptor decryptor = new Decryptor(codeWords);

            String[] candidates = decryptor.findCandidates("n");

            assert (candidates.length == 1);
            assert (candidates[0].equals("n"));
        }

        {
            String[] codeWords = new String[]{"n"};

            Decryptor decryptor = new Decryptor(codeWords);

            String[] candidates = decryptor.findCandidates("n");

            assert (candidates.length == 1);
            assert (candidates[0].equals("n"));
        }

        {
            String[] codeWords = new String[]{"cat", "CATS", "AcTS", "SCAN", "acre", "aNTS", "na"};

            Decryptor decryptor = new Decryptor(codeWords);

            String[] candidates = decryptor.findCandidates("n");

            assert (candidates.length == 0);
        }

        {
            String[] codeWords = new String[]{"cat", "carte", "CATS", "AcTS", "actarte", "SCAN", "acre", "aNTS"};

            Decryptor decryptor = new Decryptor(codeWords);

            String[] candidates = decryptor.findCandidates("cat");

            assert (candidates.length == 1);
            assert (candidates[0].equals("cat"));

            candidates = decryptor.findCandidates("race");

            assert (candidates.length == 1);
            assert (candidates[0].equals("acre"));

            candidates = decryptor.findCandidates("ca");

            assert (candidates.length == 0);

            candidates = decryptor.findCandidates("span");

            assert (candidates.length == 0);

            candidates = decryptor.findCandidates("ACT");

            assert (candidates.length == 1);
            assert (candidates[0].equals("cat"));

            candidates = decryptor.findCandidates("cats");

            assert (candidates.length == 2);
            assert (candidates[0].equals("cats") || candidates[0].equals("acts"));
            assert (candidates[1].equals("cats") || candidates[1].equals("acts"));

            candidates = decryptor.findCandidates("SCAt");

            assert (candidates.length == 2);
            assert (candidates[0].equals("cats") || candidates[0].equals("acts"));
            assert (candidates[1].equals("cats") || candidates[1].equals("acts"));
        }
    }
}