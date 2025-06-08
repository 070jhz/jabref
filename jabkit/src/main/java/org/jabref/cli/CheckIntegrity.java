package org.jabref.cli;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.jabref.logic.importer.ParserResult;
import org.jabref.logic.l10n.Localization;
import org.jabref.model.database.BibDatabase;

import static picocli.CommandLine.Command;
import static picocli.CommandLine.Mixin;
import static picocli.CommandLine.Option;
import static picocli.CommandLine.Parameters;
import static picocli.CommandLine.ParentCommand;

@Command(name = "check-integrity", description = "Check integrity of the database.")
class CheckIntegrity implements Runnable {

    @ParentCommand
    ArgumentProcessor argumentProcessor;

    @Mixin
    private ArgumentProcessor.SharedOptions sharedOptions = new ArgumentProcessor.SharedOptions();

    @Parameters(index = "0", description = "BibTeX file to check", arity = "0..1")
    private File inputFile;

    @Option(names = {"--input"}, description = "Input BibTeX file")
    private File inputOption;

    @Option(names = {"--output-format"}, description = "Output format: txt or csv")
    private String outputFormat = "txt"; // FixMe: Default value?

    @Override
    public void run() {
        String fileToCheck = inputFile != null ? inputFile.getPath() : inputOption.getPath();
        if (fileToCheck == null) {
            System.err.println(Localization.lang("No input file specified."));
            return;
        }

        if (!sharedOptions.porcelain) {
            System.out.println(Localization.lang("Checking integrity of '%0'", fileToCheck));
        }

        Optional<ParserResult> parserResult = ArgumentProcessor.importFile(
                fileToCheck,
                "bibtex",
                argumentProcessor.cliPreferences,
                sharedOptions.porcelain);
        if (parserResult.isEmpty()) {
            System.err.println(Localization.lang("Unable to open file '%0'.", fileToCheck));
            return;
        }

        if (parserResult.get().isInvalid()) {
            System.err.println(Localization.lang("Input file '%0' is invalid and could not be parsed.", fileToCheck));
            return;
        }

        BibDatabase database = parserResult.get().getDatabase();
        Map<String, List<String>> issues = performIntegrityChecks(database);

        if ("txt".equalsIgnoreCase(outputFormat)) {
            if (issues.isEmpty()) {
                if (!sharedOptions.porcelain) {
                    System.out.println(Localization.lang("No issues found."));
                }
            } else {
                for (Map.Entry<String, List<String>> category : issues.entrySet()) {
                    System.out.println(category.getKey() + ":");
                    for (String issue : category.getValue()) {
                        System.out.println("- " + issue);
                    }
                }
                if (!sharedOptions.porcelain) {
                    int total = issues.values().stream().mapToInt(List::size).sum();
                    System.out.println(Localization.lang("Found %0 issues.", String.valueOf(total)));
                }
            }
        } else if ("csv".equalsIgnoreCase(outputFormat)) {
            System.out.println("Category,Message");
            for (Map.Entry<String, List<String>> category : issues.entrySet()) {
                for (String issue : category.getValue()) {
                    System.out.println("\"" + category.getKey().replace("\"", "\"\"") + "\",\"" +
                            issue.replace("\"", "\"\"") + "\"");
                }
            }
        } else {
            System.err.println(Localization.lang("Invalid output format: %0", outputFormat));
        }
    }

    private Map<String, List<String>> performIntegrityChecks(BibDatabase database) {
        return new HashMap<>();
    }
}
