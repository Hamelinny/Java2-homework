import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import ru.spbau.sofronova.client.Client;
import ru.spbau.sofronova.server.Server;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static java.lang.Thread.sleep;
import static org.junit.Assert.assertArrayEquals;

public class LargeFileTest {

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    @Before
    public void prepare() throws IOException {
        folder.create();
    }

    @Test
    public void testLargeFile() throws Exception {
        Path file1 = folder.newFile("file1").toPath();
        byte[] data = new byte[100000];
        Files.write(file1, data);
        Server server = new Server(Server.SERVER_PORT);
        server.start();
        sleep(500);
        Client client = new Client(server.getPort());
        Path anotherFile = folder.newFile("check").toPath();
        client.executeGet(file1.toAbsolutePath().toString(), anotherFile.toAbsolutePath().toString());
        byte[] ans = Files.readAllBytes(anotherFile);
        assertArrayEquals(data, ans);
        server.stop();
    }


}
