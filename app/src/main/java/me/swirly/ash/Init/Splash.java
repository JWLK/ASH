package me.swirly.ash.Init;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.widget.Toast;

import me.swirly.ash.MainActivity;
import me.swirly.ash.R;
import me.swirly.ash.Util.Dlog;
import me.swirly.ash.Support.PermissionChecker;

public class Splash extends AppCompatActivity {
    public Dlog mDlog = new Dlog(this);
    PermissionChecker permissionChecker;
    Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.init_splash_light);

        /**
         * Log Control Init
         */
        if(mDlog != null){
            boolean isDebuggable = Dlog.isDebuggable();
            Dlog.d("Debugging Status: "+isDebuggable);
        }
        permissionRequest();
        NetworkConnection();
    }

    @Override
    public void onBackPressed() {
        //초반 플래시 화면에서 넘어갈때 뒤로가기 버튼 못누르게 함
    }

    private void permissionRequest() {
        if(Build.VERSION.SDK_INT >= 23){
            permissionChecker = new PermissionChecker(this, this);
            if(!permissionChecker.checkPermissions()){
                permissionChecker.requestPermission();
            } else {
                handler.postDelayed(new AutoIntentHandler(), 3000); // millie seconds 후에 hd handler 실행  3000ms = 3초
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if(!permissionChecker.permissionResult(requestCode, permissions, grantResults)) {
            showToast_PermissionDeny();
        } else {
            handler.postDelayed(new AutoIntentHandler(), 2000); // millie seconds 후에 hd handler 실행  3000ms = 3초
        }
    }

    private void showToast_PermissionDeny() {
        AlertDialog.Builder localBuilder = new AlertDialog.Builder(this);
        localBuilder.setTitle("권한 설정")
                .setMessage("권한 거절로 인해 일부기능이 제한됩니다.")
                .setPositiveButton("권한 설정하러 가기", new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt){
                        try {
                            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                                    .setData(Uri.parse("package:" + getPackageName()));
                            intent.addCategory(Intent.CATEGORY_DEFAULT);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            Splash.this.finish();
                        } catch (ActivityNotFoundException e) {
                            e.printStackTrace();
                            Intent intent = new Intent(Settings.ACTION_MANAGE_APPLICATIONS_SETTINGS);
                            startActivity(intent);
                            Splash.this.finish();
                        }
                    }})
                .setNegativeButton("취소하기", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt) {
                        Toast.makeText(getApplication(),"권한 승인 후 서비스 접속 부탁드립니다.",Toast.LENGTH_SHORT).show();
                        finish();
                    }})
                .create()
                .show();

    }

    private class AutoIntentHandler implements Runnable{
        public void run(){
            startActivity(new Intent(getApplication(), MainActivity.class)); //로딩이 끝난 후, ChoiceFunction 이동
            Splash.this.finish(); // 로딩페이지 Activity stack에서 제거
        }
    }

    private boolean NetworkConnection() {
        int[] networkTypes = {ConnectivityManager.TYPE_MOBILE, ConnectivityManager.TYPE_WIFI};
        try {
            ConnectivityManager manager = (ConnectivityManager) getSystemService (Context.CONNECTIVITY_SERVICE);
            for (int networkType : networkTypes) {
                NetworkInfo activeNetwork = manager.getActiveNetworkInfo();
                if(activeNetwork != null && activeNetwork.getType() == networkType){
                    return true;
                }
            }
        } catch (Exception e) {
            NotConnected_showAlert();
            return false;
        }
        return false;
    }

    private void NotConnected_showAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("네트워크 연결 오류");
        builder.setMessage("사용 가능한 무선네트워크가 없습니다.\n" + "먼저 무선네트워크 연결상태를 확인해 주세요.")
                .setCancelable(false)
                .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        finish(); // exit
                        //application 프로세스를 강제 종료
                        android.os.Process.killProcess(android.os.Process.myPid() );
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();

    }
}
