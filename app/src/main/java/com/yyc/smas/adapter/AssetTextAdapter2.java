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

import java.util.HashMap;
import java.util.Map;

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
            viewHolder=new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        try {
            JSONObject object = jsonArray.getJSONObject(po);

            String title = object.optString("title");
//            viewHolder.tv_text.setText(title + "：" + object.optString("text"));

            if (title.equals("InventoryStatus")){
                if (object.optInt("text") == 1){
                    viewHolder.tv_text.setText(act.getText(R.string.inventory_status) + "：" + act.getText(R.string.found));
                }else {
                    viewHolder.tv_text.setText(act.getText(R.string.inventory_status) + "：" + act.getText(R.string.missing));
                }
            }
            if (title.equals("scanStatus") || title.equals("FoundStatus")){
                if (object.optInt("text") == 116){
                    viewHolder.tv_text.setText(act.getText(R.string.scan_status) + "：RFID");
                }else if (object.optInt("text") == 117){
                    viewHolder.tv_text.setText(act.getText(R.string.scan_status) + "：QRCode");
                }else if (object.optInt("text") == 118){
                    viewHolder.tv_text.setText(act.getText(R.string.scan_status) + "：Manually");
                }else {
                    viewHolder.tv_text.setText(act.getText(R.string.scan_status) + "：");
                }
            }

            if (title.equals("Time") || title.equals("ScanDate") || title.equals("scanTime")){
                String time = object.optString("text");
                if (time.contains("00:00:00")){
                    time = time.substring(0, 10);
                }else if (time.contains("T")){
                    time = time.replace("T", " ");
                }else {

                }
                viewHolder.tv_text.setText(act.getText(R.string.scan_date) + "：" + time);
            }

            if (title.equals("ScanUser")){
                viewHolder.tv_text.setText(act.getText(R.string.scan_date) + "：" + object.optString("text"));
            }

            if (title.equals("LabelTag")){
                viewHolder.tv_text.setText(act.getText(R.string.label_tag1) + "：" + object.optString("text"));
            }



            if (title.equals("Location")){
                viewHolder.tv_text.setText(act.getText(R.string.location) + "：" + object.optString("text"));
            }

            if (title.equals("Remarks")){
                viewHolder.tv_text.setText(act.getText(R.string.remarks) + "：" + object.optString("text"));
            }

            if (title.equals("Title")){
                viewHolder.tv_text.setText(act.getText(R.string.title) + "：" + object.optString("text"));
            }

            if (title.equals("companyId")){
                viewHolder.tv_text.setText(act.getText(R.string.companyid) + "：" + object.optString("text"));
            }

            if (title.equals("Language")){
                viewHolder.tv_text.setText(act.getText(R.string.language) + "：" + object.optString("text"));
            }

            if (title.equals("ArchivesType")){
                viewHolder.tv_text.setText(act.getText(R.string.archives_type) + "：" + object.optString("text"));
            }

            if (title.equals("DisposalModel")){
                viewHolder.tv_text.setText(act.getText(R.string.disposal_model) + "：" + object.optString("text"));
            }

            if (title.equals("LevelType")){
                viewHolder.tv_text.setText(act.getText(R.string.level_type) + "：" + object.optString("text"));
            }

            if (title.equals("ArchivesNo")){
                viewHolder.tv_text.setText(act.getText(R.string.record_group) + "：" + object.optString("text"));
            }

            if (title.equals("LibraryCallNo")){
                viewHolder.tv_text.setText(act.getText(R.string.library_call_no) + "：" + object.optString("text"));
            }

            if (title.equals("Editions_Year") || title.equals("ArchivesYear")){
                viewHolder.tv_text.setText(act.getText(R.string.editions_year) + "：" + object.optString("text"));
            }

            if (title.equals("Img")){
                viewHolder.tv_text.setText(act.getText(R.string.images) + "：");
                if (!StringUtils.isEmpty(object.optString("text")) && !object.optString("text").equals("null")){
                    viewHolder.iv_image.setVisibility(View.VISIBLE);
                    GlideLoadingUtils.load(act, object.optString("text"), viewHolder.iv_image);
                }
            }else {
                viewHolder.iv_image.setVisibility(View.GONE);
            }



            if (title.equals("Author")){
                String text = object.optString("text");
                if (text.contains("\n")){
                    String replace = text.replace("\n", " | ");
                    viewHolder.tv_text.setText(act.getText(R.string.author) + "：" + replace);
                }
            }

            if (title.equals("AssetNo")){
                viewHolder.tv_text.setText(act.getText(R.string.system_no) + "：" + object.optString("text"));
            }

            if (title.equals("Type")){
                viewHolder.tv_text.setText(act.getText(R.string.archives_type) + "：" + object.optString("text"));
            }

            if (title.equals("BishopName")){
                viewHolder.tv_text.setText(act.getText(R.string.bishop_name) + "：" + object.optString("text"));
            }

            Map<Integer, Integer> statusMap = new HashMap<>();
            statusMap.put(1, R.string.status_1);
            statusMap.put(2, R.string.status_2);
            statusMap.put(3, R.string.status_3);
            statusMap.put(4, R.string.status_4);
            statusMap.put(5, R.string.status_5);
            statusMap.put(6, R.string.status_6);
            statusMap.put(7, R.string.status_7);
            statusMap.put(8, R.string.status_8);
            statusMap.put(9, R.string.status_9);
            statusMap.put(10, R.string.status_10);

            statusMap.put(20, R.string.status_20);
            statusMap.put(21, R.string.status_21);
            statusMap.put(22, R.string.status_22);
            statusMap.put(23, R.string.status_23);
            statusMap.put(24, R.string.status_24);

            statusMap.put(100, R.string.status_100);
            statusMap.put(101, R.string.status_101);
            statusMap.put(102, R.string.status_102);
            statusMap.put(103, R.string.status_103);
            statusMap.put(104, R.string.status_104);
            statusMap.put(105, R.string.status_105);
            statusMap.put(106, R.string.status_106);
            statusMap.put(107, R.string.status_107);

            statusMap.put(110, R.string.status_110);
            statusMap.put(111, R.string.status_111);
            statusMap.put(112, R.string.status_112);
            statusMap.put(116, R.string.status_116);
            statusMap.put(117, R.string.status_117);
            statusMap.put(118, R.string.status_118);

            statusMap.put(200, R.string.status_200);
            statusMap.put(201, R.string.status_201);
            statusMap.put(202, R.string.status_202);

            statusMap.put(210, R.string.status_210);
            statusMap.put(211, R.string.status_211);
            statusMap.put(212, R.string.status_212);

            statusMap.put(220, R.string.status_220);
            statusMap.put(225, R.string.status_225);
            statusMap.put(226, R.string.status_226);
            statusMap.put(227, R.string.status_227);

            statusMap.put(230, R.string.status_230);
            statusMap.put(231, R.string.status_231);
            statusMap.put(232, R.string.status_232);

            statusMap.put(240, R.string.status_240);
            statusMap.put(241, R.string.status_241);
            statusMap.put(242, R.string.status_242);

            statusMap.put(250, R.string.status_250);
            statusMap.put(251, R.string.status_251);
            statusMap.put(252, R.string.status_252);

            statusMap.put(260, R.string.status_260);
            statusMap.put(261, R.string.status_261);
            statusMap.put(262, R.string.status_262);
            statusMap.put(263, R.string.status_263);
            statusMap.put(264, R.string.status_264);
            statusMap.put(265, R.string.status_265);
            statusMap.put(266, R.string.status_266);

            statusMap.put(300, R.string.status_300);
            statusMap.put(301, R.string.status_301);
            statusMap.put(302, R.string.status_302);
            statusMap.put(303, R.string.status_303);
            statusMap.put(304, R.string.status_304);

            if (title.equals("StatusId") || title.equals("StatusID")){
                int text = object.optInt("text");
                if (statusMap.containsKey(text)) {
                    viewHolder.tv_text.setText(act.getText(R.string.status) + "：" + act.getText(statusMap.get(text)));
                }
            }

            Map<Integer, Integer> borrowStatusMap = new HashMap<>();
            borrowStatusMap.put(20, R.string.status_20);
            borrowStatusMap.put(21, R.string.status_21);
            borrowStatusMap.put(22, R.string.status_22);
            borrowStatusMap.put(23, R.string.status_23);
            borrowStatusMap.put(24, R.string.status_24);

            if (title.equals("Borrowstatus") || title.equals("BorrowStatus")){
                int text = object.optInt("text");
                if (borrowStatusMap.containsKey(text)) {
                    viewHolder.tv_text.setText(act.getText(R.string.borrowing_status) + "：" + act.getText(borrowStatusMap.get(text)));
                }
            }

            convertView.setOnClickListener(view -> {
                LogUtils.e(object);
            });

        } catch (JSONException e) {
            throw new RuntimeException("InventoryDesc2Adapter go go go");
        }
        return convertView;
    }

    class ViewHolder{

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
