package kr.or.hsnarae.transporthelp.impl.never_map;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.nhn.android.maps.NMapView;
import com.nhn.android.mapviewer.overlay.NMapOverlayManager;

/**
 * Created by IONEMAX on 2016-12-08.
 */

public class MapContainerView extends ViewGroup
{
    private NMapOverlayManager mOverlayManager;
    private NMapView mMapView;

    public MapContainerView(Context context)
    {
        super(context);
    }

    public MapContainerView(Context context, NMapOverlayManager overlayManager, NMapView mapView)
    {
        super(context);

        mOverlayManager = overlayManager;
        mMapView = mapView;
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom)
    {
        final int width = getWidth();
        final int height = getHeight();
        final int count = getChildCount();
        for (int i = 0; i < count; i++) {
            final View view = getChildAt(i);
            final int childWidth = view.getMeasuredWidth();
            final int childHeight = view.getMeasuredHeight();
            final int childLeft = (width - childWidth) / 2;
            final int childTop = (height - childHeight) / 2;
            view.layout(childLeft, childTop, childLeft + childWidth, childTop + childHeight);
        }

        if (changed) {
            mOverlayManager.onSizeChanged(width, height);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        int w = getDefaultSize(getSuggestedMinimumWidth(), widthMeasureSpec);
        int h = getDefaultSize(getSuggestedMinimumHeight(), heightMeasureSpec);
        int sizeSpecWidth = widthMeasureSpec;
        int sizeSpecHeight = heightMeasureSpec;

        final int count = getChildCount();
        for (int i = 0; i < count; i++) {
            final View view = getChildAt(i);

            if (view instanceof NMapView) {
                if (mMapView.isAutoRotateEnabled()) {
                    int diag = (((int)(Math.sqrt(w * w + h * h)) + 1) / 2 * 2);
                    sizeSpecWidth = MeasureSpec.makeMeasureSpec(diag, MeasureSpec.EXACTLY);
                    sizeSpecHeight = sizeSpecWidth;
                }
            }

            view.measure(sizeSpecWidth, sizeSpecHeight);
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}
