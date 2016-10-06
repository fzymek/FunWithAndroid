package pl.fzymek.tiimagegallery.util;

import android.transition.ArcMotion;
import android.transition.ChangeBounds;
import android.transition.ChangeImageTransform;
import android.transition.ChangeTransform;
import android.transition.TransitionSet;

public class ImageTransition extends TransitionSet {

    public ImageTransition() {
        setOrdering(ORDERING_TOGETHER);
        addTransition(new ChangeBounds());
        addTransition(new ChangeTransform());
        addTransition(new ChangeImageTransform());
        setPathMotion(new ArcMotion());
    }
}
