package eutros.omnicompendium.loader;

import eutros.omnicompendium.config.OmCConfig;
import eutros.omnicompendium.Omnicompendium;
import net.minecraft.client.Minecraft;
import org.apache.commons.io.FileUtils;
import org.eclipse.jgit.api.CloneCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.errors.RepositoryNotFoundException;
import org.eclipse.jgit.lib.Repository;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.util.List;

public class GitLoader {

    public static final File DIR = new File(Minecraft.getMinecraft().gameDir, "Omnicompendium");
    public static File configFile = new File(DIR, "_config.txt");
    public static String branch = OmCConfig.branch;

    public static void syncRepo() {
        Omnicompendium.LOGGER.info("Loading Omnicompendium.");
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
                    Omnicompendium.LOGGER.info(String.format("Checking out branch: %s.", OmCConfig.branch));
                    git.checkout()
                            .setName(OmCConfig.branch)
                            .setForced(true)
                            .call();
                }
                Omnicompendium.LOGGER.info("Pulling from origin.");
                git.pull()
                        .setRemoteBranchName(OmCConfig.branch)
                        .setRebase(true)
                        .call();
                branch = repo.getBranch();
                git.close();
            } catch(RepositoryNotFoundException | NoSuchFileException e) {
                gitClone();
            }
        } catch(IOException | GitAPIException e) {
            Omnicompendium.LOGGER.fatal("Omnicompendium failed to load.", e);
        }
    }

    public static void gitClone() throws GitAPIException, IOException {
        Omnicompendium.LOGGER.info("Cloning repository.");
        FileUtils.cleanDirectory(DIR);
        CloneCommand clone = Git.cloneRepository();
        clone.setDirectory(DIR);
        clone.setBranch(OmCConfig.branch);
        clone.setURI(OmCConfig.url);
        Git git = clone.call();
        FileWriter writer = new FileWriter(configFile);
        writer.write(OmCConfig.url);
        writer.close();
        branch = git.getRepository().getBranch();
        git.close();
    }

}
