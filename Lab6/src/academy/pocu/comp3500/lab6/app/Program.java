package academy.pocu.comp3500.lab6.app;

import academy.pocu.comp3500.lab6.BinaryTree;
import academy.pocu.comp3500.lab6.League;
import academy.pocu.comp3500.lab6.leagueofpocu.Player;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Random;
import java.util.function.Function;

public class Program {

    public static void main(String[] args) {
        {
            // Constructors
            League emptyLeague = new League();

            Player[] emptyLeaguePlayers = emptyLeague.getTop(10);

            assert (emptyLeaguePlayers.length == 0);

            Player player1 = new Player(1, "player1", 4);
            Player player2 = new Player(2, "player2", 6);
            Player player3 = new Player(3, "player3", 6);
            Player player4 = new Player(4, "player4", 7);
            Player player5 = new Player(5, "player5", 10);
            Player player6 = new Player(6, "player6", 12);

            League league1 = new League(new Player[]{player1, player2, player3, player4, player5, player6}, true);
            League league2 = new League(new Player[]{player6, player4, player1, player2, player5, player3}, false);

            // findMatchOrNull()
            Player match = league1.findMatchOrNull(player2);
            assert (match.getId() == player3.getId());

            match = league1.findMatchOrNull(player4);
            assert (match.getId() == player2.getId() || match.getId() == player3.getId());

            match = league1.findMatchOrNull(player5);
            assert (match.getId() == player6.getId());

            // getTop(), getBottom()
            Player[] topPlayers = league2.getTop(3);

            assert (topPlayers[0].getId() == player6.getId());
            assert (topPlayers[1].getId() == player5.getId());
            assert (topPlayers[2].getId() == player4.getId());

            Player[] bottomPlayers = league2.getBottom(3);

            assert (bottomPlayers[0].getId() == player1.getId());
            assert ((bottomPlayers[1].getId() == player2.getId() && bottomPlayers[2].getId() == player3.getId())
                    || (bottomPlayers[1].getId() == player3.getId() && bottomPlayers[2].getId() == player2.getId()));

            // join()
            boolean joinSuccess = league1.join(new Player(7, "player7", 9));
            assert (joinSuccess);

            joinSuccess = league1.join(new Player(1, "player1", 4));
            assert (!joinSuccess);

            // leave()
            boolean leaveSuccess = league1.leave(new Player(5, "player5", 10));
            assert (leaveSuccess);

            leaveSuccess = league1.leave(new Player(5, "player5", 10));
            assert (!leaveSuccess);
        }

        {
            final BinaryTree<Player> tree = new BinaryTree<Player>(Comparator.comparing(Player::getId), Player::getRating);
            final HashMap<Integer, Player> map = new HashMap<Integer, Player>();

            Random random = new Random();
            for (int i = 0; i < 10000; i++) {
                int id1 = random.nextInt(20);
                Player player1 = new Player(id1, "player" + id1, id1);
                if (map.containsKey(player1.getId()) == false) {
                    map.put(player1.getId(), player1);
                }
                tree.insert(player1);

                int id2 = random.nextInt(20);
                Player player2 = new Player(id2, "player" + id2, id2);
                map.remove(player2.getId());
                tree.delete(player2);
            }

            assert (map.size() == tree.size());
        }
    }


}