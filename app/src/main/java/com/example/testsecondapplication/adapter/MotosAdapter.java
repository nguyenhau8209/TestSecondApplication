package com.example.testsecondapplication.adapter;

import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.testsecondapplication.R;
import com.example.testsecondapplication.api.MotosApi;
import com.example.testsecondapplication.api.MotosApiClient;
import com.example.testsecondapplication.modal.Motos;
import com.squareup.picasso.Picasso;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MotosAdapter extends RecyclerView.Adapter<MotosAdapter.MotosViewHolder> {
    public static List<Motos> list;

    public void setMotos(List<Motos> motos) {
        this.list = motos;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MotosViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_adapter_motos, parent, false);
        return new MotosViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MotosViewHolder holder, int position) {
        Motos motos = list.get(position);
        holder.bind(motos);
    }

    @Override
    public int getItemCount() {
        return list != null ? list.size() : 0;
    }

    public class MotosViewHolder extends RecyclerView.ViewHolder {

        private TextView tvName, tvPrice, tvColor;
        private ImageView imgMotos;
        private ImageButton btnEditMoto, btnDeleteMoto;
        private MotosApi motosApi;


        public MotosViewHolder(@NonNull View itemView) {
            super(itemView);

            tvName = itemView.findViewById(R.id.tvName);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            tvColor = itemView.findViewById(R.id.tvColor);
            imgMotos = itemView.findViewById(R.id.imageView);
            btnEditMoto = itemView.findViewById(R.id.btnEditMoto);
            btnDeleteMoto = itemView.findViewById(R.id.btnDeleteMoto);
            motosApi = MotosApiClient.getRetrofitInstance().create(MotosApi.class);

            btnEditMoto.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showUpdateDialog(list.get(getAdapterPosition()));
                }
            });

            btnDeleteMoto.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showDeleteDialog(list.get(getAdapterPosition()));
                }
            });
        }

        private void showDeleteDialog(Motos motos) {
            // Hiển thị dialog xoá
            // Trong dialog, bạn có thể xác nhận xoá và thực hiện gọi API để xoá

            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(itemView.getContext());
            dialogBuilder.setTitle("Delete Motos");
            dialogBuilder.setMessage("Are you sure delete this item!");

            dialogBuilder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Call<Void> delete = motosApi.deleteMotos(motos.getId());
                    delete.enqueue(new Callback<Void>() {
                        @Override
                        public void onResponse(Call<Void> call, Response<Void> response) {
                            int position = getAdapterPosition();
                            if (position >= 0 && position < list.size()) {
                                list.remove(position);
                                notifyItemRemoved(position);
                            }
                            Toast.makeText(itemView.getContext(), "Delete successfully!", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onFailure(Call<Void> call, Throwable t) {
                            Toast.makeText(itemView.getContext(), "Delete false!", Toast.LENGTH_SHORT).show();

                        }
                    });
                    // Thực hiện gọi API để xoá dữ liệu
                    // Sau khi xoá thành công, cập nhật lại danh sách và thông báo cho Adapter
                }
            });
            dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });
            AlertDialog alertDialog = dialogBuilder.create();
            alertDialog.show();
        }

        private void showUpdateDialog(Motos motos) {
            Log.d("Motos", "showUpdateDialog: " + motos.getName());
            // Hiển thị dialog chỉnh sửa
            // Trong dialog, bạn có thể cho người dùng nhập thông tin mới và thực hiện gọi API để cập nhật

            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(itemView.getContext());
            LayoutInflater inflater = LayoutInflater.from(itemView.getContext());
            View dialogView = inflater.inflate(R.layout.dialog_add_item, null);
            dialogBuilder.setView(dialogView);
            dialogBuilder.setTitle("Update Moto");

            EditText etNameUpdate = dialogView.findViewById(R.id.etNameCreate);
            EditText etPriceUpdate = dialogView.findViewById(R.id.etPriceCreate);
            EditText etColorUpdate = dialogView.findViewById(R.id.etColorCreate);
            ImageView image = dialogView.findViewById(R.id.imgCreate);

            etNameUpdate.setText(motos.getName());
            etPriceUpdate.setText(String.valueOf(motos.getPrice()));
            etColorUpdate.setText(motos.getColor());
            Picasso.get().load(motos.getImage()).into(image);

            dialogBuilder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Motos updateMoto = new Motos();
                    updateMoto.setName(etNameUpdate.getText().toString());
                    updateMoto.setPrice(Double.parseDouble(etPriceUpdate.getText().toString()));
                    updateMoto.setColor(etColorUpdate.getText().toString());
                    Call<Motos> update = motosApi.updateMotos(motos.getId(), updateMoto);
                    update.enqueue(new Callback<Motos>() {
                        @Override
                        public void onResponse(Call<Motos> call, Response<Motos> response) {
                            Motos updateItem = response.body();
                            Toast.makeText(itemView.getContext(), "Update successfully!", Toast.LENGTH_SHORT).show();
                            // Thực hiện cập nhật dữ liệu trong danh sách tại vị trí tương ứng
                            int position = getAdapterPosition();
                            if (position != RecyclerView.NO_POSITION) {
                                list.set(position, updateItem);
                                notifyItemChanged(position); // Thông báo cho Adapter cập nhật item tại vị trí position
                            }
                        }

                        @Override
                        public void onFailure(Call<Motos> call, Throwable t) {
                            Toast.makeText(itemView.getContext(), "Update false!", Toast.LENGTH_SHORT).show();
                        }
                    });
                    // Thực hiện gọi API để cập nhật dữ liệu
                    // Sau khi cập nhật thành công, cập nhật lại danh sách và thông báo cho Adapter
                }
            });
            dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });
            AlertDialog alertDialog = dialogBuilder.create();
            alertDialog.show();
        }


        public void bind(Motos motos) {
            Log.d("Motos", "bind: " + motos.getCreateAt());
            tvName.setText("Name: " + motos.getName());
            tvPrice.setText("Price: " + motos.getPrice());
            tvColor.setText("Color: " + motos.getColor());
            Picasso.get().load(motos.getImage()).into(imgMotos);
        }
    }
}
