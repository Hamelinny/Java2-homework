import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import ru.spbau.sofronova.Reader;
import ru.spbau.sofronova.Writer;
import ru.spbau.sofronova.client.Client;
import ru.spbau.sofronova.server.Server;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class Tests {

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    @Before
    public void prepare() throws IOException {
        folder.create();
    }

    @Test
    public void scenarioTest() throws IOException {
        Server server = new Server();
        server.start();
        Client client = new Client(Server.SERVER_PORT);

        Path file1 = folder.newFile("file1").toPath();
        Path file2 = folder.newFile("file2").toPath();
        Path dir = folder.newFolder("dir").toPath();
        byte[] data = "abacaba".getBytes();
        Files.write(file1, data);

        Path anotherFile = folder.newFile("check").toPath();
        client.executeGet(file1.toAbsolutePath().toString(), anotherFile.toAbsolutePath().toString());
        byte[] ans = Files.readAllBytes(anotherFile);
        assertArrayEquals(data, ans);

        List<String> list = client.executeList(folder.getRoot().getAbsolutePath());
        assertTrue(list != null);
        assertEquals(4, list.size());
        assertTrue(list.contains(file1.getFileName().toString()));
        assertTrue(list.contains(file2.getFileName().toString()));
        assertTrue(list.contains(dir.getFileName().toString()));
        assertTrue(list.contains(anotherFile.getFileName().toString()));
        server.stop();
    }

    @Test
    public void testEmptyResponse() throws IOException {
        ByteChannel channel = mock(ByteChannel.class);
        Reader reader = spy(new Reader(channel));
        doNothing().when(reader).read();
        reader.read();
        assertNull(reader.getContent());
    }

    @Test
    public void testInvalidPath() throws IOException {
        Server server = new Server(40000);
        server.start();
        Client client = new Client(server.getPort());
        Path anotherFile = folder.newFile("check").toPath();
        client.executeGet("abacaba", anotherFile.toAbsolutePath().toString());
        byte[] ans = Files.readAllBytes(anotherFile);
        assertArrayEquals(Long.toString(0).getBytes(), ans);
        server.stop();
    }

    @Test
    public void writeEmpty() throws IOException {
        Writer writer = new Writer(null, mock(ByteChannel.class));
        writer.write();
        assertEquals(-1, writer.getBytesWritten());
    }

    @Test
    public void testLargeFile() throws IOException {
        Path file1 = folder.newFile("file1").toPath();
        byte[] data = new byte[1000000];
        Files.write(file1, data);
        Server server = new Server(45000);
        server.start();
        Client client = new Client(server.getPort());
        Path anotherFile = folder.newFile("check").toPath();
        client.executeGet(file1.toAbsolutePath().toString(), anotherFile.toAbsolutePath().toString());
        byte[] ans = Files.readAllBytes(anotherFile);
        assertArrayEquals(data, ans);
    }
}
