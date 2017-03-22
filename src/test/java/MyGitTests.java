import org.junit.Before;
import org.junit.Test;
import ru.spbau.sofronova.entities.Commit;
import ru.spbau.sofronova.exceptions.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static ru.spbau.sofronova.Main.*;
import static ru.spbau.sofronova.logic.MyGitBranch.*;
import static ru.spbau.sofronova.logic.MyGitCommands.*;
import static ru.spbau.sofronova.logic.MyGitHead.*;
import static ru.spbau.sofronova.logic.MyGitIndex.*;
import static ru.spbau.sofronova.logic.MyGitUtils.*;

public class MyGitTests {

    public static final String CURRENT_DIRECTORY = Paths.get(System.getProperty("user.dir")).toString();

    @Before
    public void preparation() {
        deleteDirectory(new File(GIT_DIRECTORY.toString()));
        main(makeInitArgs());
    }

    @Test
    public void initTest()  {
        assertTrue(Files.exists(GIT_DIRECTORY));
        assertTrue(Files.exists(INDEX));
        assertTrue(Files.exists(REFS_DIRECTORY));
        assertTrue(Files.exists(OBJECTS_DIRECTORY));
        assertTrue(Files.exists(HEAD));
        assertTrue(Files.exists(LOGS_DIRECTORY));
    }

    @Test
    public void addAndCommitTest() throws IOException, IndexIOException {
        Path addTestFile = buildPath(CURRENT_DIRECTORY, "addTest");
        Files.write(addTestFile, "addTest".getBytes());
        List <String> pathListAsStringList = new ArrayList<>();
        pathListAsStringList.add(addTestFile.toString());

        main(makeAddArgs(addTestFile.toString()));

        List<String> index = getCurrentIndexState();
        assertEquals(index, pathListAsStringList);

        main(makeCommitArgs("this is my add and commit test"));

        Path pathToBranch = buildPath(REFS_DIRECTORY, "master");
        Path pathToCommit = buildPath(OBJECTS_DIRECTORY, Files.lines(pathToBranch).findFirst().get());
        assertTrue(Files.exists(pathToCommit));
        Path pathToTree = buildPath(OBJECTS_DIRECTORY, Files.lines(pathToCommit).findFirst().get());
        List <String> treeContent = new ArrayList<>();
        treeContent = Files.readAllLines(pathToTree);
        assertEquals(addTestFile.toString(), treeContent.get(0));
        Path pathToBlob = buildPath(OBJECTS_DIRECTORY, treeContent.get(1));
        String blobContent = Files.lines(pathToBlob).findFirst().get();
        assertEquals("addTest", blobContent);
    }

    @Test
    public void createAndDeleteBranchTest() {

        main(makeBranchArgs("another"));
        Path pathToBranchRef = buildPath(REFS_DIRECTORY, "another");
        assertTrue(Files.exists(pathToBranchRef));
        main(makeBranchDeletionArgs("another"));
        assertTrue(Files.notExists(pathToBranchRef));
    }

    @Test
    public void checkoutBranchTest() throws IOException {
        String newBranchName = "another";
        main(makeBranchArgs(newBranchName));
        main(makeCheckoutArgs(newBranchName));
        String headContent = Files.lines(HEAD).findFirst().get();
        assertEquals(newBranchName, headContent);
        Path pathToWrite = buildPath(CURRENT_DIRECTORY, "newFile");
        Files.write(pathToWrite, "abacaba".getBytes());
        assertTrue(Files.exists(pathToWrite));
        main(makeAddArgs(pathToWrite.toString()));
        main(makeCommitArgs("newFile to another branch"));
        main(makeCheckoutArgs("master"));
        assertTrue(Files.notExists(pathToWrite));
        main(makeCheckoutArgs(newBranchName));
        assertTrue(Files.exists(pathToWrite));
    }

    @Test
    public void checkoutCommitHash() throws BranchIOException, HeadIOException, GitDoesNotExistException {
        String commitHash = getCurrentCommit();
        main(makeCheckoutArgs(commitHash));
        assertTrue(Files.exists(buildPath(REFS_DIRECTORY, commitHash)));
        assertEquals(commitHash, getCurrentBranch());
    }

    @Test
    public void testMerge() throws IOException {
        String newBranchName = "toMerge";
        main(makeBranchArgs(newBranchName));
        main(makeCheckoutArgs(newBranchName));
        Path fileToMerge = buildPath(CURRENT_DIRECTORY, "merge");
        Files.write(fileToMerge, "merge".getBytes());
        List <Path> listOfFilesToMerge = new ArrayList<>();
        listOfFilesToMerge.add(fileToMerge);
        main(makeAddArgs(fileToMerge.toString()));
        main(makeCommitArgs("this file will be merged into master"));
        main(makeCheckoutArgs("master"));
        assertTrue(Files.notExists(fileToMerge));
        main(makeMergeArgs(newBranchName));
        assertTrue(Files.exists(fileToMerge));
    }



    @Test
    public void testLog() throws HeadIOException, GitDoesNotExistException, LogIOException {
        String initMessage = new String(log());
        String[] tokens = initMessage.split(" ");
        String user = tokens[0];
        assertEquals(System.getProperty("user.name"), user);
    }

    @Test(expected = GitDoesNotExistException.class)
    public void testGitDoesNotExist() throws HeadIOException, BranchAlreadyExistsException,
            GitDoesNotExistException, BranchIOException {
        deleteDirectory(new File(GIT_DIRECTORY.toString()));
        branch("branch");
    }

    @Test(expected = BranchAlreadyExistsException.class)
    public void testBranchAlreadyExist() throws HeadIOException, BranchAlreadyExistsException,
            GitDoesNotExistException, BranchIOException {
        branch("master");
    }


    private String[] makeInitArgs() {
        return new String[]{"init"};
    }

    private String[] makeBranchArgs(String branch) {
        return new String[]{"branch", branch};
    }

    private String[] makeBranchDeletionArgs(String branch) {
        return new String[]{"branch", "-d", branch};
    }

    private String[] makeAddArgs(String file) {
        return new String[]{"add", file};
    }

    private String[] makeCommitArgs(String message) {
        return new String[]{"commit", message};
    }

    private String[] makeCheckoutArgs(String branchOrHash) {
        return new String[]{"checkout", branchOrHash};
    }

    private String[] makeMergeArgs(String branch) {
        return new String[]{"merge", branch};
    }

}
