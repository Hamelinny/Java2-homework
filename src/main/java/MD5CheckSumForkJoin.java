import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;



/**An implementation with ForkJoinPool**/
public class MD5CheckSumForkJoin implements MD5CheckSum {
    /**A calculation of checksum of directory**/
    public byte[] calcCheckSum(String path) {
        ForkJoinPool pool = new ForkJoinPool();
        DfsTask task = new DfsTask(new File(path));
        pool.submit(task);
        try {
            return task.get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    private class DfsTask extends RecursiveTask<byte[]> {
        private final File start;

        DfsTask(File start) {
            this.start = start;
        }

        @Override
        protected byte[] compute() {
            try {
                if (start.isDirectory()) {
                    List<DfsTask> subTasks = new LinkedList<>();
                    MessageDigest digest = null;
                    try {
                        digest = MessageDigest.getInstance("MD5");
                    } catch (NoSuchAlgorithmException e) {
                        throw new RuntimeException(e);
                    }
                    String[] children = start.list();
                    for (String aChildren : children) {
                        File f = new File(start, aChildren);
                        DfsTask childTask = new DfsTask(f);
                        childTask.fork();
                        subTasks.add(childTask);
                    }
                    for (DfsTask task : subTasks) {
                        task.join();
                        digest.update(task.get());
                    }
                    return digest.digest();
                } else {
                    return calcFile(start);
                }
            }
            catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
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






}
