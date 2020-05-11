package eutros.omnicompendium.loader;

import eutros.omnicompendium.Config;
import eutros.omnicompendium.Omnicompendium;
import net.minecraft.client.Minecraft;
import org.apache.commons.io.FileUtils;
import org.eclipse.jgit.api.CheckoutCommand;
import org.eclipse.jgit.api.CloneCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.errors.RepositoryNotFoundException;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.util.List;

public class GitLoader {

    public static final File DIR = new File(Minecraft.getMinecraft().gameDir, "Omnicompendium");
    public static File configFile = new File(DIR, "_config.txt");

    public static void syncRepo() {
        try {
            try {
                List<String> lines;
                lines = Files.readAllLines(configFile.toPath());
                if(lines.isEmpty() || !lines.get(0).equals(Config.url)) {
                    gitClone();
                    return;
                }

                Git git = Git.open(DIR);
                if(!git.getRepository().getBranch().equals(Config.branch)) {
                    CheckoutCommand checkout = git.checkout();
                    checkout.setName(Config.branch);
                    checkout.call();
                }
                git.pull().call();
                git.close();
            } catch(RepositoryNotFoundException | NoSuchFileException e) {
                gitClone();
            }
        } catch(IOException | GitAPIException e) {
            Omnicompendium.LOGGER.fatal("Omnicompendium failed to load.", e);
        }
    }

    public static void gitClone() throws GitAPIException, IOException {
        FileUtils.cleanDirectory(DIR);
        CloneCommand clone = Git.cloneRepository();
        clone.setDirectory(DIR);
        clone.setBranch(Config.branch);
        clone.setURI(Config.url);
        Git git = clone.call();
        FileWriter writer = new FileWriter(configFile);
        writer.write(Config.url);
        writer.close();
        git.close();
    }

}
