import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import ru.spbau.sofronova.entities.Blob;
import ru.spbau.sofronova.exceptions.*;
import ru.spbau.sofronova.logic.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static ru.spbau.sofronova.logic.MyGitUtils.*;


public class MyGitTests {

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    private MyGit rep;
    private MyGitLogs logs;
    private MyGitBranch brnch;
    private MyGitHead head;
    private MyGitIndex ind;

    @Before
    public void preparation() throws IOException, GitDoesNotExistException, BranchIOException,
            InitIOException, GitAlreadyInitializedException, LogIOException, IndexIOException,
            BranchAlreadyExistsException, ObjectIOException, ObjectStoreException, HeadIOException {
        rep = new MyGit(folder.newFolder("oops").getAbsolutePath());
        logs = new MyGitLogs(rep);
        brnch = new MyGitBranch(rep);
        head = new MyGitHead(rep);
        ind = new MyGitIndex(rep);
        rep.init();
    }

    @Test
    public void initTest()  {
        assertTrue(Files.exists(rep.GIT_DIRECTORY));
        assertTrue(Files.exists(rep.INDEX));
        assertTrue(Files.exists(rep.REFS_DIRECTORY));
        assertTrue(Files.exists(rep.OBJECTS_DIRECTORY));
        assertTrue(Files.exists(rep.HEAD));
        assertTrue(Files.exists(rep.LOGS_DIRECTORY));
    }

    @Test
    public void addAndCommitTest() throws IOException, IndexIOException, GitDoesNotExistException,
            LogIOException, ObjectIOException, ObjectStoreException, HeadIOException, BranchIOException {
        Path addTestFile = buildPath(rep.GIT_DIRECTORY.getParent(), "addTest");
        Files.write(addTestFile, "addTest".getBytes());
        List <Path> pathList = new ArrayList<>();
        pathList.add(addTestFile);
        List <String> pathListAsStringList = new ArrayList<>();
        pathListAsStringList.add(addTestFile.toString());

        rep.add(pathList);

        List<String> index = ind.getCurrentIndexState();

        List <String> onlyFiles = new ArrayList<>();
        for (int i = 0; i < index.size(); i += 2)
            onlyFiles.add(index.get(i));
        assertEquals(onlyFiles, pathListAsStringList);
        rep.commit("this is my add and commit test");

        Path pathToBranch = buildPath(rep.REFS_DIRECTORY, "master");
        Path pathToCommit = buildPath(rep.OBJECTS_DIRECTORY, Files.lines(pathToBranch).findFirst().get());
        assertTrue(Files.exists(pathToCommit));
        Path pathToTree = buildPath(rep.OBJECTS_DIRECTORY, Files.lines(pathToCommit).findFirst().get());
        List <String> treeContent;
        treeContent = Files.readAllLines(pathToTree);
        assertEquals(addTestFile.toString(), treeContent.get(0));
        Path pathToBlob = buildPath(rep.OBJECTS_DIRECTORY, treeContent.get(1));
        String blobContent = Files.lines(pathToBlob).findFirst().get();
        assertEquals("addTest", blobContent);
    }

    @Test
    public void createAndDeleteBranchTest() throws HeadIOException, BranchAlreadyExistsException,
            GitDoesNotExistException, ObjectStoreException, BranchIOException, BranchDeletionException,
            LogIOException {

        rep.branch("another");
        Path pathToBranchRef = buildPath(rep.REFS_DIRECTORY, "another");
        assertTrue(Files.exists(pathToBranchRef));
        rep.branchWithDOption("another");
        assertTrue(Files.notExists(pathToBranchRef));
    }

    @Test
    public void checkoutBranchTest() throws IOException, HeadIOException, BranchAlreadyExistsException,
            GitDoesNotExistException, ObjectStoreException, BranchIOException, IndexIOException,
            ObjectIOException, LogIOException {
        String newBranchName = "another";
        rep.branch(newBranchName);
        rep.checkout(newBranchName);
        String headContent = Files.lines(rep.HEAD).findFirst().get();
        assertEquals(newBranchName, headContent);
        Path pathToWrite = buildPath(rep.GIT_DIRECTORY.getParent(), "newFile");
        Files.write(pathToWrite, "abacaba".getBytes());
        List <Path> toWrite = new ArrayList<>();
        toWrite.add(pathToWrite);
        assertTrue(Files.exists(pathToWrite));
        rep.add(toWrite);
        rep.commit("newFile to another branch");
        rep.checkout("master");
        assertTrue(Files.notExists(pathToWrite));
        rep.checkout(newBranchName);
        assertTrue(Files.exists(pathToWrite));
    }

    @Test
    public void checkoutCommitHash() throws BranchIOException, HeadIOException, GitDoesNotExistException,
            BranchAlreadyExistsException, ObjectStoreException, LogIOException {
        String commitHash = head.getCurrentCommit();
        rep.checkout(commitHash);
        assertTrue(Files.exists(buildPath(rep.REFS_DIRECTORY, commitHash)));
        assertEquals(commitHash, head.getCurrentBranch());
    }

    @Test
    public void testMerge() throws IOException, HeadIOException, BranchAlreadyExistsException,
            GitDoesNotExistException, ObjectStoreException, BranchIOException, IndexIOException,
            ObjectIOException, LogIOException, MergeIOException {
        String newBranchName = "toMerge";
        rep.branch(newBranchName);
        rep.checkout(newBranchName);
        Path fileToMerge = buildPath(rep.GIT_DIRECTORY.getParent(), "merge");
        Files.write(fileToMerge, "merge".getBytes());
        List <Path> listOfFilesToMerge = new ArrayList<>();
        listOfFilesToMerge.add(fileToMerge);
        rep.add(listOfFilesToMerge);
        rep.commit("this file will be merged into master");
        rep.checkout("master");
        assertTrue(Files.notExists(fileToMerge));
        rep.merge(newBranchName);
        assertTrue(Files.exists(fileToMerge));
    }


