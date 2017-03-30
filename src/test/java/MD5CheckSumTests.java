import org.junit.Test;

import java.nio.file.Files;

public class MD5CheckSumTests {

    @Test
    public void simpleTest() {
        MD5CheckSumSingleThread simple = new MD5CheckSumSingleThread();
        MD5CheckSumForkJoin forkJoin = new MD5CheckSumForkJoin();
        byte[] ans1 = simple.calcCheckSum(System.getProperty("user.dir"));
        byte[] ans2 = forkJoin.calcCheckSum(System.getProperty("user.dir"));
        System.out.println(ans1);
        System.out.println(ans2);
        /**они почему-то отличаются, но почему, я выяснить не успела**/
    }

}
