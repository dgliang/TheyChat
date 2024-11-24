package com.example.theychat;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import com.example.theychat.util.DateUtil;
import com.example.theychat.work.CollectWork;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class WorkManagerActivity extends AppCompatActivity {
    private final static String TAG = "WorkManagerActivity";
    private TextView tv_result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_work_manager);

        tv_result = findViewById(R.id.tv_result);

        findViewById(R.id.btn_once_start).setOnClickListener(view -> doOnceWork());
        findViewById(R.id.btn_period_start).setOnClickListener(view -> doPeriodWork());
    }

    // 执行一次性动作
    private void doOnceWork() {

        // 打印日志，表示开始执行一次性工作
        Log.d(TAG, "doOnceWork");
        String workName = "OnceName";

        // 创建约束条件，设置该任务的约束条件为：需要网络连接
        Constraints constraints = new Constraints.Builder()
                //.setRequiresBatteryNotLow(true) // 设备电量充足
                //.setRequiresCharging(true) // 设备正在充电
                .setRequiredNetworkType(NetworkType.CONNECTED) // 已经连上网络
                .build();

        Data inputData = new Data.Builder()
                .putString("name", "小明")
                .putInt("height", 180)
                .putDouble("weight", 80)
                .build();

        String workTag = "OnceTag";
        OneTimeWorkRequest onceRequest = new OneTimeWorkRequest.Builder(CollectWork.class)
                .addTag(workTag)
                .setConstraints(constraints)
                .setInputData(inputData)
                .build();

        // 获取生成的工作请求的唯一标识符（UUID）
        UUID workID = onceRequest.getId();

        // 将一次性工作请求添加到工作队列中进行执行
        WorkManager workManager = WorkManager.getInstance(this);
        workManager.enqueue(onceRequest);

        workManager.getWorkInfoByIdLiveData(workID).observe(this, workInfo -> {
            Log.d(TAG, "WorkInfo: " + workInfo.toString());

            // 判断工作是否成功完成
            if (workInfo.getState() == WorkInfo.State.SUCCEEDED) {
                Data output = workInfo.getOutputData();
                int code = output.getInt("resultCode", 0);
                String desc = output.getString("resultDesc");
                String result = String.format("处理结果: resultCode=%d, resultDesc=%s", code, desc);
                tv_result.setText(result);
            }
        });
    }

    // 执行持续性动作
    private void doPeriodWork() {

        // 打印日志，表示开始执行周期性工作
        Log.d(TAG , "doPeriodWork");
        tv_result.setText("周期性任务请观察 App 日志");
        String workName = "PeriodName";

        // 创建约束条件，设置该任务的约束条件为：需要网络连接
        Constraints constraints = new Constraints.Builder()
                //.setRequiresBatteryNotLow(true) // 设备电量充足
                //.setRequiresCharging(true) // 设备正在充电
                .setRequiredNetworkType(NetworkType.CONNECTED) // 已经连上网络
                .build();

        Data inputData = new Data.Builder()
                .putString("name", "小芳")
                .putInt("height", 170)
                .putDouble("weight", 60)
                .build();

        String workTag = "PeriodTag";
        PeriodicWorkRequest periodRequest = new PeriodicWorkRequest.Builder(CollectWork.class, 15, TimeUnit.MINUTES)
                .addTag(workTag)
                .setConstraints(constraints)
                .setInputData(inputData)
                .build();

        // 获取生成的工作请求的唯一标识符（UUID）
        UUID workId = periodRequest.getId();

        // 将一次性工作请求添加到工作队列中进行执行
        WorkManager workManager = WorkManager.getInstance(this);
        workManager.enqueue(periodRequest);

        workManager.getWorkInfoByIdLiveData(workId).observe(this, workInfo -> {
            Log.d(TAG, "workInfo:" + workInfo.toString());

            // 判断工作是否成功完成
            if (workInfo.getState() == WorkInfo.State.SUCCEEDED) {
                Data outputData = workInfo.getOutputData();
                int resultCode = outputData.getInt("resultCode", 0);
                String resultDesc = outputData.getString("resultDesc");
                String desc = String.format("%s resultCode=%d，resultDesc=%s", DateUtil.getNowTime(), resultCode, resultDesc);
                tv_result.setText(desc);
            }
        });
    }
}
