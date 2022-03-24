package com.htnova.android.accessapp;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.htnova.access.pojo.dto.DeviceDataDto;
import com.htnova.access.pojo.dto.ModCwaDto;
import com.htnova.access.pojo.dto.ModSmokeDto;
import com.htnova.access.pojo.dto.ModSysDto;
import com.htnova.access.pojo.dto.ModTvocDto;
import com.htnova.access.pojo.dto.SensorData;

import java.util.List;

public class MainViewHandler {
    private static Activity targetActivity;
    private static View[] cwaModStateViews;
    private static View[] tvocModStateViews;
    private static View[] smokeModStateViews;
    private static int[] cwaModStateImageIds;
    private static int[] tvocModStateImageIds;
    private static int[] smokeModStateImageIds;
    private static TextView[] cwaValuesTextView;
    private static TextView[] cwaUnitsTextView;
    private static TextView[] tvocValuesTextView;
    private static TextView[] tvocUnitsTextView;
    private static TextView[] tvocAlarmTypesTextView;
    private static TextView[] smokeValuesTextView;
    private static TextView[] smokeUnitsTextView;

    public MainViewHandler(Activity targetActivity){
        this.targetActivity = targetActivity;

        // 获取模块状态对应的视图控件。
        cwaModStateViews = new View[]{targetActivity.findViewById(R.id.cwaModIcon), targetActivity.findViewById(R.id.cwaModText)};
        cwaModStateImageIds = new int[]{R.drawable.cwa_mod_icon0, R.drawable.cwa_mod_icon1, R.drawable.cwa_mod_icon2, R.drawable.cwa_mod_icon3};

        tvocModStateViews = new View[]{targetActivity.findViewById(R.id.tvocModIcon), targetActivity.findViewById(R.id.tvocModText)};
        tvocModStateImageIds = new int[]{R.drawable.tvoc_mod_icon0, R.drawable.tvoc_mod_icon1, R.drawable.tvoc_mod_icon2, R.drawable.tvoc_mod_icon3};

        smokeModStateViews = new View[]{targetActivity.findViewById(R.id.smokeModIcon), targetActivity.findViewById(R.id.smokeModText)};
        smokeModStateImageIds = new int[]{R.drawable.smoke_mod_icon0, R.drawable.smoke_mod_icon1, R.drawable.smoke_mod_icon2, R.drawable.smoke_mod_icon3};

        // 获取模块数据对应的视图控件。
        cwaValuesTextView = new TextView[7];
        int[] cwaValueIds = new int[]{R.id.cwaValue1Text, R.id.cwaValue2Text, R.id.cwaValue3Text, R.id.cwaValue4Text, R.id.cwaValue5Text, R.id.cwaValue6Text, R.id.cwaValue7Text};
        for(int i = 0; i < cwaValueIds.length; i++){
            cwaValuesTextView[i] = targetActivity.findViewById(cwaValueIds[i]);
        }

        cwaUnitsTextView = new TextView[7];
        int[] cwaUnitIds = new int[]{R.id.cwaUnit1Text, R.id.cwaUnit2Text, R.id.cwaUnit3Text, R.id.cwaUnit4Text, R.id.cwaUnit5Text, R.id.cwaUnit6Text, R.id.cwaUnit7Text};
        for(int i = 0; i < cwaUnitIds.length; i++){
            cwaUnitsTextView[i] = targetActivity.findViewById(cwaUnitIds[i]);
        }

        tvocValuesTextView = new TextView[12];
        int[] tvocValueIds = new int[]{R.id.tvocValue1Text, R.id.tvocValue2Text, R.id.tvocValue3Text, R.id.tvocValue4Text,
                R.id.tvocValue5Text, R.id.tvocValue6Text, R.id.tvocValue7Text, R.id.tvocValue8Text,
                R.id.tvocValue9Text, R.id.tvocValue10Text, R.id.tvocValue11Text, R.id.tvocValue12Text};
        for(int i = 0; i < tvocValueIds.length; i++){
            tvocValuesTextView[i] = targetActivity.findViewById(tvocValueIds[i]);
        }

        tvocUnitsTextView = new TextView[12];
        int[] tvocUnitIds = new int[]{R.id.tvocUnit1Text, R.id.tvocUnit2Text, R.id.tvocUnit3Text, R.id.tvocUnit4Text,
                R.id.tvocUnit5Text, R.id.tvocUnit6Text, R.id.tvocUnit7Text, R.id.tvocUnit8Text,
                R.id.tvocUnit9Text, R.id.tvocUnit10Text, R.id.tvocUnit11Text, R.id.tvocUnit12Text};
        for(int i = 0; i < tvocUnitIds.length; i++){
            tvocUnitsTextView[i] = targetActivity.findViewById(tvocUnitIds[i]);
        }

        tvocAlarmTypesTextView = new TextView[12];
        int[] tvocAlarmTypeIds = new int[]{R.id.tvocAlarmType1Text, R.id.tvocAlarmType2Text, R.id.tvocAlarmType3Text, R.id.tvocAlarmType4Text,
                R.id.tvocAlarmType5Text, R.id.tvocAlarmType6Text, R.id.tvocAlarmType7Text, R.id.tvocAlarmType8Text,
                R.id.tvocAlarmType9Text, R.id.tvocAlarmType10Text, R.id.tvocAlarmType11Text, R.id.tvocAlarmType12Text};
        for(int i = 0; i < tvocAlarmTypeIds.length; i++){
            tvocAlarmTypesTextView[i] = targetActivity.findViewById(tvocAlarmTypeIds[i]);
        }

        smokeValuesTextView = new TextView[7];
        int[] smokeValueIds = new int[]{R.id.smokeValue1Text, R.id.smokeValue2Text, R.id.smokeValue3Text, R.id.smokeValue4Text, R.id.smokeValue5Text, R.id.smokeValue6Text, R.id.smokeValue7Text};
        for(int i = 0; i < smokeValueIds.length; i++){
            smokeValuesTextView[i] = targetActivity.findViewById(smokeValueIds[i]);
        }

        smokeUnitsTextView = new TextView[7];
        int[] smokeUnitIds = new int[]{R.id.smokeUnit1Text, R.id.smokeUnit2Text, R.id.smokeUnit3Text, R.id.smokeUnit4Text, R.id.smokeUnit5Text, R.id.smokeUnit6Text, R.id.smokeUnit7Text};
        for(int i = 0; i < smokeUnitIds.length; i++){
            smokeUnitsTextView[i] = targetActivity.findViewById(smokeUnitIds[i]);
        }
    }

