package com.example.ollie.model;

import ollie.Model;
import ollie.annotation.Column;
import ollie.annotation.NotNull;
import ollie.annotation.Table;

import java.util.Date;

@Table("notes")
public class Note extends Model {
	public static final String Title = "title";
	public static final String Body = "body";
	public static final String Date = "date";

	@Column(Title)
	public String title;
	@Column(Body)
	@NotNull
	public String body;
	@Column(Date)
	public Date date;
}