package ollie.internal.codegen.writer;

import javax.lang.model.element.Element;
import java.io.IOException;
import java.io.Writer;

public interface SourceWriter<T extends Element> {
	String createSourceName(T element);

	void writeSource(Writer writer, T element) throws IOException;
}
