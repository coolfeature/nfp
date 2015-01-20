package ionas.nfp.ui;

import android.view.GestureDetector;
import android.view.MotionEvent;

public class FlingDetector {
	  static final int SWIPE_MIN_DISTANCE = 60;
	  static final int SWIPE_MAX_OFF_PATH = 125;
	  static final int SWIPE_THRESHOLD_VELOCITY = 200;
	  
	  private final GestureDetector gestureDetector;

	  public FlingDetector(final FlingListener listener) {
	    gestureDetector = new GestureDetector(new GestureDetector.SimpleOnGestureListener() {
	      @Override
	      public boolean onFling(MotionEvent e1, MotionEvent e2,
	          float velocityX, float velocityY) {
	        if (Math.abs(e1.getY() - e2.getY()) > SWIPE_MAX_OFF_PATH) {
	          if (Math.abs(e1.getX() - e2.getX()) > SWIPE_MAX_OFF_PATH
	              || Math.abs(velocityY) < SWIPE_THRESHOLD_VELOCITY) {
	            return false;
	          }
	        } else {
	          if (Math.abs(velocityX) < SWIPE_THRESHOLD_VELOCITY) {
	            return false;
	          }
	          if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE) {
	            listener.onRightToLeft();
	          } else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE) {
	            listener.onLeftToRight();
	          }
	        }
	        return true;

	      }
	    });
	  }
	  
	  public boolean onTouchEvent(MotionEvent event) {
	    return gestureDetector.onTouchEvent(event);
	  }
	  
	}

