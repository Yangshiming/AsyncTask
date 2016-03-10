package asynctask.yang.shu.asynctask;

import android.content.Context;
import android.media.MediaCodecList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Young on 2016/3/1.
 */
public class NewsAdapter extends BaseAdapter implements AbsListView.OnScrollListener{

    private List<NewsBean> mList;
    private LayoutInflater layoutInflater;
    private ImageLoader imageLoader;
    private int mStart,mEnd;
    public static String[] URLS;
    private boolean mFirstIn;

    public NewsAdapter(Context context, List<NewsBean> data,ListView listView) {

        this.mList = data;
        imageLoader = new ImageLoader(listView);
        layoutInflater = LayoutInflater.from(context);
        URLS = new String[data.size()];
        for (int i = 0;i<data.size();i++){
            URLS[i] = data.get(i).newsIconUrl;
        }
        listView.setOnScrollListener(this);
        mFirstIn = true;
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null){
            convertView = layoutInflater.inflate(R.layout.item_layout,null);
            viewHolder = new ViewHolder();
            viewHolder.ivIcon = (ImageView) convertView.findViewById(R.id.iv_icon);
            String url = mList.get(position).newsIconUrl;
            viewHolder.ivIcon.setTag(url);
            /*
            * 1. 用多线程显示
            *  new ImageLoader().showImageByThread(viewHolder.ivIcon, url);;
            * */

            /*
            * 2. 用异步加载显示
            * */

            imageLoader.showImageByAsyncTask(viewHolder.ivIcon,url);
            viewHolder.tvTitle = (TextView) convertView.findViewById(R.id.tv_title);
            viewHolder.tvContent=(TextView) convertView.findViewById(R.id.tv_Content);
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder)convertView.getTag();
        }

        imageLoader.showImageByThread(viewHolder.ivIcon, mList.get(position).newsIconUrl);
        viewHolder.tvTitle.setText(mList.get(position).newsTitle);
        viewHolder.tvContent.setText(mList.get(position).newsContent);
        return convertView;
    }

    //当滚动状态改变时调用
    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        if (scrollState == SCROLL_STATE_IDLE){
            imageLoader.loadImage(mStart,mEnd);
        }else {
            imageLoader.cancleAllTask();
        }
    }

    //滚动时一直回调，直到停止滚动时才停止回调。单击时回调一次
    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        mStart = firstVisibleItem;
        mEnd = firstVisibleItem+visibleItemCount;

        //第一次显示时调用，visibleItemCount在开始时为0，这样不能就在图片
        if (mFirstIn == true && visibleItemCount > 0){
            imageLoader.loadImage(mStart,mEnd);
        }
    }

    class ViewHolder{
        public ImageView ivIcon;
        public TextView tvTitle;
        public TextView tvContent;
    }
}
