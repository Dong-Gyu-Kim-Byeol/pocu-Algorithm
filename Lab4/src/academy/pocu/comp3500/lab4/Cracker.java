package academy.pocu.comp3500.lab4;

import academy.pocu.comp3500.lab4.pocuhacker.RainbowTable;
import academy.pocu.comp3500.lab4.pocuhacker.User;

public class Cracker {
    private final User[] userTable;
    private final String email;
    private final String password;

    public Cracker(final User[] userTable, final String email, final String password) {
        this.userTable = userTable;
        this.email = email;
        this.password = password;
    }

    public String[] run(final RainbowTable[] rainbowTables) {
        assert (rainbowTables.length == 5);
        final RainbowTable crc32RainbowTable = rainbowTables[0];
        final RainbowTable md2RainbowTable = rainbowTables[1];
        final RainbowTable md5RainbowTable = rainbowTables[2];
        final RainbowTable sha1RainbowTable = rainbowTables[3];
        final RainbowTable sha256RainbowTable = rainbowTables[4];
        final String[] plainPasswords = new String[this.userTable.length];

        EHashType hashType = null;
        for (final User user : this.userTable) {
            if (user.getEmail().equals(this.email)) {
                hashType = EHashType.getHashTypeOrNull(user.getPasswordHash(), this.password);
                break;
            }
        }

        if (hashType == null) {
            return plainPasswords;
        }

        RainbowTable targetRainbowTable;
        switch (hashType) {
            case CRC32:
                targetRainbowTable = crc32RainbowTable;
                break;
            case MD2:
                targetRainbowTable = md2RainbowTable;
                break;
            case MD5:
                targetRainbowTable = md5RainbowTable;
                break;
            case SHA1:
                targetRainbowTable = sha1RainbowTable;
                break;
            case SHA256:
                targetRainbowTable = sha256RainbowTable;
                break;
            default:
                assert (false);
                throw new IllegalArgumentException("unknown type");
        }

        int index = 0;
        for (final User user : this.userTable) {
            final String plain = targetRainbowTable.get(user.getPasswordHash());
            if (plain != null) {
                plainPasswords[index] = plain;
            }
            ++index;
        }

        return plainPasswords;
    }
}
