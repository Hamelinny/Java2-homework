package ru.spbau.sofronova.logger;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configurator;
import org.apache.logging.log4j.core.config.builder.api.*;
import org.apache.logging.log4j.core.config.builder.impl.BuiltConfiguration;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.nio.file.Paths;

import static ru.spbau.sofronova.logic.MyGitUtils.buildPath;

/**
 * Class which is need for creating a Logger for MyGit.
 */
public class MyGitLogBuilder {

    /**
     * Method to create a Logger. Logs will be placed in specified folder.
     * @param path path to folder where logs will be
     * @return Logger object
     */
    public static Logger getLogger(@NotNull Path path) {

        final ConfigurationBuilder<BuiltConfiguration> builder = ConfigurationBuilderFactory.newConfigurationBuilder();
        builder.setConfigurationName("MyGitLogger");
        builder.setStatusLevel(Level.OFF);
        final LayoutComponentBuilder layoutBuilder = builder
                .newLayout("PatternLayout")
                .addAttribute("pattern", "%d [%t] %-5level: %msg%n%throwable");
        final ComponentBuilder<?> rolloverStrategy = builder
                .newComponent("DefaultRolloverStrategy")
                .addAttribute("max", 3);
        final ComponentBuilder<?> triggeringPolicy = builder
                .newComponent("Policies")
                .addComponent(builder
                        .newComponent("SizeBasedTriggeringPolicy").addAttribute("size", "4MB"));
        final Path logs = buildPath(path.toString(), "myGitLogs");
        final AppenderComponentBuilder appenderBuilder = builder
                        .newAppender("file", "ROLLINGFILE")
                        .addAttribute("fileName", Paths.get(logs.toString(), "git0.log").toString())
                        .addAttribute("filePattern", Paths.get(logs.toString(), "git%i.log").toString())
                        .add(layoutBuilder)
                        .addComponent(triggeringPolicy)
                        .addComponent(rolloverStrategy);
        builder.add(appenderBuilder);
        final RootLoggerComponentBuilder logger = builder
                        .newRootLogger(Level.TRACE)
                        .add(builder.newAppenderRef("file"))
                        .addAttribute("additivity", false);
        builder.add(logger);
        return Configurator.initialize(builder.build()).getRootLogger();
    }

    private MyGitLogBuilder() {};
}
