package com.example.cvd;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class PhotoBookAdapter extends RecyclerView.Adapter<PhotoBookAdapter.MyViewHolder>{

    Context context;

    ArrayList<PhotoBook> photoList = new ArrayList<>();

    PhotoBookAdapter(Context context){

        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(R.layout.photo_item, parent, false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        PhotoBook photo = photoList.get(position);

        holder.name_text.setText(String.valueOf(photo.getPhoto_name())); // 이름

        byte[] photoImage = photo.getPhoto_image(); //사진
        Bitmap bitmap = BitmapFactory.decodeByteArray(photoImage, 0, photoImage.length);
        holder.imageView.setImageBitmap(bitmap);
    }

    @Override
    public int getItemCount() {
        return photoList.size();
    }

    /**
     * 아이템 삭제
     * @param position 위치
     */
    public void removeItem(int position){

        photoList.remove(position);
    }


    public void addItem(PhotoBook item){

        photoList.add(item);
    }

    class MyViewHolder extends RecyclerView.ViewHolder{

        TextView name_text;
        ImageView imageView;
        LinearLayout mainLayout;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            name_text = itemView.findViewById(R.id.name_text);
            imageView = itemView.findViewById(R.id.photo_image);
            mainLayout = itemView.findViewById(R.id.main_layout);
        }
    }
}
