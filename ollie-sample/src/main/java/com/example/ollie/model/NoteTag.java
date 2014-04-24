package com.example.ollie.model;

import ollie.Model;
import ollie.annotation.Column;
import ollie.annotation.Table;

@Table("NoteTags")
public class NoteTag extends Model {
	@Column("NoteId")
	public Note note;
	@Column("TagId")
	public Tag tag;
}