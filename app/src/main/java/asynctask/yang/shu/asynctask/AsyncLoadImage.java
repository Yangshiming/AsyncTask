package asynctask.yang.shu.asynctask;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;


public class AsyncLoadImage extends Activity {

    private ImageView imageView;
    private ProgressBar progressBar;
    MyAsyncTask myAsyncTask;
    private static String url ="http://static.cnbetacdn.com/article/2016/0228/3e370ce9480cb91.png";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_async_load_image);

        imageView = (ImageView)findViewById(R.id.iv_loadImage);
        progressBar = (ProgressBar)findViewById(R.id.pg_loadImage);

        myAsyncTask = new MyAsyncTask();
        myAsyncTask.execute(url);
    }

    // AsyncTask是基于线程池进行实现的,当一个线程没有结束时,后面的线程是不能执行的.
    @Override
    protected void onPause() {
        super.onPause();
        if(myAsyncTask != null && myAsyncTask.getStatus() == AsyncTask.Status.RUNNING)
        {
            //cancel方法只是将对应的AsyncTask标记为cancelt状态,并不是真正的取消线程的执行.
            myAsyncTask.cancel(true);
        }
    }

//        AsyncTask定义了三种泛型类型 Params，Progress和Result。

//        Params 启动任务执行的输入参数，比如HTTP请求的URL。
//        Progress 后台任务执行的百分比。
//        Result 后台执行任务最终返回的结果，比如String。
    class MyAsyncTask extends AsyncTask<String,Integer,Bitmap> {

//            异步加载数据最少要重写以下这两个方法：
//
//            1. doInBackground(Params…) 后台执行，比较耗时的操作都可以放在这里。注意这里不能直接操作UI。
//            此方法在后台线程执行，完成任务的主要工作，通常需要较长的时间。
//            在执行过程中可以调用publicProgress(Progress…)来更新任务的进度。
//            2. onPostExecute(Result)  相当于Handler 处理UI的方式，在这里面可以使用在doInBackground 得到的结果处理操作UI。
//            此方法在主线程执行，任务执行的结果作为此方法的参数返回

        @Override
        protected Bitmap doInBackground(String... params) {

            //如果task是cancel状态,则终止for循环,以进行下个task的执行.
            if (isCancelled()){
                return null;
            }
            //获取传进来的参数
            String url = params[0];
            Bitmap bitmap = null;
            URLConnection connection;
            InputStream is;

            try {
                for (int i = 0 ;i<100;i++) {
                    Thread.sleep(30);
                    //调用publishProgress方法将自动触发onProgressUpdate方法来进行进度条的更新.
                    publishProgress(i);
                }

                //加载图片
                connection = new URL(url).openConnection();
                is = connection.getInputStream();
                //为了更清楚的看到加载图片的等待操作,将线程休眠3秒钟.
                BufferedInputStream bis = new BufferedInputStream(is);
                bitmap= BitmapFactory.decodeStream(bis);
                is.close();
                bis.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }
            return bitmap;
        }

        @Override
        protected void onPreExecute() {
            //super.onPreExecute();
            //此处将progressbar设置为可见
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            //隐藏progressBar
            progressBar.setVisibility(View.GONE);
            imageView.setImageBitmap(bitmap);

        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            progressBar.setProgress(values[0]);
        }
    }

}
