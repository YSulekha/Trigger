package com.haryadi.trigger.touch_helper;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;

import com.haryadi.trigger.R;


//Method for delete when swiped in recycler view

public class TouchhelperCallback extends ItemTouchHelper.Callback {

    private final ItemTouchHelperAdapter mAdapter;
    public static final float ALPHA_FULL = 1.0f;
    Context context;
    int mDirection;

    public TouchhelperCallback(ItemTouchHelperAdapter adapter,Context c){
        mAdapter = adapter;
        context = c;
    }
    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
        //Commented to test the swipe from left to right
    //    int swipeFlags = ItemTouchHelper.START;
        int swipeFlags = ItemTouchHelper.END;
        return makeMovementFlags(dragFlags,swipeFlags);
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        return false;
    }

    @Override
    public boolean isItemViewSwipeEnabled() {
        return true;
    }



    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        if(direction == ItemTouchHelper.END){
            mAdapter.onItemDismiss(viewHolder.getAdapterPosition());
        }
    }

    @Override
    public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        Paint p = new Paint();
        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
            View itemView = viewHolder.itemView;
            float height = (float) itemView.getBottom() - (float) itemView.getTop();
            float width = height / 3;
            if (dX > 0 ) {
                p.setColor(context.getResources().getColor(R.color.colorAccent));
                //left,top,right,bottom
                RectF background = new RectF((float) itemView.getLeft(), (float) itemView.getTop(), dX, (float) itemView.getBottom());
                c.drawRect(background, p);
                Bitmap icon = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_delete_black_48dp);
                RectF icon_dest = new RectF((float) itemView.getLeft() + width, (float) itemView.getTop() + width, (float) itemView.getLeft() + 2 * width, (float) itemView.getBottom() - width);
                c.drawBitmap(icon, null, icon_dest, p);
            }
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }

    }

}
