package com.example.dell_1.myapp3.InternalMemory;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaScannerConnection;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.dell_1.myapp3.R;

import java.io.File;
import java.util.ArrayList;

import static com.example.dell_1.myapp3.InternalMemory.InternalStorage.selectallflag;

public class MyRecyclerViewAdapter extends RecyclerView.Adapter<MyRecyclerViewAdapter.ViewHolder> {

    private ArrayList<String> mData;
    private ArrayList<String> mData2;
    private LayoutInflater mInflater;
    private int selected_position ;
    private ItemClickListener mClickListener;
    private ArrayList<String> mSelected ;
    ArrayList<Uri> files = new ArrayList<>();
    Uri uri;
    private static final String TAG = "com.example.dell_1.myapp3.InternalMemory";
    private Context context;

    // data is passed into the constructor
    public MyRecyclerViewAdapter(Context context, ArrayList<String> data, ArrayList<String> data2) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
        this.mData2 = data2;
        this.context = context;
        this.mSelected = new ArrayList<>();
    }

    // inflates the cell layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.recyclerview_item, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the textview in each cell
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String animal = mData.get(position);
        String animal2 = mData2.get(position);
        int THUMBSIZE = 150;
        Bitmap ThumbImage = ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(animal2),
                THUMBSIZE, THUMBSIZE);
        Bitmap thumb = ThumbnailUtils.createVideoThumbnail(animal2, MediaStore.Video.Thumbnails.MINI_KIND);
        holder.myTextView.setText(animal + "");
            if(animal!= null && (animal.endsWith(".mp3") || animal.endsWith(".aac"))){
                holder.myImage.setImageResource(R.drawable.song);
            }
        else if(animal!= null && animal.endsWith(".pdf")){
            holder.myImage.setImageResource(R.drawable.pdficon2);
        }
        else
            if(animal!= null && (animal.endsWith(".jpeg") || animal.endsWith(".jpg") || animal.endsWith(".png")) && BitmapFactory.decodeFile(animal2)!=null ){
                holder.myImage.setImageBitmap(ThumbImage);
            }
            else
            if(animal!= null && animal.endsWith(".mp4")){
                holder.myImage.setImageBitmap(thumb);
            }
            else
            if(animal!= null && animal.endsWith(".zip")){
                holder.myImage.setImageResource(R.drawable.zip);
            }
            else
            if(animal!= null && animal.endsWith(".txt")){
                holder.myImage.setImageResource(R.drawable.text);
            }
           else if(animal!= null && animal.endsWith(".apk")){
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageArchiveInfo(animal2, PackageManager.GET_ACTIVITIES);
            if(packageInfo != null) {
                ApplicationInfo appInfo = packageInfo.applicationInfo;
                    appInfo.sourceDir = animal2;
                    appInfo.publicSourceDir = animal2;
                Drawable icon = appInfo.loadIcon(context.getPackageManager());
                Bitmap bmpIcon = ((BitmapDrawable) icon).getBitmap();
                holder.myImage.setImageBitmap(bmpIcon);
            }

        }
            else {
                holder.myImage.setImageResource(R.drawable.folder);
            }

        if(selectallflag){
            holder.itemView.setBackgroundColor(Color.MAGENTA);
            }
        }


    // total number of cells
    @Override
    public int getItemCount() {
        return mData2.size();
    }


    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        TextView myTextView;
        ImageButton myImage;
        ViewHolder(View itemView) {
            super(itemView);
            myImage = (ImageButton) itemView.findViewById(R.id.buttonimage);
            myTextView = (TextView) itemView.findViewById(R.id.info_text);
            myImage.setOnClickListener(this);
            myImage.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }

        @Override
        public boolean onLongClick(View view) {
            if (mClickListener != null) mClickListener.onLongClick(view, getAdapterPosition());
            selected_position = getAdapterPosition();
            if (mSelected.contains(Integer.toString(selected_position))) {
                mSelected.remove(Integer.toString(selected_position));
                itemView.setBackgroundColor(Color.TRANSPARENT);// remove item from list;
                // update view (v) state here
                // eg: remove highlight
            } else {
                mSelected.add(Integer.toString(selected_position));
                itemView.setBackgroundColor(Color.LTGRAY);
                // add item to list
                // update view (v) state here
                // eg: add highlight
            }
            Log.v(TAG, Integer.toString(mSelected.size()) + " this is size");
            return  true;
        }
    }

    // convenience method for getting data at click position
    public String getItem(int id) {
        return mData2.get(id);
    }

    // allows clicks events to be caught
    public void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
        boolean onLongClick(View view,int position);
    }

    protected void deleteItem() {
        for (int i = 0; i < mSelected.size(); i++) {
            Log.v(TAG, mSelected.get(i));
            Log.v(TAG, mData2.get(Integer.parseInt(mSelected.get(i))));
            File file = new File(mData2.get(Integer.parseInt(mSelected.get(i))));
            if(file.isDirectory()){
                String[] children = file.list();
                for (int j = 0; j < children.length; j++)
                {
                    new File(file, children[i]).delete();
                    mData2.remove(Integer.parseInt(mSelected.get(i)));
                }
            }
            Log.v(TAG, file.toString());
            file.delete();
            mData2.remove(Integer.parseInt(mSelected.get(i)));
            MediaScannerConnection.scanFile(context,new String[] { file.toString() }, null, new MediaScannerConnection.OnScanCompletedListener()
            {
                public void onScanCompleted(String path, Uri uri)
                {
                    Log.i("ZAA", "Scanned " + path + ":");
                    Log.i("ZAA", "-> uri=" + uri);
                }
            });
        }
        notifyDataSetChanged();

    }

    protected void shareItem(View v){
        Intent share = new Intent(Intent.ACTION_SEND_MULTIPLE);
        share.setType("*/*");
        for (int i = 0; i < mSelected.size(); i++) {
            Log.v(TAG, mSelected.get(i));
            Log.v(TAG, mData2.get(Integer.parseInt(mSelected.get(i))));
            File file = new File(mData2.get(Integer.parseInt(mSelected.get(i))));
            Uri uri = Uri.fromFile(file);
            files.add(uri);
        }
            share.putParcelableArrayListExtra(Intent.EXTRA_STREAM, files);
            v.getContext().startActivity(Intent.createChooser(share, "Share file"));

    }

    protected void selectAll() {
            mSelected.addAll(mData2);
            Log.v(TAG, Integer.toString(mSelected.size()));
        }

        protected  ArrayList<String>  getList(){
            for(String im : mSelected){
                Log.v(TAG, im + " print");
            }
            Log.v(TAG,Integer.toString(mSelected.size()) + "  final size");
            return mSelected;
        }

        protected Uri getUri() {
            for (int i = 0; i < mSelected.size(); i++) {
                uri = Uri.fromFile(new File(mData2.get(Integer.parseInt(mSelected.get(i)))));
                Log.v(TAG, uri.toString() +" uri");
            }
            return uri;
        }


}
