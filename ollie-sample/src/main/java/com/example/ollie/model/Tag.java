package com.example.ollie.model;

import ollie.Model;
import ollie.annotation.Column;
import ollie.annotation.NotNull;
import ollie.annotation.Table;

@Table("Tags")
public class Tag extends Model {
	@Column("Name")
	@NotNull
	public String name;
}