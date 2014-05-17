package ollie.test.model;


import ollie.Model;
import ollie.annotation.Column;
import ollie.annotation.Polymorphic;
import ollie.annotation.PolymorphicType;
import ollie.annotation.Table;

@Table("attachments")
@Polymorphic
public abstract class Attachment extends Model {

    @Column("Url")
    public String Url;

    @Column("Type")
    public String Type;

    public abstract String getTitle();

    @Table("attachments")

    @PolymorphicType("image_attachment")
    public static class ImageAttachment extends Attachment {
        public static final String TITLE = "Image";
        public String Type = "image_attachment";

        @Override
        public String getTitle() {
            return TITLE;
        }
    }

    @Table("attachments")
    @PolymorphicType("video_attachment")
    public static class VideoAttachment extends Attachment {
        public static final String TITLE = "Video";
        public String Type = "video_attachment";

        @Override
        public String getTitle() {
            return TITLE;
        }
    }
}
