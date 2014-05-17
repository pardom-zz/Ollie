package ollie.test.model;

import ollie.Model;
import ollie.annotation.Column;
import ollie.annotation.Polymorphic;
import ollie.annotation.PolymorphicType;
import ollie.annotation.Table;

@Table("attachments")
@Polymorphic
public abstract class Attachment extends Model {

    @Column("type")
    public String type;

    @Table("attachments")
    @PolymorphicType("image_attachment")
    public static class ImageAttachment extends Attachment {

        @Override
        public String toString() {
            return "Image";
        }
    }

    @Table("attachments")
    @PolymorphicType("video_attachment")
    public static class VideoAttachment extends Attachment {

        @Override
        public String toString() {
            return "Video";
        }
    }
}
