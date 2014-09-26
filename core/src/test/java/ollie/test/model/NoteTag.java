package ollie.test.model;

import ollie.Model;
import ollie.annotation.Column;
import ollie.annotation.ForeignKey;
import ollie.annotation.Table;

import static ollie.annotation.ForeignKey.ReferentialAction.CASCADE;

@Table("noteTags")
public class NoteTag extends Model {
	public static final String Note = "note";
	public static final String Tag = "tag";

	@Column(Note)
	@ForeignKey(onDelete = CASCADE)
	public Note note;
	@Column(Tag)
	@ForeignKey(onDelete = CASCADE)
	public Tag tag;
}