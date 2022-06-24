package io.quarkus.cli.image;

import java.util.Map;
import java.util.Optional;

import io.quarkus.cli.build.BuildSystemRunner;
import io.quarkus.devtools.project.BuildTool;
import picocli.CommandLine;

@CommandLine.Command(name = "build", sortOptions = false, showDefaultValues = true, mixinStandardHelpOptions = false, header = "Build a container image.", description = "%n"
        + "This command will build a container image for the project.", subcommands = { Docker.class, Buildpack.class,
                Jib.class,
                Openshift.class }, footer = { "%n"
                        + "For example (using default values), it will create a container image using docker with REPOSITORY='${user.name}/<project.artifactId>' and TAG='<project.version>'."
                        + "%n" }, headerHeading = "%n", commandListHeading = "%nCommands:%n", synopsisHeading = "%nUsage: ", parameterListHeading = "%n", optionListHeading = "Options:%n")
public class Build extends BaseImageCommand {

    private static final Map<BuildTool, String> ACTION_MAPPING = Map.of(BuildTool.MAVEN, "compile quarkus:image-build",
            BuildTool.GRADLE, "imageBuild");

    @Override
    public void populateImageConfiguration(Map<String, String> properties) {
        super.populateImageConfiguration(properties);
    }

    @Override
    public Optional<String> getAction() {
        return Optional.ofNullable(ACTION_MAPPING.get(getRunner().getBuildTool()));
    }

    @Override
    public Integer call() throws Exception {
        try {
            populateImageConfiguration(propertiesOptions.properties);
            BuildSystemRunner runner = getRunner();

            String action = getAction().orElseThrow(
                    () -> new IllegalStateException("Unknown image push action for " + runner.getBuildTool().name()));
            BuildSystemRunner.BuildCommandArgs commandArgs = runner.prepareAction(action, buildOptions, runMode, params);
            return runner.run(commandArgs);
        } catch (Exception e) {
            return output.handleCommandException(e, "Unable to build image: " + e.getMessage());
        }
    }

    @Override
    public String toString() {
        return "Build {imageOptions='" + imageOptions + "'}";
    }
}
