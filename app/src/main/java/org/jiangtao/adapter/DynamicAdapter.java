package org.jiangtao.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.jiangtao.application.LifeApplication;
import org.jiangtao.bean.ArticleAllDynamic;
import org.jiangtao.lifetime.CommentActivity;
import org.jiangtao.lifetime.HomePageActivity;
import org.jiangtao.lifetime.ImageActivity;
import org.jiangtao.lifetime.R;
import org.jiangtao.lifetime.UserHomePageActivity;
import org.jiangtao.utils.BitmapUtils;
import org.jiangtao.utils.ConstantValues;

import java.io.IOException;
import java.util.ArrayList;

import cn.sharesdk.onekeyshare.OnekeyShare;
import de.hdodenhof.circleimageview.CircleImageView;

import static org.jiangtao.lifetime.R.id.dynamic_textview_userName;

/**
 * Created by mr-jiang
 * on 15-12-2.
 * DynamicFragment recylerView适配
 */
public class DynamicAdapter extends RecyclerView.Adapter<DynamicAdapter.ViewHolder> {
    public ArrayList<ArticleAllDynamic> mList;
    public Context mContext;
    private LayoutInflater mLayoutInflater;
    public static final String TAG = DynamicAdapter.class.getSimpleName();
    public static Bitmap bitmap = null;
    public DynamicAdapter.ViewHolder mHolder;
    public static boolean mHeadIsClick = true;
    public android.os.Handler handler;

    /**
     * 构造函数
     */
    public DynamicAdapter(ArrayList<ArticleAllDynamic> mList, Context context) {
        this.mList = mList;
        this.mContext = context;
        mLayoutInflater = LayoutInflater.from(mContext);
    }


    @Override
    public DynamicAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mLayoutInflater.inflate(
                R.layout.layout_dynamic_listview, parent, false);
        final ViewHolder holder = new ViewHolder(view);
        holder.setOnItemClickListener(new ViewHolder.ViewHolderOnClick() {
            @Override
            public void onItemClicked(View view, int position) {
                switch (view.getId()) {
                    case R.id.profile_image_listview: {
                        openHomePage(position);
                        break;
                    }
                    case R.id.dynamic_comment_listview: {
                        openComment(position);
                        break;
                    }
                    case R.id.dynamic_love_listview: {
                        new AsyncTask<Integer, Void, Void>() {

                            @Override
                            protected Void doInBackground(Integer... params) {
                                int position = params[0];
                                try {
                                    BitmapUtils.savePhotoToSDCard(ConstantValues.
                                                    saveImageUri, mList.get(position).getArticle_id() + ".png",
                                            LifeApplication.picasso.load(ConstantValues.getArticleImageUrl +
                                                    mList.get(position).getArticle_image()).get());
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                return null;
                            }
                        }.execute(position);
                        showShare(position);
                        break;
                    }
                    case R.id.dynamic_imageview: {
                        Intent intent = new Intent(mContext, ImageActivity.class);
                        intent.putExtra("image_address", mList.get(position).getArticle_image());
                        mContext.startActivity(intent);
                    }
                }
            }
        });
        return holder;
    }


    private void showShare(int position) {
        OnekeyShare oks = new OnekeyShare();
        //关闭sso授权
        oks.disableSSOWhenAuthorize();
        // 分享时Notification的图标和文字  2.5.9以后的版本不调用此方法
        //oks.setNotification(R.drawable.ic_launcher, getString(R.string.app_name));
        // title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
        oks.setTitle(mContext.getString(R.string.share));
        // titleUrl是标题的网络链接，仅在人人网和QQ空间使用
        oks.setTitleUrl("https://github.com/BosCattle/LifeTime");
        // text是分享文本，所有平台都需要这个字段
        oks.setText(mList.get(position).getArticle_content());
        // imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
        oks.setImagePath(ConstantValues.saveImageUri + mList.get(position).
                getArticle_image() + ".png");//确保SDcard下面存在此张图片
        // url仅在微信（包括好友和朋友圈）中使用
        oks.setUrl("http://sharesdk.cn");
        // comment是我对这条分享的评论，仅在人人网和QQ空间使用
        oks.setComment("我是测试评论文本");
        // site是分享此内容的网站名称，仅在QQ空间使用
        oks.setSite(mContext.getString(R.string.app_name));
        // siteUrl是分享此内容的网站地址，仅在QQ空间使用
        oks.setSiteUrl("http://sharesdk.cn");
        // 启动分享GUI
        oks.show(mContext);
    }

    /**
     * 保存图片到本地
     */


    /**
     * 打开评论界面
     */
    private void openComment(int position) {
        int article_id = mList.get(position).getArticle_id();
        Intent intent = new Intent(mContext, CommentActivity.class);
        intent.putExtra("article_id", article_id);
        mContext.startActivity(intent);
    }

