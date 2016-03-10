package asynctask.yang.shu.asynctask;

import android.content.Context;
import android.media.MediaCodecList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Young on 2016/3/1.
 */
public class NewsAdapter extends BaseAdapter {

    private List<NewsBean> mList;
    private LayoutInflater layoutInflater;
    private ImageLoader imageLoader;
    public NewsAdapter(Context context, List<NewsBean> data) {
        this.mList = data;
        layoutInflater = LayoutInflater.from(context);
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
            imageLoader = new ImageLoader();
            imageLoader.showImageByAsyncTask(viewHolder.ivIcon,url);
            viewHolder.tvTitle = (TextView) convertView.findViewById(R.id.tv_title);
            viewHolder.tvContent=(TextView) convertView.findViewById(R.id.tv_Content);
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder)convertView.getTag();
        }

        new ImageLoader().showImageByThread(viewHolder.ivIcon, mList.get(position).newsIconUrl);
        viewHolder.tvTitle.setText(mList.get(position).newsTitle);
        viewHolder.tvContent.setText(mList.get(position).newsContent);
        return convertView;
    }

    class ViewHolder{
        public ImageView ivIcon;
        public TextView tvTitle;
        public TextView tvContent;
    }
}
