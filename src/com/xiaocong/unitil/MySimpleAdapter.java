/**
 * 自定义简单适配器
 */
package com.xiaocong.unitil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.StrikethroughSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
public class MySimpleAdapter extends BaseAdapter
{
    private Context context;
    private List<Map<String, Object>> list;
    private int layoutId;
    private String[] itemName;
    private int[] itemId;
    private LayoutInflater layoutInflater;
    private ArrayList<Object> listviews;
    private Boolean checked[];
    private AdapterListener mAdapterListener;
    private int mPosition;
    private View mConvertView;
    private boolean isLv = true;
    private int selected,i;
    private int itemCount = 0,getview=0;; 
    private RelativeLayout.LayoutParams relp;
    private AbsListView.LayoutParams itemrelp;
    public MySimpleAdapter(Context context, List<Map<String, Object>> data, int resource,
            String[] from, int[] to,RelativeLayout.LayoutParams relp,int i)
    {
        this.context = context;
        this.list = data;
        this.i=i;
        this.layoutId = resource;
        this.itemName = from;
        this.itemId = to;
        this.relp=relp;
        int innerWidth=relp.width;
        int innerheight=relp.height;
        
        itemrelp=new AbsListView.LayoutParams((int)(innerWidth*0.25),(int)(innerheight*0.5-6));
        layoutInflater = LayoutInflater.from(context);
        checked = new Boolean[getCount()];
    }

    public MySimpleAdapter(Context context, List<Map<String, Object>> data, int resource,
            String[] from, int[] to, AdapterListener adapterListener)
    {
        this.context = context;
        this.list = data;
        this.layoutId = resource;
        this.itemName = from;
        this.itemId = to;
        layoutInflater = LayoutInflater.from(context);
        checked = new Boolean[getCount()];
        for (int i = 0; i < checked.length; i++)
        {
            checked[i] = false;
        }

        this.mAdapterListener = adapterListener;
    }

    @Override
    public int getCount()
    {
        // TODO Auto-generated method stub
    	itemCount = list.size();
        return list.size();
    }

    @Override
    public Object getItem(int position)
    {
        // TODO Auto-generated method stub
        return list.get(position);
    }

    @Override
    public long getItemId(int position)
    {
        // TODO Auto-generated method stub
        return 0;
    }

    @SuppressWarnings("unchecked")
    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
//    	Log.e("getView", position+"");
//    	if (position==(itemCount-1)) {
//			MyAccountCenterActivity.tt = 2;
//		}
    	if (position==0) {
    		getview++;
		}
    	if (getview>1) {
			if (convertView!=null) {
				listviews = (ArrayList<Object>) convertView.getTag();
				return convertView;
			}
		}
        if (convertView == null)
        {
            convertView = layoutInflater.inflate(layoutId, null);
            listviews = new ArrayList<Object>();
            for (int i = 0; i < itemId.length; i++)
            {
                View v = convertView.findViewById(itemId[i]);
                listviews.add(v);
            }
            convertView.setTag(listviews);
        }
        else
        {
            listviews = (ArrayList<Object>) convertView.getTag();
        }
        
//        Log.e("listviews.size()=",listviews.size()+"");
        
        /****************************************************************/
       
