package academy.pocu.comp3500.lab4.app;

import academy.pocu.comp3500.lab4.Cracker;
import academy.pocu.comp3500.lab4.Hash;
import academy.pocu.comp3500.lab4.pocuhacker.RainbowTable;
import academy.pocu.comp3500.lab4.pocuhacker.User;

import java.util.HashMap;
import java.util.Map;

public class Program {
    public static void main(String[] args) {
        HashMap<String, String> crc32Map = new HashMap<>(Map.of(
                "211534962", "0000",
                "477404077", "letmein",
                "55151997", "qwerty",
                "901924565", "password"));

        assert (crc32Map.get(Hash.getCrc32("0000")).equals("0000"));
        assert (crc32Map.get(Hash.getCrc32("letmein")).equals("letmein"));
        assert (crc32Map.get(Hash.getCrc32("qwerty")).equals("qwerty"));
        assert (crc32Map.get(Hash.getCrc32("password")).equals("password"));


        HashMap<String, String> md2Map = new HashMap<>(Map.of(
                "yiRNCBNQgQETz6+ieP/VgQ==", "0000",
                "EfIl0sd6mcLoS45wACqTUg==", "letmein",
                "wssIXCT4UJhuVfHESr5odg==", "qwerty",
                "8DiBqIxuORNfDsxg79YJuQ==", "password"));

        assert (md2Map.get(Hash.getMd2("0000")).equals("0000"));
        assert (md2Map.get(Hash.getMd2("letmein")).equals("letmein"));
        assert (md2Map.get(Hash.getMd2("qwerty")).equals("qwerty"));
        assert (md2Map.get(Hash.getMd2("password")).equals("password"));


        HashMap<String, String> md5Map = new HashMap<>(Map.of(
                "Sn0e1BRHTkAzrCnMuGU9mw==", "0000",
                "DRB9CfW75Ayt495ccenptw==", "letmein",
                "2FeO34RYzgb7xbt2pYxcpA==", "qwerty",
                "X03MO1qnZdYdgyfeuILPmQ==", "password"));

        assert (md5Map.get(Hash.getMd5("0000")).equals("0000"));
        assert (md5Map.get(Hash.getMd5("letmein")).equals("letmein"));
        assert (md5Map.get(Hash.getMd5("qwerty")).equals("qwerty"));
        assert (md5Map.get(Hash.getMd5("password")).equals("password"));


        HashMap<String, String> sha1Map = new HashMap<>(Map.of(
                "Od+lUoMxjTGv5aP/Sg4yU+IEXkM=", "0000",
                "t6h1/B6iKLkGEEG3zsS9PFKrPOM=", "letmein",
                "sbN3OgXA7QF2eHpPFXT/AHX3Uh4=", "qwerty",
                "W6ph5Mm5Pz8GgiULbPgzG37mj9g=", "password"));

        assert (sha1Map.get(Hash.getSha1("0000")).equals("0000"));
        assert (sha1Map.get(Hash.getSha1("letmein")).equals("letmein"));
        assert (sha1Map.get(Hash.getSha1("qwerty")).equals("qwerty"));
        assert (sha1Map.get(Hash.getSha1("password")).equals("password"));


        HashMap<String, String> sha256Map = new HashMap<>(Map.of(
                "mvFbM25qlhmShTffMLLmojdlafz51+dz7M7eZWBlKaA=", "0000",
                "HIv+j4AdeXRcRjHQn/82yCqjf8TM5PyUZoPXsza2MDI=", "letmein",
                "ZehL4zUy+3hMSBKWdfnv86aCsnFowOp0Syz1juAjN8U=", "qwerty",
                "XohImNooBHFR0OVvjcYpJ3NgPQ1qq73WKhHvch0VQtg=", "password"));

        assert (sha256Map.get(Hash.getSha256("0000")).equals("0000"));
        assert (sha256Map.get(Hash.getSha256("letmein")).equals("letmein"));
        assert (sha256Map.get(Hash.getSha256("qwerty")).equals("qwerty"));
        assert (sha256Map.get(Hash.getSha256("password")).equals("password"));


        RainbowTable[] rainbowTables = new RainbowTable[]{
                new RainbowTable(crc32Map),
                new RainbowTable(md2Map),
                new RainbowTable(md5Map),
                new RainbowTable(sha1Map),
                new RainbowTable(sha256Map),
        };

        final String email = "notahacker@not.a.hacker";
        final String password = "notahackerpassword";

        final String normalUser1 = "john.smith@te.st";
        final String normalUser2 = "hong.gil.dong@nor.mal";

        // CRC32
        {
            User[] userTable = new User[]{
                    new User("001", normalUser1, "2418662205"),
                    new User("004", email, "632000577"),
                    new User("011", normalUser2, "477404077")
            };

            Cracker cracker = new Cracker(userTable, email, password);
            String[] plainTexts = cracker.run(rainbowTables);

            assert (plainTexts[0] == null);
            assert (plainTexts[1] == null);
            assert (plainTexts[2] != null && plainTexts[2].equals("letmein"));
        }

        // MD2
        {
            User[] userTable = new User[]{
                    new User("001", normalUser1, "8DiBqIxuORNfDsxg79YJuQ=="),
                    new User("005", normalUser2, "yiRNCBNQgQETz6+ieP/VgQ=="),
                    new User("006", email, "UHkDM4kEQC1JUsXEPN3QcA==")
            };

            Cracker cracker = new Cracker(userTable, email, password);
            String[] plainTexts = cracker.run(rainbowTables);

            assert (plainTexts[0] != null && plainTexts[0].equals("password"));
            assert (plainTexts[1] != null && plainTexts[1].equals("0000"));
            assert (plainTexts[2] == null);
        }

        // MD5
        {
            User[] userTable = new User[]{
                    new User("010", email, "lQGk5Otx90KH95fKA25Aug=="),
                    new User("011", normalUser1, "2FeO34RYzgb7xbt2pYxcpA=="),
                    new User("012", normalUser2, "6v2Gb022xeiHfqvTTmmT/g==")
            };

            Cracker cracker = new Cracker(userTable, email, password);
            String[] plainTexts = cracker.run(rainbowTables);

            assert (plainTexts[0] == null);
            assert (plainTexts[1] != null && plainTexts[1].equals("qwerty"));
            assert (plainTexts[2] == null);
        }

        // SHA1
        {
            User[] userTable = new User[]{
                    new User("001", normalUser2, "Od+lUoMxjTGv5aP/Sg4yU+IEXkM="),
                    new User("002", email, "LhcvnqAh1/Tme0rYqG2R37+J8ak="),
                    new User("003", normalUser1, "IpvINWW5+7SOw7I5/cAVuc81jXc=")
            };

            Cracker cracker = new Cracker(userTable, email, password);
            String[] plainTexts = cracker.run(rainbowTables);

            assert (plainTexts[0] != null && plainTexts[0].equals("0000"));
            assert (plainTexts[1] == null);
            assert (plainTexts[2] == null);
        }

        // SHA256
        {
            User[] userTable = new User[]{
                    new User("001", email, "08WISV7yGWsQpUCXlnErNl6ledurwx7pRhPGiS3zhIA="),
                    new User("002", normalUser2, "XohImNooBHFR0OVvjcYpJ3NgPQ1qq73WKhHvch0VQtg="),
                    new User("003", normalUser1, "/Z7d6Us6HBWG5GIez6AHWSJg2irdWAdXKlsO+6WnVhI=")
            };

            Cracker cracker = new Cracker(userTable, email, password);
            String[] plainTexts = cracker.run(rainbowTables);

            assert (plainTexts[0] == null);
            assert (plainTexts[1] != null && plainTexts[1].equals("password"));
            assert (plainTexts[2] == null);
        }
    }
}