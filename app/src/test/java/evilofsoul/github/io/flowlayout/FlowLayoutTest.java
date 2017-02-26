package evilofsoul.github.io.flowlayout;

import android.content.Context;
import android.graphics.Rect;
import android.os.Parcelable;
import android.view.View;
import android.view.ViewGroup;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

@RunWith(RobolectricTestRunner.class)
public class FlowLayoutTest {
    private final static int COLUMN_WIDTH = 20;
    private final static int VIEWPORT_WIDTH = 100;
    private final static int VIEWPORT_HEIGHT = 60;

    Context context;
    int widthMeasureSpec;
    int heightMeasureSpec;

    FlowLayout layout;

    @Before
    public void setUp() throws Exception {

        context = RuntimeEnvironment.application;
        widthMeasureSpec = View.MeasureSpec.makeMeasureSpec(VIEWPORT_WIDTH, View.MeasureSpec.EXACTLY);
        heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(VIEWPORT_HEIGHT, View.MeasureSpec.EXACTLY);

        layout = new FlowLayout(context);
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(VIEWPORT_HEIGHT, VIEWPORT_WIDTH);
        layout.setLayoutParams(layoutParams);
        layout.setColumnWidth(COLUMN_WIDTH);
    }

    @After
    public void tearDown() throws Exception {

    }

    /**
     * Checking base logic of onMeasure method
     */
    @Test
    public void onMeasure_isCorrect(){
        View view = spy(new View(context));

        layout.addView(view);
        layout.measure(widthMeasureSpec, heightMeasureSpec);

        int childWidthMeasureSpec = View.MeasureSpec.makeMeasureSpec(COLUMN_WIDTH, View.MeasureSpec.AT_MOST);
        int childHeightMeasureSpec = View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(heightMeasureSpec), View.MeasureSpec.AT_MOST);

        verify(view).measure(childWidthMeasureSpec, childHeightMeasureSpec);
    }

    @Test
    public void onMeasure_widthWrapContent_correctLayoutWidth() {
        layout.setLayoutParams(new ViewGroup.LayoutParams(WRAP_CONTENT, VIEWPORT_HEIGHT));
        final int childCount = 25;
        final int childHeight = 20;

        View[] views = new View[childCount];
        for(int i=0; i<childCount; i++){
            views[i] = new View(context);
            views[i].setLayoutParams(new ViewGroup.LayoutParams(MATCH_PARENT, childHeight));
            layout.addView(views[i]);
        }
        layout.measure(widthMeasureSpec, heightMeasureSpec);

        int childCountInColumn = layout.getMeasuredHeight()/childHeight;
        int expectedLayoutWidth = childCount/childCountInColumn*COLUMN_WIDTH;
        assertThat(layout.getMeasuredWidth(),is(equalTo(expectedLayoutWidth)));
    }

    /**
     * Checking base logic of onLayout method
     */
    @Test
    public void onLayout_isCorrect(){
        View view = spy(new View(context));

        layout.addView(view);
        layout.measure(widthMeasureSpec, heightMeasureSpec);
        layout.layout(0,0,VIEWPORT_WIDTH, VIEWPORT_HEIGHT);

        verify(view).layout(anyInt(),anyInt(),anyInt(),anyInt());
    }

    @Test
    public void layout_layoutInOneColumn(){
        final int childHeight = 20;
        final int childCount = 3;

        View[] views = new View[childCount];
        for (int i = 0; i<childCount; i++){
            views[i] = createViewWithHeight(childHeight);
            layout.addView(views[i]);
        }

        layout.measure(widthMeasureSpec, heightMeasureSpec);
        layout.layout(0,0,VIEWPORT_WIDTH,VIEWPORT_HEIGHT);

        for (int i = 0; i<childCount; i++){
            final int actualTopOffset = views[i].getTop();

            final int expectedTopOffset = i*childHeight;
            assertThat(actualTopOffset, is(equalTo(expectedTopOffset)));
        }
    }

    @Test
    public void layout_layoutInTwoColumns(){
        final int heightOfView1 = 40;
        final int heightOfView2 = 10;
        final int heightOfView3 = 20;
        View view1 = createViewWithHeight(heightOfView1);
        View view2 = createViewWithHeight(heightOfView2);
        View view3 = createViewWithHeight(heightOfView3);

        addViewToLayout(layout, view1, view2, view3);
        layout.measure(widthMeasureSpec, heightMeasureSpec);
        layout.layout(0,0,VIEWPORT_WIDTH,VIEWPORT_HEIGHT);

        Rect actualBounds = new Rect();
        Rect expectedBounds;

        //Check location of view1
        view1.getHitRect(actualBounds);
        expectedBounds = new Rect(0,0,COLUMN_WIDTH,heightOfView1);
        assertThat(actualBounds, is(equalTo(expectedBounds)));
        //Check location of view2
        view2.getHitRect(actualBounds);
        expectedBounds = new Rect(0,heightOfView1, COLUMN_WIDTH , heightOfView1+heightOfView2);
        assertThat(actualBounds, is(equalTo(expectedBounds)));
        //Check location of view3
        view3.getHitRect(actualBounds);
        expectedBounds = new Rect(COLUMN_WIDTH, 0, 2*COLUMN_WIDTH , heightOfView3);
        assertThat(actualBounds, is(equalTo(expectedBounds)));
    }

    View createViewWithHeight(int height){
        View view = new View(context);
        view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, height));
        return view;
    }

    void addViewToLayout(ViewGroup layout, View... views){
        for(int i=0; i<views.length; i++){
            layout.addView(views[i]);
        }
    }
}