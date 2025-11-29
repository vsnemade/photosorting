package com.vishtech.command;


import org.springframework.stereotype.Component;
import picocli.CommandLine;

import java.util.concurrent.Callable;

@Component
@CommandLine.Command(name = "face", mixinStandardHelpOptions = true, subcommands = {
        FaceMatchCommand.class, NoFaceCommand.class })
public class FaceCommand implements Callable<Integer> {

    @Override
    public Integer call() throws Exception {
        new CommandLine(new FaceCommand()).usage(System.out);
        return CommandLine.ExitCode.OK;
    }
}
