package com.vishtech;

import com.vishtech.command.FaceCommand;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.ExitCodeGenerator;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;
import picocli.CommandLine;
import picocli.CommandLine.IFactory;

@Component
public class ApplicationRunner implements CommandLineRunner, ExitCodeGenerator {

	private final FaceCommand faceCommand;

	private final IFactory factory; // auto-configured to inject PicocliSpringFactory
	private final ConfigurableApplicationContext context;
	private int exitCode;

	public ApplicationRunner(FaceCommand faceCommand, IFactory factory, ConfigurableApplicationContext context) {
		this.faceCommand = faceCommand;
		this.factory = factory;
		this.context=context;
	}

	@Override
	public void run(String... args) throws Exception {
		exitCode = new CommandLine(faceCommand, factory).execute(args);
		SpringApplicationExit(exitCode);
	}

	private void SpringApplicationExit(int exitCode) {
		// Clean shutdown
		context.close();

		// Exit JVM
		System.exit(exitCode);
	}
	@Override
	public int getExitCode() {
		return exitCode;
	}
}
