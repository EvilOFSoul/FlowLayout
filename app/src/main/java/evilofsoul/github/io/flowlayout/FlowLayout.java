package evilofsoul.github.io.flowlayout;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

public class FlowLayout extends ViewGroup {
    // TODO: 24.02.2017 Remove hardcode data!
    private int columnWidth = 200;

    public FlowLayout(Context context) {
        super(context);
    }

    public FlowLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FlowLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public FlowLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public boolean shouldDelayChildPressedState() {
        return false;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final int childCount = getChildCount();
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);

        int childWidthMeasureSpec = MeasureSpec.makeMeasureSpec(
                MeasureSpec.getSize(columnWidth), MeasureSpec.AT_MOST);
        int childHeightMeasureSpec = MeasureSpec.makeMeasureSpec(
                MeasureSpec.getSize(heightMeasureSpec), MeasureSpec.AT_MOST);

        int columnHeight = 0;
        int calculatedWidth = 0;
        for(int i=0; i<childCount; i++) {
            View view = getChildAt(i);
            if (view.getVisibility() == GONE) {
                continue;
            }

            measureChild(view, childWidthMeasureSpec, childHeightMeasureSpec);
            // TODO: 24.02.2017 Optimize it! It is code duplicate from onLayout
            if(columnHeight+view.getMeasuredHeight() > height){
                columnHeight = 0;
                calculatedWidth += columnWidth;
            }
            columnHeight += view.getMeasuredHeight();
        }

        width = Math.max(calculatedWidth, width);

        setMeasuredDimension(width,height);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        final int childCount = getChildCount();

        int columnHeight = 0;
        int columnX = 0;
        for(int i=0; i<childCount; i++) {
            View view = getChildAt(i);
            final int viewHeight =  view.getMeasuredHeight();

            if(columnHeight+viewHeight>getMeasuredHeight()) {
                columnHeight = 0;
                columnX += columnWidth;
            }

            view.layout(columnX, columnHeight, columnX+view.getMeasuredWidth(), columnHeight+view.getMeasuredHeight());
            columnHeight+=view.getMeasuredHeight();
        }
    }

    public void setColumnWidth(int columnWidth) {
        this.columnWidth = columnWidth;
    }

}
