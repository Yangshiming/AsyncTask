package asynctask.yang.shu.asynctask;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.security.PrivateKey;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends Activity {

    private ListView mlistView;
    private static String url = "http://www.imooc.com/api/teacher?type=4&num=30";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mlistView = (ListView)findViewById(R.id.lv_main);


        new NewsAsyncTask().execute(url);



    }


    /*
    * 异步加载网页数据
    * */
    class NewsAsyncTask extends AsyncTask<String,Void,List<NewsBean>> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(List<NewsBean> newsBeans) {
            super.onPostExecute(newsBeans);
            NewsAdapter newsAdapter = new NewsAdapter(MainActivity.this,newsBeans);
            mlistView.setAdapter(newsAdapter);
        }

        @Override
        protected List<NewsBean> doInBackground(String... params) {

            return getJasonData(params[0]);
        }
    }


    /*
    * 通过url解析json数据
    *
    * */
    private List<NewsBean> getJasonData(String url) {

        List<NewsBean> newsBeanList = new ArrayList<>();
        try {
            String jasonData = readStream(new URL(url).openStream());
             // 此句功能与url.getConnection().getInputStream()相同，可以根据URL直接联网获得网络数据，简单粗暴，返回类型为InputStream
            JSONObject jsonObject;
            NewsBean newsBean;

            jsonObject = new JSONObject(jasonData);
            JSONArray jsonArray = jsonObject.getJSONArray("data");

            for(int i = 0;i<jsonArray.length();i++)
            {
                newsBean = new NewsBean();
                jsonObject = jsonArray.getJSONObject(i);
                newsBean.newsIconUrl = jsonObject.getString("picSmall");
                newsBean.newsTitle = jsonObject.getString("name");
                newsBean.newsContent = jsonObject.getString("description");
                newsBeanList.add(newsBean);

            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return  newsBeanList;
    }


    /*
    * 通过InputStream解析网页数据
    * 读取字符流，此接口可以以后常用
    * */
    private String readStream(InputStream is){
        InputStreamReader isr;
        String result = "";

        String line = "";
        try {
            isr = new InputStreamReader(is,"utf-8");   //将获得的字节流转化为字符流
            BufferedReader br = new BufferedReader(isr);
            while((line = br.readLine() )!= null){
                result += line;
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }


}
