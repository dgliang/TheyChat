package com.example.theychat.work;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

public class CollectWork extends Worker {
    private final static String TAG = "CollectWork";
    private Data input;

    public CollectWork(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        input = workerParams.getInputData();
    }

    @NonNull
    @Override
    public Result doWork() {
        String desc = String.format("请求参数包括：姓名=%s，身高=%d，体重=%f",
                input.getString("name"),
                input.getInt("height", 0),
                input.getDouble("weight", 0)
        );

        Log.d(TAG, "doWork: " + desc);

        Data output = new Data.Builder()
                .putInt("resultCode", 0)
                .putString("resultDesc", "处理成功")
                .build();

        return Result.success(output);
    }
}
