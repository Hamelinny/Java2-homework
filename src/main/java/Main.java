
public class Main {

    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("please enter name of directory\n");
            return;
        }
        if (args.length > 1) {
            System.out.println("too many arguments\n");
        }
        MD5CheckSumSingleThread simple = new MD5CheckSumSingleThread();
        MD5CheckSumForkJoin forkJoin = new MD5CheckSumForkJoin();
        byte[] ans1 = simple.calcCheckSum(System.getProperty("user.dir"));
        byte[] ans2 = forkJoin.calcCheckSum(System.getProperty("user.dir"));
        System.out.println(ans1);
        System.out.println(ans2);
        ans1 = simple.calcCheckSum(args[0]);
        ans2 = forkJoin.calcCheckSum(args[0]);
        System.out.println(ans1);
        System.out.println(ans2);
    }
}
