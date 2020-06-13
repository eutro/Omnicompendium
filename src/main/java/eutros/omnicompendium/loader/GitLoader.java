package eutros.omnicompendium.loader;

import eutros.omnicompendium.config.OmCConfig;
import net.minecraft.client.Minecraft;
import org.apache.commons.io.FileUtils;
import org.eclipse.jgit.api.CloneCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.errors.RepositoryNotFoundException;
import org.eclipse.jgit.lib.BatchingProgressMonitor;
import org.eclipse.jgit.lib.Repository;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.util.List;

import static eutros.omnicompendium.Omnicompendium.LOGGER;

public class GitLoader {

    public static final File DIR = new File(Minecraft.getMinecraft().gameDir, "Omnicompendium");
    public static final LoggingProgressMonitor PROGRESS_MONITOR = new LoggingProgressMonitor();
    public static File configFile = new File(DIR, "_config.txt");
    public static String branch = OmCConfig.branch;

    public static void syncRepo() {
        LOGGER.info("Loading Omnicompendium.");
        try {
            try {
                List<String> lines;
                lines = Files.readAllLines(configFile.toPath());
                if(lines.isEmpty() || !lines.get(0).equals(OmCConfig.url)) {
                    gitClone();
                    return;
                }

                Git git = Git.open(DIR);
                Repository repo = git.getRepository();
                if(!repo.getBranch().equals(OmCConfig.branch)) {
                    LOGGER.info(String.format("Checking out branch: %s.", OmCConfig.branch));
                    git.checkout()
                            .setName(OmCConfig.branch)
                            .setForced(true)
                            .setProgressMonitor(PROGRESS_MONITOR)
                            .call();
                }
                LOGGER.info("Pulling from origin.");
                git.pull()
                        .setRemoteBranchName(OmCConfig.branch)
                        .setRebase(true)
                        .setProgressMonitor(PROGRESS_MONITOR)
                        .call();
                branch = repo.getBranch();
                git.close();
                LOGGER.info("Finished pull.");
            } catch(RepositoryNotFoundException | NoSuchFileException e) {
                gitClone();
            }
        } catch(IOException | GitAPIException e) {
            LOGGER.fatal("Omnicompendium failed to load.", e);
        }
    }

    public static void gitClone() throws GitAPIException, IOException {
        final File[] files = DIR.listFiles();
        if(files != null) {
            LOGGER.info(String.format("Deleting: %s files.", files.length));
            IOException exception = null;
            for(final File file : files) {
                try {
                    FileUtils.forceDelete(file);
                } catch(final IOException ioe) {
                    exception = ioe;
                }
            }

            if(null != exception) {
                throw exception;
            }
        }
        LOGGER.info("Cloning repository.");
        CloneCommand clone = Git.cloneRepository()
                .setDirectory(DIR)
                .setBranch(OmCConfig.branch)
                .setURI(OmCConfig.url)
                // Why is shallow cloning still not supported? https://bugs.eclipse.org/bugs/show_bug.cgi?id=475615
                .setProgressMonitor(PROGRESS_MONITOR);
        Git git = clone.call();
        FileWriter writer = new FileWriter(configFile);
        writer.write(OmCConfig.url);
        writer.close();
        branch = git.getRepository().getBranch();
        git.close();
        LOGGER.info("Finished cloning.");
    }

    private static class LoggingProgressMonitor extends BatchingProgressMonitor {

        @Override
        protected void onUpdate(String taskName, int workCurr) {
            StringBuilder s = new StringBuilder();
            format(s, taskName, workCurr);
            send(s);
        }

        @Override
        protected void onEndTask(String taskName, int workCurr) {
            StringBuilder s = new StringBuilder();
            format(s, taskName, workCurr);
            s.append("\n"); //$NON-NLS-1$
            send(s);
        }

        private void format(StringBuilder s, String taskName, int workCurr) {
            s.append("\r"); //$NON-NLS-1$
            s.append(taskName);
            s.append(": "); //$NON-NLS-1$
            while(s.length() < 25)
                s.append(' ');
            s.append(workCurr);
        }

        @Override
        protected void onUpdate(String taskName, int cmp, int totalWork, int pct) {
            StringBuilder s = new StringBuilder();
            format(s, taskName, cmp, totalWork, pct);
            send(s);
        }

        @Override
        protected void onEndTask(String taskName, int cmp, int totalWork, int pct) {
            StringBuilder s = new StringBuilder();
            format(s, taskName, cmp, totalWork, pct);
            send(s);
        }

        private void format(StringBuilder s, String taskName, int cmp, int totalWork, int pct) {
            s.append(taskName);
            s.append(": "); //$NON-NLS-1$
            while(s.length() < 25)
                s.append(' ');

            String endStr = String.valueOf(totalWork);
            StringBuilder curStr = new StringBuilder(String.valueOf(cmp));
            while(curStr.length() < endStr.length())
                curStr.insert(0, " "); //$NON-NLS-1$
            if(pct < 100)
                s.append(' ');
            if(pct < 10)
                s.append(' ');
            s.append(pct);
            s.append("% ("); //$NON-NLS-1$
            s.append(curStr);
            s.append("/"); //$NON-NLS-1$
            s.append(endStr);
            s.append(")"); //$NON-NLS-1$
        }

        private void send(StringBuilder s) {
            LOGGER.info(s);
        }

    }

}
