package com.example.ollie.model;

import ollie.Model;
import ollie.annotation.Column;
import ollie.annotation.ForeignKey;
import ollie.annotation.Table;

@Table("noteTags")
public class NoteTag extends Model {
	public static final String Note = "note";
	public static final String Tag = "tag";

	@Column(Note)
	@ForeignKey
	public Note note;
	@Column(Tag)
	@ForeignKey
	public Tag tag;
}