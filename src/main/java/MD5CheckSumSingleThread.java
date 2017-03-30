import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**An implementation with single thread**/
public class MD5CheckSumSingleThread implements MD5CheckSum {
    /**A calculation of checksum of directory**/
    public byte[] calcCheckSum(String path) {
        return dfs(new File(path));
    }

    private byte[] dfs(File dir) {

        if (dir.isDirectory()) {
            MessageDigest digest = null;
            try {
                digest = MessageDigest.getInstance("MD5");
            } catch (NoSuchAlgorithmException e) {
                throw new RuntimeException(e);
            }
            digest.update(dir.getName().getBytes());
            String[] children = dir.list();
            for (String aChildren : children) {
                File f = new File(dir, aChildren);
                digest.update(dfs(f));
            }
            return digest.digest();
        } else {
            return calcFile(dir);
        }
    }

    private byte[] sum(byte[] first, byte[] second) {
        if (first == null)
            return second;
        byte[] result = new byte[first.length + second.length];
        System.arraycopy(first, 0, result, 0, first.length);
        System.arraycopy(second, 0, result, first.length, second.length);
        return result;
    }

    private byte[] calcFile(File file) {
        MessageDigest digest = null;
        try {
            FileInputStream inputStream = new FileInputStream(file);
            digest = MessageDigest.getInstance("MD5");
            byte[] buffer = new byte[4096];
            while (inputStream.read(buffer) != -1) {
                digest.update(buffer);
            }
        } catch (FileNotFoundException e) {
            System.out.println("file not found\n");
        } catch (NoSuchAlgorithmException | IOException e) {
            throw new RuntimeException(e);
        }
        return digest.digest();
    }

    private byte[] calcContent(byte[] content) {
        MessageDigest digest = null;
        if (content == null)
            return "".getBytes();
        try {
            digest = MessageDigest.getInstance("MD5");
            digest.update(content);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        return digest.digest();
    }
}
