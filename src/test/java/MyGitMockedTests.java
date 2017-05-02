import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import ru.spbau.sofronova.exceptions.*;
import ru.spbau.sofronova.logic.*;

import java.io.IOException;
import java.util.ArrayList;

import static org.mockito.Mockito.*;

public class MyGitMockedTests {

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
        rep = spy(new MyGit(folder.newFolder("oops").getAbsolutePath()));
        logs = spy(rep.getLogsManager());
        brnch = spy(rep.getBranchManager());
        head = spy(rep.getHeadManager());
        ind = spy(rep.getIndexManager());
        when(rep.getBranchManager()).thenReturn(brnch);
        when(rep.getHeadManager()).thenReturn(head);
        when(rep.getIndexManager()).thenReturn(ind);
        when(rep.getLogsManager()).thenReturn(logs);
    }

    @Test (expected = GitAlreadyInitializedException.class)
    public void initTest() throws GitDoesNotExistException, BranchIOException, InitIOException,
            GitAlreadyInitializedException, LogIOException, IndexIOException, BranchAlreadyExistsException,
            ObjectIOException, ObjectStoreException, HeadIOException {
        doThrow(new GitAlreadyInitializedException("git already initialized\n")).when(rep).init();
        rep.init();
    }

    @Test
    public void addTest() throws ObjectStoreException, IndexIOException, ObjectIOException,
            GitDoesNotExistException, HeadIOException, BranchAlreadyExistsException,
            InitIOException, LogIOException, BranchIOException, GitAlreadyInitializedException {
        rep.init();
        doNothing().when(ind).updateIndex(new ArrayList<>());
        rep.add(new ArrayList<>());
        verify(ind, times(1)).updateIndex(new ArrayList<>());
    }

    @Test
    public void mergeTest() throws HeadIOException, GitDoesNotExistException, IndexIOException,
            ObjectStoreException, MergeIOException, ObjectIOException, LogIOException, BranchIOException,
            BranchAlreadyExistsException, InitIOException, GitAlreadyInitializedException {
        rep.init();
        doReturn("branch").when(head).getCurrentBranch();
        rep.merge("master");
        verify(rep, times(1)).commit("merge master into branch");
    }

    @Test (expected = GitDoesNotExistException.class)
    public void notInitializedTest() throws HeadIOException, GitDoesNotExistException, LogIOException,
            ObjectStoreException, IndexIOException, ObjectIOException,
            BranchAlreadyExistsException, BranchIOException, MergeIOException,
            GitAlreadyInitializedException, InitIOException {
        doNothing().when(rep).init();
        rep.init();
        rep.add(new ArrayList<>());
    }
}
