/** An interface to provide a calculation of a directory check sum **/
public interface MD5CheckSum {
    byte[] calcCheckSum(String path);
}