    @Test
    public void testLog() throws HeadIOException, GitDoesNotExistException, LogIOException {
        String initMessage = new String(rep.log());
        String[] tokens = initMessage.split(" |\\n");
        String user = tokens[1];
        assertEquals(System.getProperty("user.name"), user);
    }

    @Test
    public void commitWithoutChanges() throws GitDoesNotExistException, IndexIOException, HeadIOException,
            ObjectStoreException, ObjectIOException, LogIOException, BranchIOException {
        rep.commit("no changes");
    }

    @Test
    public void commitWithTwoFiles() throws IOException, IndexIOException, GitDoesNotExistException,
            LogIOException, ObjectIOException, ObjectStoreException, HeadIOException, BranchIOException {
        Path addOne = buildPath(rep.GIT_DIRECTORY.getParent(), "one");
        Files.write(addOne, "one".getBytes());
        List <Path> pathList = new ArrayList<>();
        pathList.add(addOne);
        Path addTwo = buildPath(rep.GIT_DIRECTORY.getParent(), "two");
        Files.write(addTwo, "two".getBytes());
        pathList.add(addTwo);
        rep.add(pathList);
        rep.commit("smth");
        rep.commit("another");
        rep.commit("smth");
    }

    @Test
    public void storeTest() throws IOException, ObjectIOException, ObjectStoreException {
        Path bl = buildPath(rep.GIT_DIRECTORY.getParent(), "blob");
        Files.write(bl, "blob".getBytes());
        Blob blob = Blob.getBlob(bl, rep);
        blob.storeObject();
        assertTrue(Files.exists(buildPath(rep.OBJECTS_DIRECTORY, blob.getHash())));
        assertEquals("blob", Files.lines(buildPath(rep.OBJECTS_DIRECTORY, blob.getHash())).findFirst().get());
    }

    @Test
    public void headStateTest() throws HeadIOException {
        head.updateHead("another");
        assertEquals("another", head.getCurrentBranch());
    }

    @Test
    public void indexStateTest() throws IndexIOException, IOException, ObjectIOException,
            ObjectStoreException {

        ind.cleanIndex();
        List <Path> list = new ArrayList<>();
        Path toIndex = buildPath(rep.GIT_DIRECTORY.getParent(), "ind");
        Files.createFile(toIndex);
        list.add(toIndex);
        ind.updateIndex(list);
        assertEquals(toIndex.toString(), Files.lines(rep.INDEX).findFirst().get());
    }

    @Test(expected = GitDoesNotExistException.class)
    public void testGitDoesNotExist() throws HeadIOException, BranchAlreadyExistsException,
            GitDoesNotExistException, BranchIOException, ObjectStoreException, LogIOException {
        deleteDirectory(new File(rep.GIT_DIRECTORY.toString()));
        rep.branch("branch");
    }

    @Test(expected = BranchAlreadyExistsException.class)
    public void testBranchAlreadyExist() throws HeadIOException, BranchAlreadyExistsException,
            GitDoesNotExistException, BranchIOException, ObjectStoreException, LogIOException {
        rep.branch("master");
    }

    @Test
    public void cleanTest() throws IndexIOException, IOException {
        Files.createFile(buildPath(rep.GIT_DIRECTORY.getParent(), "hello"));
        rep.clean();
        assertTrue(Files.notExists(buildPath(rep.GIT_DIRECTORY.getParent(), "hello")));
    }

    @Test
    public void statusTest() throws IOException, IndexIOException, ObjectIOException,
            GitDoesNotExistException, ObjectStoreException {

        List <Path> file = new ArrayList<>();
        file.add(buildPath(rep.GIT_DIRECTORY.getParent(), "hello"));

        Files.createFile(file.get(0));
        assertTrue(rep.status().startsWith("untracked"));
;
        rep.add(file);
        assertTrue(rep.status().startsWith("staged"));

        Files.write(file.get(0), "hello".getBytes());
        assertTrue(rep.status().startsWith("modified"));

        Files.delete(file.get(0));
        assertTrue(rep.status().startsWith("deleted"));
    }

    @Test
    public void rmTest() throws IOException, GitDoesNotExistException, IndexIOException,
            ObjectStoreException, ObjectIOException {
        List <Path> path = new ArrayList<>();
        List <String> pathAsString = new ArrayList<>();
        path.add(buildPath(rep.GIT_DIRECTORY.getParent(), "here"));
        pathAsString.add(path.get(0).toString());
        Files.createFile(path.get(0));
        rep.add(path);
        assertEquals(pathAsString.get(0), ind.getCurrentIndexState().get(0));
        rep.rm(pathAsString);
        assertEquals("", String.join("", ind.getCurrentIndexState()));
        assertTrue(Files.notExists(path.get(0)));
    }

    @Test
    public void resetTest() throws IOException, GitDoesNotExistException, IndexIOException,
            ObjectStoreException, ObjectIOException {
        List <Path> path = new ArrayList<>();
        path.add(buildPath(rep.GIT_DIRECTORY.getParent(), "reset"));
        Files.createFile(path.get(0));
        rep.add(path);
        assertEquals(path.get(0).toString(), ind.getCurrentIndexState().get(0));
        rep.reset(path.get(0));
        assertEquals("", String.join("", ind.getCurrentIndexState()));
    }
}