package ollie.test;

import android.content.ContentProvider;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowContentResolver;

import java.io.File;
import java.util.List;

import ollie.Ollie;
import ollie.query.Select;
import ollie.test.content.OllieSampleProvider;
import ollie.test.model.Attachment;
import ollie.test.shadows.PersistentShadowSQLiteOpenHelper;

import static org.fest.assertions.api.Assertions.assertThat;

@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE, shadows = PersistentShadowSQLiteOpenHelper.class)
public class PolymorphicTest {

    private long mImageId;
    private long mVideoId;

    @BeforeClass
    public static void setup() {
        new File("path").delete();
    }

    @Before
    public void initialize() {
        ContentProvider contentProvider = new OllieSampleProvider();
        contentProvider.onCreate();
        ShadowContentResolver.registerProvider("com.example.ollie", contentProvider);

        Ollie.init(Robolectric.application, "OllieSample.db", 1);
        populateDatabase();
    }
    public void populateDatabase() {
        mImageId = new Attachment.ImageAttachment().save();
        mVideoId = new Attachment.VideoAttachment().save();
    }

    @After
    public void clear(){
        Attachment.find(Attachment.class, mImageId).delete();
        Attachment.find(Attachment.class, mVideoId).delete();
    }

    @Test
    public void testPolymorphicProcessing() {
        assertThat(Ollie.getModelAdapter(Attachment.class).isPolymorphic()).isTrue();
        assertThat(Ollie.getModelAdapter(Attachment.class).getTypeColumn()).isEqualTo("type");
        assertThat(Ollie.getModelAdapter(Attachment.class).getTypeName()).isEmpty();

        assertThat(Ollie.getModelAdapter(Attachment.ImageAttachment.class).isPolymorphic()).isTrue();
        assertThat(Ollie.getModelAdapter(Attachment.ImageAttachment.class).getTypeColumn()).isEqualTo("type");
        assertThat(Ollie.getModelAdapter(Attachment.ImageAttachment.class).getTypeName()).isEqualTo("image_attachment");

        assertThat(Ollie.getModelAdapter(Attachment.VideoAttachment.class).isPolymorphic()).isTrue();
        assertThat(Ollie.getModelAdapter(Attachment.VideoAttachment.class).getTypeColumn()).isEqualTo("type");
        assertThat(Ollie.getModelAdapter(Attachment.VideoAttachment.class).getTypeName()).isEqualTo("video_attachment");
    }

    @Test
    public void testLoadEntity() {
        Attachment attachment;

        attachment = Attachment.find(Attachment.class, mImageId);
        assertThat(attachment).isNotNull();
        assertThat(attachment.id).isEqualTo(mImageId);
        assertThat(attachment.toString()).isEqualTo("Image");

        attachment = Attachment.find(Attachment.class, mVideoId);
        assertThat(attachment).isNotNull();
        assertThat(attachment.id).isEqualTo(mVideoId);
        assertThat(attachment.toString()).isEqualTo("Video");

        attachment = Attachment.find(Attachment.ImageAttachment.class, mVideoId);
        assertThat(attachment).isNull();
    }

    @Test
    public void testLoadMany() {
        List<Attachment> attachments = new Select().from(Attachment.class).fetch();
        assertThat(attachments).hasSize(2);
        assertThat(attachments.get(0).toString()).isEqualTo("Image");
        assertThat(attachments.get(1).toString()).isEqualTo("Video");

    }
}