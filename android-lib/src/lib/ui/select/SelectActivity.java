package lib.ui.select;

import java.util.ArrayList;
import java.util.List;

import android.lib.R;
import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.WindowManager.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

/**
 * @ClassName: SelectActivity.java
 * @Description:
 * @Author JinChao
 * @Date 2013-6-20 上午10:48:29
 * @Copyright: 版权由 HundSun 拥有
 */
public class SelectActivity extends Activity implements Callback
{

    // PopupWindow对象
    private PopupWindow       selectPopupWindow = null;
    // 自定义Adapter
    private OptionAdapter     optionsAdapter    = null;
    
    private ThicknessAdapter thicknessAdapter =null;
    private GridAdapter gridAdapter = null;
    // 下拉框选项数据源
    private ArrayList<String> datas             = new ArrayList<String>();
    // 下拉框依附组件
    private LinearLayout      parent;
    // 下拉框依附组件宽度，也将作为下拉框的宽度
    private int               pwidth;
    // 文本框
    private EditText          et;
    // 下拉箭头图片组件
    private ImageView         image;
    // 恢复数据源按钮
    private Button            button;
    // 展示所有下拉选项的ListView
    private ListView          listView          = null;
    // 用来处理选中或者删除下拉项消息
    private Handler           handler;
    // 是否初始化完成标志
    private boolean           flag              = false;
    
    private GridView gridView =null;
    private ImageView gridPopWinBtn;
    
