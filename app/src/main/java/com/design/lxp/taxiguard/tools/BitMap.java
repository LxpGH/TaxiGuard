package com.design.lxp.taxiguard.tools;

import android.graphics.*;
import com.design.lxp.taxiguard.R;

public class BitMap {

    public  Bitmap getOvalBitmap(Bitmap bitmap) {//图片剪切成圆形
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap
                .getHeight(), Bitmap.Config.ARGB_8888);//将图片转换成bitmap
        Canvas canvas = new Canvas(output);//创建画布
        final int color = 0xff424242;
        final Paint paint = new Paint();//创建画刷
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());//创建剪切区域
        final RectF rectF = new RectF(rect);//将剪切的矩形区域转换成浮点型矩形区域
        paint.setAntiAlias(true);//设置成抗锯齿
        canvas.drawARGB(0, 0, 0, 0);//设置画布背景
        paint.setColor(color);

        canvas.drawOval(rectF, paint);//根据矩形区域剪切画圆形

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));//设置画刷的src和dst区域的混合模式
        canvas.drawBitmap(bitmap, rect, rect, paint);//根据参数进行剪切源图片区域并转换成bitmap格式
        return output;
    }
}
