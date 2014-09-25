package ollie.internal;

import ollie.internal.codegen.OllieProcessor;

import javax.annotation.processing.Processor;
import java.util.Arrays;

final class ProcessorTestUtilities {
	static Iterable<? extends Processor> ollieProcessors() {
		return Arrays.asList(
				new OllieProcessor()
		);
	}
}