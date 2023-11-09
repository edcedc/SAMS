package com.yyc.smas.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.StringUtils;
import com.yyc.smas.R;
import com.yyc.smas.util.GlideLoadingUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * @Author nike
 * @Date 2023/6/9 15:03
 * @Description
 */
public class AssetTextAdapter2 extends BaseAdapter {

    private Context act;
    private JSONArray jsonArray;

    public AssetTextAdapter2(Context act, JSONArray jsonArray) {
        this.act = act;
        this.jsonArray = jsonArray;
    }


    @Override
    public int getCount() {
        return jsonArray.length();
    }

    @Override
    public Object getItem(int i) {
        return jsonArray.optJSONObject(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int po, View convertView, ViewGroup viewGroup) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = convertView.inflate(act, R.layout.i_text, null);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        try {
            JSONObject object = jsonArray.getJSONObject(po);

            String title = object.optString("title");
//            viewHolder.tv_text.setText(title + "：" + object.optString("text"));

            if (title.equals("InventoryStatus")) {
                if (object.optInt("text") == 1) {
                    viewHolder.tv_text.setText(act.getText(R.string.inventory_status) + "：" + act.getText(R.string.found));
                } else {
                    viewHolder.tv_text.setText(act.getText(R.string.inventory_status) + "：" + act.getText(R.string.missing));
                }
            }
            if (title.equals("scanStatus")) {
                if (object.optInt("text") == 116) {
                    viewHolder.tv_text.setText(act.getText(R.string.scan_status) + "：RFID");
                } else if (object.optInt("text") == 117) {
                    viewHolder.tv_text.setText(act.getText(R.string.scan_status) + "：QRCode");
                } else if (object.optInt("text") == 118) {
                    viewHolder.tv_text.setText(act.getText(R.string.scan_status) + "：Manually");
                } else {
                    viewHolder.tv_text.setText(act.getText(R.string.scan_status) + "：");
                }
            }
            if (title.equals("FoundStatus")) {
                if (object.optInt("text") == 116) {
                    viewHolder.tv_text.setText(act.getText(R.string.found_status) + "：RFID");
                } else if (object.optInt("text") == 117) {
                    viewHolder.tv_text.setText(act.getText(R.string.found_status) + "：QRCode");
                } else if (object.optInt("text") == 118) {
                    viewHolder.tv_text.setText(act.getText(R.string.found_status) + "：Manually");
                } else {
                    viewHolder.tv_text.setText(act.getText(R.string.found_status) + "：");
                }
            }

            if (title.equals("ScanDate")) {
                viewHolder.tv_text.setText(act.getText(R.string.scan_date) + "：" + object.optString("text"));
            }

            if (title.equals("Time")) {
                String time = object.optString("text");
                if (time.contains("00:00:00")) {
                    time = time.substring(0, 10);
                } else {
                    time = time.replace("T", " ");
                }
                viewHolder.tv_text.setText(act.getText(R.string.scan_date) + "：" + time);
            }

            if (title.equals("ScanUser")) {
                viewHolder.tv_text.setText(act.getText(R.string.scan_date) + "：" + object.optString("text"));
            }

            if (title.equals("LabelTag")) {
                viewHolder.tv_text.setText(act.getText(R.string.label_tag1) + "：" + object.optString("text"));
            }

            if (title.equals("scanTime")) {
                viewHolder.tv_text.setText(act.getText(R.string.scan_time) + "：" + object.optString("text"));
            }

            if (title.equals("Location")) {
                viewHolder.tv_text.setText(act.getText(R.string.location) + "：" + object.optString("text"));
            }

            if (title.equals("Remarks")) {
                viewHolder.tv_text.setText(act.getText(R.string.remarks) + "：" + object.optString("text"));
            }

            if (title.equals("Title")) {
                viewHolder.tv_text.setText(act.getText(R.string.title) + "：" + object.optString("text"));
            }

            if (title.equals("companyId")) {
                viewHolder.tv_text.setText(act.getText(R.string.companyid) + "：" + object.optString("text"));
            }

            if (title.equals("Language")) {
                viewHolder.tv_text.setText(act.getText(R.string.language) + "：" + object.optString("text"));
            }

            if (title.equals("ArchivesType")) {
                viewHolder.tv_text.setText(act.getText(R.string.archives_type) + "：" + object.optString("text"));
            }

            if (title.equals("DisposalModel")) {
                viewHolder.tv_text.setText(act.getText(R.string.disposal_model) + "：" + object.optString("text"));
            }

            if (title.equals("LevelType")) {
                viewHolder.tv_text.setText(act.getText(R.string.level_type) + "：" + object.optString("text"));
            }

            if (title.equals("ArchivesNo")) {
                viewHolder.tv_text.setText(act.getText(R.string.record_group) + "：" + object.optString("text"));
            }

            if (title.equals("LibraryCallNo")) {
                viewHolder.tv_text.setText(act.getText(R.string.library_call_no) + "：" + object.optString("text"));
            }

            if (title.equals("Editions_Year") || title.equals("ArchivesYear")) {
                viewHolder.tv_text.setText(act.getText(R.string.editions_year) + "：" + object.optString("text"));
            }

            if (title.equals("Img")) {
                if (StringUtils.isEmpty(object.optString("text"))) {
                    viewHolder.iv_image.setVisibility(View.GONE);
                    viewHolder.tv_text.setVisibility(View.GONE);
                } else {
                    GlideLoadingUtils.load(act, object.optString("text"), viewHolder.iv_image);
                    viewHolder.tv_text.setVisibility(View.GONE);
                    viewHolder.iv_image.setVisibility(View.VISIBLE);
                }
            } else {
                viewHolder.iv_image.setVisibility(View.GONE);
                viewHolder.tv_text.setVisibility(View.VISIBLE);
            }

            if (title.equals("Borrowstatus")) {
                if (object.optInt("text") == 0) {
                    viewHolder.tv_text.setText(act.getText(R.string.borrowing_status) + "：" + act.getText(R.string.borrowing0));
                } else {
                    viewHolder.tv_text.setText(act.getText(R.string.borrowing_status) + "：" + act.getText(R.string.borrowing1));
                }
            }

            if (title.equals("Author")) {
                StringBuffer sb = new StringBuffer();
                String text = object.optString("text");
                if (text.contains("\n")) {
                    String[] split = text.split("\n");
                    for (int i = 0; i < split.length; i++) {
                        String s = split[0];
                        sb.append(s).append(" ");
                        // 在这里进行处理
                    }
                }
                viewHolder.tv_text.setText(act.getText(R.string.author) + "：" + sb.toString());
            }

            convertView.setOnClickListener(view -> {
                    if (po == jsonArray.length() - 1){

                    }
                }
            );

        } catch (JSONException e) {
            throw new RuntimeException("InventoryDesc2Adapter go go go");
        }
        return convertView;
    }

    class ViewHolder {

        AppCompatTextView tv_text;
        AppCompatImageView iv_image;
        View layout;

        public ViewHolder(View convertView) {
            tv_text = convertView.findViewById(R.id.tv_text);
            iv_image = convertView.findViewById(R.id.iv_image);
            layout = convertView.findViewById(R.id.layout);
        }

    }

}
