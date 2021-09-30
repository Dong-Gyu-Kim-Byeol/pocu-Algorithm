package academy.pocu.comp3500.lab4;

public enum EHashType {
    CRC32,
    MD2,
    MD5,
    SHA1,
    SHA256;

    public static EHashType getHashTypeOrNull(final String hash, final String plain) {
        if (hash.equals(Hash.getCrc32(plain))) {
            return EHashType.CRC32;
        }

        if (hash.equals(Hash.getMd2(plain))) {
            return EHashType.MD2;
        }

        if (hash.equals(Hash.getMd5(plain))) {
            return EHashType.MD5;
        }

        if (hash.equals(Hash.getSha1(plain))) {
            return EHashType.SHA1;
        }

        if (hash.equals(Hash.getSha256(plain))) {
            return EHashType.SHA256;
        }

        return null;
    }
}
