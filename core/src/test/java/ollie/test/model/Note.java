package ollie.test.model;

import ollie.Model;
import ollie.annotation.Column;
import ollie.annotation.NotNull;
import ollie.annotation.Table;

import java.util.Date;

@Table("notes")
public class Note extends Model {
	public static final String TITLE = "title";
	public static final String BODY = "body";
	public static final String DATE = "date";

	@Column(TITLE)
	public String title;
	@Column(BODY)
	@NotNull
	public String body;
	@Column(DATE)
	public Date date;
}