package asynctask.yang.shu.asynctask;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.LruCache;
import android.widget.ImageView;
import android.widget.ListView;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Young on 2016/3/1.
 */
public class ImageLoader {

    private ImageView mImageView;
    private String mUrl;
    private LruCache<String,Bitmap> mCaches;
    private ListView mListView;
    private Set<NewsAsyncTask> mTask;




    public ImageLoader(ListView listVew){
        mListView = listVew;
        mTask = new HashSet<>();

        //获取最大可用内存
        long maxMemeory = Runtime.getRuntime().maxMemory();
        int cacheSize = (int) (maxMemeory/4);
        mCaches = new LruCache<String,Bitmap>(cacheSize){
            @Override
            protected int sizeOf(String key, Bitmap value) {
                //每次存入缓存的时候调用
                return value.getByteCount();
            }
        };
    }



    public void addBitmapToCache(String url,Bitmap bitmap){
        if (getBitmapFrpmCache(url) == null){
            mCaches.put(url,bitmap);
        }
    }

    public Bitmap getBitmapFrpmCache(String url){
        if (url != null){
            return mCaches.get(url);
        }
        else
            return  null;
    }
    /**
     * showImageByThread 是通过多线程的方式加载图片
     */
    private Handler mhandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (mImageView.getTag() == mUrl) {
                mImageView.setImageBitmap((Bitmap) msg.obj);
            }
        }
    };

    public void showImageByThread(ImageView imageView,final String url){
        mUrl = url;
        mImageView = imageView;
        new Thread(){
            @Override
            public void run() {
                super.run();
                try {
                    Bitmap bitmap = getBitmapFromURL(url);
                    Message message = Message.obtain();
                    message.obj = bitmap;
//                    mhandler.handleMessage(message);   //这儿不能用handleMessage，要用sendMessage（）；
                    mhandler.sendMessage(message);
                    Thread.sleep(1000);                  //方便看图片加载显示的时候是否抖动
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    public Bitmap getBitmapFromURL(String urlString) throws IOException {
        Bitmap bitmap = null;
        InputStream is = null;
        BufferedInputStream bis = null;

        try {
            URL url = new URL(urlString);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            is = new BufferedInputStream(httpURLConnection.getInputStream());
            //is = url.openConnection().getInputStream();
           // bis = new BufferedInputStream(is);
            bitmap = BitmapFactory.decodeStream(is);
            httpURLConnection.disconnect();
            return bitmap;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if (is != null) {
                is.close();
            }
        }

        return bitmap;
    }

    /*
    *  用异步加载的方式显示图片
    * */
    public void showImageByAsyncTask(ImageView imageView,String url){
        //现在缓存中去寻找是否存在图片
        Bitmap bitmap = getBitmapFrpmCache(url);
        //如果不存在那么就得去网上下载
        if (bitmap == null){
            imageView.setImageResource(R.drawable.ic_launcher);
        }
        else
            imageView.setImageBitmap(bitmap);
    }

    //用来加载从mstart到mEnd所有图片
    public void loadImage(int mStart,int mEnd){
        for (int i = mStart;i<mEnd;i++){
            String url = NewsAdapter.URLS[i];
            //现在缓存中去寻找是否存在图片
            Bitmap bitmap = getBitmapFrpmCache(url);
            //如果不存在那么就得去网上下载
            if (bitmap == null){
                NewsAsyncTask task = new NewsAsyncTask(url);
                new NewsAsyncTask(url).execute(url);
                mTask.add(task);
            }
            else{
                ImageView imageView = (ImageView) mListView.findViewWithTag(url);
                imageView.setImageBitmap(bitmap);
            }
        }
    }

    public void cancleAllTask() {
        for (NewsAsyncTask task:mTask){
            task.cancel(false);
        }
    }

    public class  NewsAsyncTask extends AsyncTask<String ,Void,Bitmap>{
//        private ImageView mImageView;
        private String url;
        public NewsAsyncTask(String url){
//            this.mImageView = imageView;
            this.url = url;
        }
        @Override
        protected Bitmap doInBackground(String... params) {
            Bitmap bitmap = null;
            try {
                bitmap = getBitmapFromURL(url);
                if ( bitmap == null){
                    addBitmapToCache(params[0],bitmap);
                }
                return bitmap;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            ImageView imageView = (ImageView) mListView.findViewWithTag(url);
            if (imageView != null && bitmap != null)
                imageView.setImageBitmap(bitmap);
            mTask.remove(this);
        }
    }

}
