package nl.codesheep.android.pagesforreddit.data;

import android.net.Uri;
import android.support.test.InstrumentationRegistry;
import android.test.ProviderTestCase2;
import android.test.mock.MockContentResolver;
import android.util.Log;

import org.junit.Test;

import nl.codesheep.android.pagesforreddit.data.generated.SubRedditProvider;
import nl.codesheep.android.pagesforreddit.data.models.SubReddit;

import static nl.codesheep.android.pagesforreddit.data.SubRedditProvider.SubReddits.SUBREDDITS;

public class ProviderTest extends ProviderTestCase2<SubRedditProvider> {
    private static final String TAG = ProviderTest.class.getSimpleName();
    private static MockContentResolver resolver;

    public ProviderTest() {
        super(SubRedditProvider.class, SubRedditProvider.AUTHORITY);
    }

    @Override
    public void setUp() {
        try {
            Log.i(TAG, "Entered setup");
            setContext(InstrumentationRegistry.getTargetContext());
            super.setUp();
            resolver = getMockContentResolver();
        }
        catch (Exception e) {

        }
    }

    @Override
    public void tearDown() {
        try {
            super.tearDown();
        }
        catch (Exception e) {

        }
    }

    @Test
    public void testThing() {
        assertNotNull(resolver);
        SubReddit subReddit = new SubReddit();
        subReddit.setName("awww");
        Uri uri = resolver.insert(SUBREDDITS, subReddit.toContentValues());

        assertEquals("content://nl.codesheep.android.pagesforreddit/subreddits/1", uri.toString());

    }
}
