package ollie.test.model;

import ollie.Model;
import ollie.annotation.Column;
import ollie.annotation.NotNull;
import ollie.annotation.Table;

@Table("tags")
public class Tag extends Model {
	public static final String Name = "name";

	@Column(Name)
	@NotNull
	public String name;
}