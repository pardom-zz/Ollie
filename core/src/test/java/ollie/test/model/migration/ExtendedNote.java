package ollie.test.model.migration;

import ollie.annotation.Column;
import ollie.annotation.Table;
import ollie.test.model.Note;

@Table("extended_notes")
public class ExtendedNote extends Note {
	@Column("extendedBody")
	public String extendedBody;
}