    @Override
    public void onBindViewHolder(DynamicAdapter.ViewHolder holder, int position) {
        mHolder = holder;
        holder.mArticleTextView.setText(mList.get(position).getArticle_content());
        holder.mHotTextView.setText(+
                mList.get(position).getArticle_love_number() + "");
        holder.mCommentTextView.setText(+
                mList.get(position).getArticle_comment_number() + "");
        holder.mCollectionTextView.setText(
                mList.get(position).getArticle_comment_number() + "");
        holder.mUserNameTextView.setText(mList.get(position).getUser_name());
        holder.mTimeTextView.setText((mList.get(position).getArticle_time()));
        if (LifeApplication.hasNetWork) {
            LifeApplication.picasso.load(ConstantValues.getArticleImageUrl +
                    mList.get(position).getArticle_image()).
                    into(holder.mArticleImageView);
            LifeApplication.picasso
                    .load(ConstantValues.getArticleImageUrl +
                            mList.get(position).getUser_headpicture())
                    .into(holder.mHeadImageCircleImageView);
        } else {
            Bitmap articleBitmap = getCacheBitmap(ConstantValues.getArticleImageUrl +
                    mList.get(position).getArticle_image());
            applyImageView(articleBitmap, holder);
            Bitmap headImageBitmap = getCacheBitmap(ConstantValues.getArticleImageUrl +
                    mList.get(position).getUser_headpicture());
            alpplyHeadImage(headImageBitmap, holder);
        }


    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    /**
     * 获取用户的位置，并且根据位置获得article_user_id
     *
     * @param position
     */
    public void openHomePage(int position) {
        if (LifeApplication.getInstance().isNetworkAvailable()) {
         if (mList.get(position).getArticle_user_id()==LifeApplication.user_id){
             Intent intent = new Intent(mContext, HomePageActivity.class);
             mContext.startActivity(intent);
         }else {
             Intent intent = new Intent(mContext, UserHomePageActivity.class);
             int article_user_id = mList.get(position).getArticle_user_id();
             intent.putExtra("user_id", article_user_id);
             mContext.startActivity(intent);
         }
        }
        else {
            Toast.makeText(mContext, R.string.article_network_error,
                    Toast.LENGTH_SHORT).show();
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public CircleImageView mHeadImageCircleImageView;
        public TextView mUserNameTextView;
        public TextView mTimeTextView;
        public ImageView mArticleImageView;
        public TextView mArticleTextView;
        public TextView mHotTextView;
        public TextView mCommentTextView;
        public TextView mCollectionTextView;
        public TextView mLoveTextView;
        public ViewHolderOnClick viewHolderOnClick;

        public ViewHolder(final View itemView) {
            super(itemView);
            mHeadImageCircleImageView = (CircleImageView) itemView.findViewById(
                    R.id.profile_image_listview);
            mUserNameTextView = (TextView) itemView.findViewById(dynamic_textview_userName);
            mTimeTextView = (TextView) itemView.findViewById(R.id.dynamic_time_listview);
            mArticleImageView = (ImageView) itemView.findViewById(R.id.dynamic_imageview);
            mArticleTextView = (TextView) itemView.findViewById(R.id.dynamic_article_content);
            mHotTextView = (TextView) itemView.findViewById(R.id.dynamic_textview_listview);
            mCommentTextView = (TextView) itemView.findViewById(R.id.dynamic_comment_listview);
            mCollectionTextView = (TextView) itemView.findViewById(R.id.dynamic_collection_listview);
            mLoveTextView = (TextView) itemView.findViewById(R.id.dynamic_love_listview);
            setOnclickListener();
        }

        public void setOnclickListener() {
            if (mHeadIsClick) {
                mHeadImageCircleImageView.setOnClickListener(this);
            }
            mArticleImageView.setOnClickListener(this);
            mCommentTextView.setOnClickListener(this);
            mCollectionTextView.setOnClickListener(this);
            mLoveTextView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (viewHolderOnClick != null) {
                viewHolderOnClick.onItemClicked(v, getLayoutPosition());
            }
        }

        public void setOnItemClickListener(ViewHolderOnClick viewHolderOnClick) {
            this.viewHolderOnClick = viewHolderOnClick;
        }

        public interface ViewHolderOnClick {
            void onItemClicked(View view, int position);
        }
    }

    /**
     * 根据地址获取缓存图片
     *
     * @param url
     * @return
     */
    public Bitmap getCacheBitmap(final String url) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    bitmap = LifeApplication.picasso.load(url).get();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
        if (bitmap != null) {
            return bitmap;
        }
        return null;
    }

    /**
     * 显示文章的图片
     *
     * @param bitmap
     * @param holder
     */
    public void applyImageView(Bitmap bitmap, DynamicAdapter.ViewHolder holder) {
        holder.mArticleImageView.setImageBitmap(bitmap);
    }

    /**
     * 显示头像
     *
     * @param bitmap
     * @param holder
     */
    public void alpplyHeadImage(Bitmap bitmap, DynamicAdapter.ViewHolder holder) {
        holder.mHeadImageCircleImageView.setImageBitmap(bitmap);
    }

    public void refresh(ArrayList<ArticleAllDynamic> list) {
        mList = list;
        notifyDataSetChanged();
    }
}