    private SelectActivity me = this; 
    
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_main);
    }

    /**
     * 没有在onCreate方法中调用initWedget()，而是在onWindowFocusChanged方法中调用，
     * 是因为initWedget()中需要获取PopupWindow浮动下拉框依附的组件宽度，在onCreate方法中是无法获取到该宽度的
     */
    @Override
    public void onWindowFocusChanged(boolean hasFocus)
    {
        super.onWindowFocusChanged(hasFocus);
        while (!flag)
        {
            initWedget();
            flag = true;
        }
    }

    /** * 初始化界面控件 */
    private void initWedget()
    {
        // 初始化Handler,用来处理消息
        handler = new Handler(SelectActivity.this);
        // 初始化界面组件
        parent = (LinearLayout) findViewById(R.id.parent);
        et = (EditText) findViewById(R.id.edittext);
        Button listPopWinBtn = (Button) findViewById(R.id.list_popwin);
        // 获取下拉框依附的组件宽度
        int width = parent.getWidth();
        pwidth = width;
        // 设置点击下拉箭头图片事件，点击弹出PopupWindow浮动下拉框
        listPopWinBtn.setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View v)
            {
                if (flag)
                {
                    // 显示PopupWindow窗口
                    popupListWindwShowing();
                }
            }
        });
        
        gridPopWinBtn = (ImageView) findViewById(R.id.grid_popwin);
        gridPopWinBtn.setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View v)
            {
                if (flag)
                {
                    // 显示PopupWindow窗口
                    popupGridWindwShowing();
                }
            }
        });
        
        Button thicknessListPopWinBtn = (Button) findViewById(R.id.list_thickness_popwin);
        
        thicknessListPopWinBtn.setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View v)
            {
                if (flag)
                {
                    // 显示PopupWindow窗口
                    popupThickListWindwShowing();
                }
            }
        });
        
        
        
        // 初始化PopupWindow
        //initPopuWindow();
        button = (Button) findViewById(R.id.refresh);
        // 设置点击事件，恢复下拉框列表数据，没有什么作用，纯粹是为了方便多看几次效果而设置
        button.setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View v)
            {
                initDatas();
                optionsAdapter.notifyDataSetChanged();
            }
        });

    }

    /** * 初始化填充Adapter所用List数据 */
    private void initDatas()
    {
        datas.clear();
        datas.add("北京");
        datas.add("上海");
        datas.add("广州");
        datas.add("深圳");
        datas.add("重庆");
        datas.add("青岛");
        datas.add("石家庄");
    }

    /** * 初始化PopupWindow */
    private void initPopuListWindow()
    {
        initDatas();
        // PopupWindow浮动下拉框布局
        View selectPopWindowView = (View) this.getLayoutInflater().inflate(R.layout.select_list_popwindow, null);
        listView = (ListView) selectPopWindowView.findViewById(R.id.list);
        // 设置自定义Adapter
        optionsAdapter = new OptionAdapter(this, handler, datas);
        listView.setAdapter(optionsAdapter);
        selectPopupWindow = new PopupWindow(selectPopWindowView, pwidth, LayoutParams.WRAP_CONTENT, true);
        selectPopupWindow.setOutsideTouchable(true);
        /* 这一句是为了实现弹出PopupWindow后，当点击屏幕其他部分及Back键时PopupWindow会消失， */
        selectPopupWindow.setBackgroundDrawable(new BitmapDrawable());
    }
    
    
    private void initPopuThicknessListWindow()
    {
        // PopupWindow浮动下拉框布局
        View selectPopWindowView = (View) this.getLayoutInflater().inflate(R.layout.select_list_popwindow, null);
        listView = (ListView) selectPopWindowView.findViewById(R.id.list);
        // 设置自定义Adapter
        thicknessAdapter = new ThicknessAdapter(this);
        listView.setAdapter(thicknessAdapter);
        listView.setOnItemClickListener(new OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                    int position, long id) {
                Toast.makeText(me, "选择了：position:"+ position, Toast.LENGTH_LONG).show();
                selectPopupWindow.dismiss();
            }
        });
        selectPopupWindow = new PopupWindow(selectPopWindowView, pwidth, LayoutParams.WRAP_CONTENT, true);
        selectPopupWindow.setOutsideTouchable(true);
        /* 这一句是为了实现弹出PopupWindow后，当点击屏幕其他部分及Back键时PopupWindow会消失， */
        selectPopupWindow.setBackgroundDrawable(new BitmapDrawable());
    }
    
    private void initPopuGridWindow()
    {
        // PopupWindow浮动下拉框布局
        View selectPopWindowView = (View) this.getLayoutInflater().inflate(R.layout.select_grid_popwindow, null);
        gridView = (GridView) selectPopWindowView.findViewById(R.id.gridView1);
        // 设置自定义Adapter
        gridAdapter = new GridAdapter(this);
        gridView.setAdapter(gridAdapter);
        
        gridView.setOnItemClickListener(new OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                    int position, long id) {
                Toast.makeText(me, "选择了：position:"+ position, Toast.LENGTH_LONG).show();
                selectPopupWindow.dismiss();
            }
        });
        selectPopupWindow = new PopupWindow(selectPopWindowView, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, true);
        selectPopupWindow.setOutsideTouchable(true);
        /* 这一句是为了实现弹出PopupWindow后，当点击屏幕其他部分及Back键时PopupWindow会消失， */
        selectPopupWindow.setBackgroundDrawable(new BitmapDrawable());
    }

    public void popupListWindwShowing()
    {
        initPopuListWindow();
        selectPopupWindow.showAsDropDown(parent, 0, -3);
    }
    public void popupThickListWindwShowing()
    {
        initPopuThicknessListWindow();
        selectPopupWindow.showAsDropDown(parent, 0, -3);
    }
    public void popupGridWindwShowing(){
        initPopuGridWindow();
        selectPopupWindow.showAsDropDown(parent, 0, -3);
    }
    
    public void dismiss()
    {
        selectPopupWindow.dismiss();
    }

    @Override
    public boolean handleMessage(Message msg)
    {
        Bundle data = msg.getData();
        switch (msg.what)
        {
            case 1:
                int selIndex = data.getInt("selIndex");
                et.setText(datas.get(selIndex));
                dismiss();
                break;
            case 2:
                int delIndex = data.getInt("delIndex");
                datas.remove(delIndex);
                optionsAdapter.notifyDataSetChanged();
                break;
        }
        return false;
    }

    class OptionAdapter extends BaseAdapter
    {

        private List<String> items;
        private Context      ctx;
        private Handler      handler;

        public OptionAdapter(Context ctx, Handler handler, List<String> items)
        {
            this.ctx = ctx;
            this.items = items;
            this.handler = handler;
        }

        @Override
        public int getCount()
        {
            return this.items.size();
        }

        @Override
        public Object getItem(int position)
        {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public long getItemId(int position)
        {
            // TODO Auto-generated method stub
            return 0;
        }

        ViewHolder holder;

        @Override
        public View getView(int position, View convertView, ViewGroup parent)
        {
            if (convertView == null)
            {
                holder = new ViewHolder();
                convertView = LayoutInflater.from(ctx).inflate(R.layout.select_list_item, null);
                holder.itemTv = (TextView) convertView.findViewById(R.id.item);
                convertView.setTag(holder);
            } else
            {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.itemTv.setText(items.get(position));
            holder.itemTv.setOnClickListener(new OnClickListener()
            {

                @Override
                public void onClick(View v)
                {
                    CharSequence text = ((TextView) v).getText();
                    et.setText(text);
                }
            });
            return convertView;
        }

    }
    
    class ThicknessAdapter extends BaseAdapter{
        
        private int[] thicknessArray;
        private Context ctx;
        private ThicknessAdapter(Context ctx){
            this.ctx = ctx;
            thicknessArray = new int[5];
            thicknessArray[0] = 1;
            thicknessArray[1] = 2;
            thicknessArray[2] = 3;
            thicknessArray[3] = 4;
            thicknessArray[4] = 5;
        }
        
        @Override
        public int getCount()
        {
            return thicknessArray.length;
        }

        @Override
        public Object getItem(int position)
        {
            // TODO Auto-generated method stub
            return thicknessArray[position];
        }

        @Override
        public long getItemId(int position)
        {
            // TODO Auto-generated method stub
            return position;
        }
        ViewHolder3 holder3;

        @Override
        public View getView(int position, View convertView, ViewGroup parent)
        {
            if(convertView==null){
                holder3 = new ViewHolder3();
                convertView = LayoutInflater.from(ctx).inflate(R.layout.select_list_thickness_item, null);
                holder3.thicknessBtn = (Button)convertView.findViewById(R.id.thickness);
                convertView.setTag(holder3);
             }else{
                holder3 = (ViewHolder3)convertView.getTag();
             }
            final int thickness = this.thicknessArray[position];
            holder3.thicknessBtn.setHeight(thickness);
            return convertView;
        }
        
    }
    
    
    class ViewHolder3{
        Button thicknessBtn;
    }
    
    class GridAdapter extends BaseAdapter {  
        private int[] colors; 
        private Context      ctx;
        
        //construct  
        public GridAdapter(Context ctx) {  
            colors = new int[12];
            colors[0] = R.color.black;
            colors[1] = R.color.brown;
            colors[2] = R.color.orange;
            colors[3] = R.color.yellow;
            colors[4] = R.color.pink;
            colors[5] = R.color.purple;
            colors[6] = R.color.grey;
            colors[7] = R.color.cyan;
            colors[8] = R.color.white;
            colors[9] = R.color.red;
            colors[10] = R.color.green;
            colors[11] = R.color.blue;
            this.ctx = ctx;
        }  
          
        @Override  
        public int getCount() {  
            // TODO Auto-generated method stub  
            return colors.length;  
        }  
  
        @Override  
        public Object getItem(int position) {  
            // TODO Auto-generated method stub  
            return colors[position];  
        }  
  
        @Override  
        public long getItemId(int position) {  
            // TODO Auto-generated method stub  
            return position;  
        }  
  
        @Override  
        public View getView(final int position, View convertView, ViewGroup parent) {  
            // TODO Auto-generated method stub  
            if(convertView==null){
               holder2 = new ViewHolder2();
               convertView = LayoutInflater.from(ctx).inflate(R.layout.select_grid_item, null);
               holder2.imageView = (Button)convertView.findViewById(R.id.imageView1);
               convertView.setTag(holder2);
            }else{
               holder2 = (ViewHolder2)convertView.getTag();
            }
            convertView.setLayoutParams(new GridView.LayoutParams(50, 50));
            final Resources res = this.ctx.getResources(); 
            /*Bitmap bitmap = BitmapFactory.decodeResource(res,R.drawable.ic_launcher);  
            holder2.imageView.setImageBitmap(bitmap);*/
            holder2.imageView.setBackgroundColor(res.getColor(colors[position]));
            holder2.imageView.setText("  ");
            convertView.setOnTouchListener(new OnTouchListener()
            {
                @Override
                public boolean onTouch(View v, MotionEvent event)
                {
                    if(event.getAction() == MotionEvent.ACTION_DOWN){
                    }else if(event.getAction() == MotionEvent.ACTION_UP){
                        gridPopWinBtn.setBackgroundColor(res.getColor(colors[position]));
                        selectPopupWindow.dismiss();
                    }
                    return false;
                }
            });
            return convertView;  
        }  
        
        ViewHolder2 holder2;
    }
    class ViewHolder
    {
        public TextView itemTv;
    }
    class ViewHolder2
    {
        public Button imageView;
    }

}