        /****************************************************************/
        for (int i = 0; i < listviews.size(); i++)
        {
            Object o = listviews.get(i);
//            if (o instanceof RadioButton)
//            {
//                RadioButton rb = (RadioButton) o;
//                if (i != listviews.size() - 1)
//                {
//                    if (checked[i])
//                    {
//                        rb.setChecked(true);
//                    }
//                    else
//                    {
//                        rb.setChecked(false);
//                    }
//                }
//            }
//            else if (o instanceof Button)
//            {
//                Object o2 = list.get(position).get(itemName[i]);
//                if (o2 instanceof String)
//                {
//                    ((Button) o).setText(String.valueOf(list.get(position).get(itemName[i])));
//                }
//                else if (o2 instanceof Integer)
//                {
//                    ((Button) o).setBackgroundResource((Integer) o2);
//                }
//            }
            if (o instanceof TextView)
            {
                String s = String.valueOf(list.get(position).get(itemName[i]));
                if (s != null && s.length() >= 2 && s.substring(0, 2).equals("原价"))
                {
                    s = s.replace("原价", "");
                    SpannableStringBuilder ssb = new SpannableStringBuilder(s);
                    ssb.setSpan(new StrikethroughSpan(), 0, ssb.length(),
                            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    ((TextView) o).setText(ssb);
                }
                else
                {
                    ((TextView) o).setText(String.valueOf(list.get(position).get(itemName[i])));
                }
            }
        
        
        
//            else if (o instanceof WebImageView)
//            {
//                String o2 = String.valueOf(list.get(position).get(itemName[i]));
//                if (o2 instanceof String)
//                {
//                    WebImageView wImageView = (WebImageView) o;
//                    wImageView.setImageFromURL(o2);
//                }
//            }
            else if (o instanceof ImageView)
            {
                Object o2 = list.get(position).get(itemName[i]);
                
                ImageView iv=(ImageView) o;
//                ImageLoader  imageLoader=new ImageLoader(context,null);
//                if (list.get(position).get(itemName[i])!=null) {
////                    imageLoader.DisplayImage(XCAPI.BASE_URL+list.get(position).get(itemName[i]), iv);
//                    imageLoader.DisplayImage(XCAPI.BASE_URL+list.get(position).get(itemName[i]).toString(), iv);
//				}else {
//                Log.e("list.get(position).get(itemName[i]).toString()", list.get(position).get(itemName[i]).toString()+"");
                iv.setBackgroundResource((Integer) o2);
					 
					 
//					iv.setBackgroundResource(Integer.parseInt(list.get(position).get(itemName[i]).toString()));
//				}
        
//                if (o2 instanceof Bitmap)
//                {
//                    Bitmap b = (Bitmap) o2;
////                    ImageView iv = (ImageView) o;
//                    if (b != null && !b.isRecycled())
//                    {
//                        iv.setImageBitmap(b);
//                    }
//                    else
//                    {
//                        iv.setImageResource(R.drawable.bg_app_default);
//
//                    }
//                }
//                else if (o2 instanceof Drawable)
//                {
//                    ((ImageView) o).setImageDrawable((Drawable) o2);
//                }
//                else if (o2 instanceof Integer)
//                {
//                    ((ImageView) o).setImageResource((Integer) o2);
//                }
//                else
//                {
//                    ((ImageView) o).setImageResource(R.drawable.bg_app_default);
//                }
            }else if (o instanceof ImageButton) {
            	 Object o2 = list.get(position).get(itemName[i]);
                 
            	 ImageButton iv=(ImageButton) o;
//                 ImageLoader  imageLoader=new ImageLoader(context,null);
//                 if (list.get(position).get(itemName[i])!=null) {
////                     imageLoader.DisplayImage(XCAPI.BASE_URL+list.get(position).get(itemName[i]), iv);
//                     imageLoader.DisplayImage(XCAPI.BASE_URL+list.get(position).get(itemName[i]).toString(), iv);
// 				}else {
//                 Log.e("list.get(position).get(itemName[i]).toString()", list.get(position).get(itemName[i]).toString()+"");
                 iv.setBackgroundResource((Integer) o2);
			}
            // else if (o instanceof LinearLayout) {
            // LinearLayout layout = (LinearLayout) o;
            // layout.removeAllViews();
            // addStars(
            // (Activity) context,
            // layout,
            // Float.parseFloat((String) list.get(position).get(
            // itemName[i])));
            // }
            // if (isLv) {
            // if (position % 2 == 0) {
            // convertView.setBackgroundDrawable(context.getResources()
            // .getDrawable(R.drawable.selector_lv_color));
            // } else {
            // convertView.setBackgroundDrawable(context.getResources()
            // .getDrawable(R.drawable.selector_lv_blue));
            // }
            // }
            if (isLv)
            {
                // convertView.setBackgroundDrawable(context.getResources()
                // .getDrawable(R.drawable.selector_lv_yellow));
            }
        }

        if (mAdapterListener != null)
        {
            mPosition = position;
            mConvertView = convertView;
//            Log.e("mPosition =",mPosition +"");
//            Log.e(" mConvertView =", mConvertView +"");
            
            mAdapterListener.addListener(mPosition, mConvertView, selected);
        }
        if (i>0) {
        	 convertView.setLayoutParams(itemrelp);
		}	else {
			   itemrelp=new AbsListView.LayoutParams(250,39);
			   convertView.setLayoutParams(itemrelp);
		}
        return convertView;
    }

    // 设置星星
    // public static void addStars(Activity a, LinearLayout layout, float i) {
    // if (i > 5) {
    // i = 5;
    // }
    // int w = Utils.dp2px(a, 12);
    // float j = 5 - i;
    // while (i > 0.5) {
    // ImageView iv = new ImageView(a);
    // iv.setLayoutParams(new LayoutParams(w, w));
    // iv.setImageResource(R.drawable.scoring_full);
    // layout.addView(iv);
    // i--;
    // }
    // if (i == 0.5) {
    // ImageView iv = new ImageView(a);
    // iv.setLayoutParams(new LayoutParams(w, w));
    // iv.setImageResource(R.drawable.scoring_half);
    // layout.addView(iv);
    // j--;
    // }
    // while (j > 0) {
    // ImageView iv = new ImageView(a);
    // iv.setLayoutParams(new LayoutParams(w, w));
    // iv.setImageResource(R.drawable.scoring_empty);
    // layout.addView(iv);
    // j--;
    // }
    // }

    /**
     * 
     * @author Administrator
     * @param position
     *            当前点击的item位置
     * @param convertView
     *            监听事件的view
     */
    public interface AdapterListener
    {
        void addListener(int position, View convertView, Object... objects);
    }

    public boolean isLv()
    {
        return isLv;
    }

    public void setLv(boolean isLv)
    {
        this.isLv = isLv;
    }

    public void setmAdapterListener(AdapterListener mAdapterListener)
    {
        this.mAdapterListener = mAdapterListener;
    }

    public int getSelected()
    {
        return selected;
    }

    public void setSelected(int selected)
    {
        if (this.selected != selected)
        {
            this.selected = selected;
            notifyDataSetChanged();
        }
    }
}
