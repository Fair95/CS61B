package gitlet;

import java.io.File;
import java.io.IOException;

import static gitlet.Utils.join;

/**
 * Driver class for Gitlet, a subset of the Git version-control system.
 *
 * @author TODO
 */
public class Main {

    /**
     * Usage: java gitlet.Main ARGS, where ARGS contains
     * <COMMAND> <OPERAND1> <OPERAND2> ...
     */
    public static void main(String[] args) throws IOException {
        // if no argument provided
        if (args.length == 0) {
            Utils.exitWithError("Please enter a command.");
        }
        String firstArg = args[0];
        Repository repo;
        switch (firstArg) {
            case "init":
                // handle the `init` command
                Utils.validateNumArgs(args, 1);
                repo = new Repository();
                repo.setUp();
                break;
            case "add":
                // handle the `add [filename]` command
                Utils.validateNumArgs(args, 2);
                repo = Repository.getState();
                repo.add(args[1]);
                break;
            case "commit":
                // handle the `commit [message]` command
                Utils.validateNumArgs(args, 2);
                repo = Repository.getState();
                repo.commit(args[1]);
                break;
            case "rm":
                // handle the `rm [filename]` command
                Utils.validateNumArgs(args, 2);
                repo = Repository.getState();
                repo.rm(args[1]);
                break;
            case "log":
                // handle the `log` command
                Utils.validateNumArgs(args, 1);
                repo = Repository.getState();
                repo.log();
                break;
            case "global-log":
                // handle the `global-log` command
                Utils.validateNumArgs(args, 1);
                repo = Repository.getState();
                repo.global_log();
                break;
            case "find":
                // handle the `find [commit message]` command
                Utils.validateNumArgs(args, 2);
                repo = Repository.getState();
                repo.find(args[1]);
                break;
            case "status":
                // handle the `status` command
                Utils.validateNumArgs(args, 1);
                repo = Repository.getState();
                repo.status();
                break;
            case "checkout":
                // handle the `checkout` command
                repo = Repository.getState();
                if (args.length == 3 && args[1].equals("--")) {
                    // 1. checkout -- [filename]
                    repo.checkoutFile(args[2]);
                } else if (args.length == 4 && args[2].equals("--")) {
                    // 2. checkout [commit id] -- [filename]
                    repo.checkoutFileInCommit(args[1], args[3]);
                } else if (args.length == 2) {
                    // 3. checkout [branch name]
                    repo.checkoutBranch(args[1]);
                } else {
                    Utils.exitWithError("Incorrect operands.");
                }
                break;
            case "branch":
                // handle the `branch [branch name]` command
                Utils.validateNumArgs(args, 2);
                repo = Repository.getState();
                repo.makeBranch(args[1]);
                break;
            case "rm-branch":
                // handle the `rm-branch [branch name]` command
                Utils.validateNumArgs(args, 2);
                repo = Repository.getState();
                repo.deleteBranch(args[1]);
                break;
            case "reset":
                // handle the `reset [commit id]` command
                Utils.validateNumArgs(args, 2);
                repo = Repository.getState();
                repo.reset(args[1]);
                break;
            case "merge":
                // handle the `merge [branch name]` command
                Utils.validateNumArgs(args, 2);
                repo = Repository.getState();
                repo.merge(args[1]);
                break;
            default:
                Utils.exitWithError("No command with that name exists.");
        }
    }

}
