package games.android.com.loconotemvp;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

public class SlideAdapter extends PagerAdapter {
    Context context;
    LayoutInflater inflater;

    public SlideAdapter(Context context){
        this.context = context;
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return (view == (LinearLayout)object);
    }

    public String[] backgroundColors = {
            "gradient",
            "samecolorgradient"
    };

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.slide, container, false);
        LinearLayout layoutslide = view.findViewById(R.id.slidelinearlayout);
        //insert the stuff that is different between each slide here
        String pathname = "/drawable/" + backgroundColors[position];
        TextView gradientbackground = (TextView) view.findViewById(R.id.gradientdrawable);
        gradientbackground.setBackground(Drawable.createFromPath(pathname));
        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((LinearLayout)object);
    }


}
