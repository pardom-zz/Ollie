package ollie.test.model;

import ollie.Model;
import ollie.annotation.Column;
import ollie.annotation.Table;

@Table("notebooks")
public class Notebook extends Model {
    public static final String NAME = "name";
    public static final String NOTE = "note";

    @Column(NAME)
    public String name;

    @Column(NOTE)
    public Note note;
}
