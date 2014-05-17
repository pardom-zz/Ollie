package ollie.test;

import android.content.ContentProvider;
import ollie.Ollie;
import ollie.internal.AdapterHolder;
import ollie.test.content.OllieSampleProvider;
import ollie.test.model.Attachment;
import ollie.test.shadows.PersistentShadowSQLiteOpenHelper;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowContentResolver;

import java.io.File;

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

    @Test
    public void testPolymorphicProcessing(){
        assertThat(Ollie.getModelAdapter(Attachment.class).isPolymorphic()).isTrue();
        assertThat(Ollie.getModelAdapter(Attachment.class).getTypeColumn()).isEqualTo("Type");

        assertThat(Ollie.getModelAdapter(Attachment.ImageAttachment.class).isPolymorphic()).isTrue();
        assertThat(Ollie.getModelAdapter(Attachment.ImageAttachment.class).getTypeColumn()).isEqualTo("Type");

        assertThat(Ollie.getModelAdapter(Attachment.ImageAttachment.class).isPolymorphic()).isTrue();
        assertThat(Ollie.getModelAdapter(Attachment.ImageAttachment.class).getTypeColumn()).isEqualTo("Type");
    }

    @Test
    public void testLoadEntity() {
        assertThat(mImageId)
                .isNotNull()
                .isNotEqualTo(-1l);

        Attachment image = Attachment.find(Attachment.class, mImageId);
        assertThat(image).isNotNull();
        assertThat(image.id).isEqualTo(mImageId);
        assertThat(image.getTitle()).isEqualTo(Attachment.ImageAttachment.TITLE);
    }

}