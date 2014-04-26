package com.example.ollie.model;

import ollie.Model;
import ollie.annotation.Column;
import ollie.annotation.ForeignKey;
import ollie.annotation.Table;

@Table("NoteTags")
public class NoteTag extends Model {
	@Column("NoteId")
	@ForeignKey
	public Note note;
	@Column("TagId")
	@ForeignKey
	public Tag tag;
}