    public void updateView(DeviceDataDto deviceDataDto) {
        // 取出模块的数据，然后显示相应数据。
        ModCwaDto cwaDto = deviceDataDto.getCwaMod();
        ModTvocDto tvocDto = deviceDataDto.getTvocMod();
        ModSmokeDto smokeDto = deviceDataDto.getSmokeMod();
        ModSysDto sysDto = deviceDataDto.getSysMod();
        if(cwaDto == null || tvocDto == null || smokeDto == null || sysDto == null){
            return;
        }

        // 设备SN号。
        TextView deviceSnView = (TextView) targetActivity.findViewById(R.id.HTId);
        deviceSnView.setText(deviceDataDto.getSn());

        // 温度。
        TextView temperatureView = (TextView) targetActivity.findViewById(R.id.temperature);
        temperatureView.setText(Float.toString(cwaDto.getTemp()));

        // 湿度。
        TextView humidityView = (TextView) targetActivity.findViewById(R.id.humidity);
        humidityView.setText(Float.toString(cwaDto.getHumidity()));

        // 更新模块的报警状态。
        updateModState(cwaDto.getState(), cwaModStateViews, cwaModStateImageIds);
        updateModState(tvocDto.getState(), tvocModStateViews, tvocModStateImageIds);
        updateModState(smokeDto.getState(), smokeModStateViews, smokeModStateImageIds);

        // 更新传感器显示的数据。
        updateSensor(cwaDto.getSensors(), cwaValuesTextView, cwaUnitsTextView, null);
        updateSensor(tvocDto.getSensors(), tvocValuesTextView, tvocUnitsTextView, tvocAlarmTypesTextView);
        updateSensor(smokeDto.getSensors(), smokeValuesTextView, smokeUnitsTextView, null);
    }

    private void updateModState(int state, View[] modStateViews, int[] modImageIds){
        // 化学战剂模块的报警状态，文字和图标的颜色。
        // 0-正常，1-预报，2-低报，3-高报。
        //（正常）绿色 #00f28e
        //（预警）黄色 #ff8f17
        //（低报）红色 #ff4847
        //（高报）紫色 #bd30e6
        ImageView imageView = (ImageView) modStateViews[0];
        TextView textView = (TextView) modStateViews[1];

        imageView.setImageResource(modImageIds[0]);
        if(state == 0){
            textView.setTextColor(getColor(R.color.stateNormal));
        }
        if(state == 1){
            textView.setTextColor(getColor(R.color.statePreAlarm));
        }
        if (state == 2){
            textView.setTextColor(getColor(R.color.stateLowAlarm));
        }
        if(state == 3){
            textView.setTextColor(getColor(R.color.stateHighAlarm));
        }
    }

    private void updateSensor(List<SensorData> sensorDatas, TextView[] valuesTextView, TextView[] unitsTextView, TextView[] alarmTypesTextView){
        if(sensorDatas != null && sensorDatas.size() > 0){
            int sensorCount = sensorDatas.size();
            int viewControlLength = valuesTextView.length;
            for(int i = 0; i < sensorCount; i++){
                // 传感器列表小于视图控件列表。
                if(i >= viewControlLength){
                    break;
                }

                SensorData sensorData = sensorDatas.get(i);
                valuesTextView[i].setText(sensorData.getName() + ":" + sensorData.getValue());
                unitsTextView[i].setText(sensorData.getUnitName());
                if(sensorData.getAlarmState() == 0){
                    valuesTextView[i].setTextColor(getColor(R.color.stateNormal));
                }
                if(sensorData.getAlarmState() == 1){
                    valuesTextView[i].setTextColor(getColor(R.color.statePreAlarm));
                }
                if(sensorData.getAlarmState() == 2){
                    valuesTextView[i].setTextColor(getColor(R.color.stateLowAlarm));
                }
                if(sensorData.getAlarmState() == 3){
                    valuesTextView[i].setTextColor(getColor(R.color.stateHighAlarm));
                }
                if(alarmTypesTextView != null){
                    if(sensorData.getAlarmTypeLevels() != null && sensorData.getAlarmTypeLevels().size() > 0){
                        StringBuilder buf = new StringBuilder();
                        sensorData.getAlarmTypeLevels().forEach(tempAlarmType->{
                            buf.append(tempAlarmType).append(" ");
                        });
                        if(buf.length() > 0){
                            buf.deleteCharAt(buf.length() - 1);
                        }
                        alarmTypesTextView[i].setVisibility(View.VISIBLE);
                        alarmTypesTextView[i].setText(buf.toString());
                    }else{
                        alarmTypesTextView[i].setVisibility(View.GONE);
                    }
                }
            }
        }
    }

    private int getColor(int colorResId){
        Context context = targetActivity.getApplicationContext();
        int currColorId = ContextCompat.getColor(context, colorResId);
        return currColorId;
    }
}