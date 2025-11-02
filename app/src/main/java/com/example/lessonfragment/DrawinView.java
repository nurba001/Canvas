package com.example.lessonfragment;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.NonNull;

import android.graphics.Color;
import android.graphics.Path;
import android.os.Handler;
import java.util.logging.LogRecord;

public class DrawinView extends View {

    private Paint housePaint;
    private Paint roofPaint;
    private Paint windowPaint;
    private Path roofPath;
    private Paint cloudPaint;
    private float cloud1X, cloud1Y; //  Позиция первого облака
    private float cloud2X, cloud2Y; //  Позиция второго облака
    private float cloudSpeed = 1f; //  Скорость движения облаков
    private Handler handler; // Для движения
    private Runnable runnable; //  Задача для выполнения анимации

    // Конструкторы (необходимы для корректной работы View)
    public DrawinView(Context context) {
        super(context);
        init();
    }

    public DrawinView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }



    // Инициализация стилей (Paint)
    private void init() {
        // Краска для стен дома
        housePaint = new Paint();
        housePaint.setColor(Color.rgb(200, 150, 100)); // Коричневатый цвет стен
        housePaint.setStyle(Paint.Style.FILL);

        // Краска для крыши
        roofPaint = new Paint();
        roofPaint.setColor(Color.RED);
        roofPaint.setStyle(Paint.Style.FILL);

        // Краска для окна
        windowPaint = new Paint();
        windowPaint.setColor(Color.CYAN); // Голубой цвет для стекла
        windowPaint.setStyle(Paint.Style.FILL);


        // --- 1. Рисуем облака ---

        // Краска для облаков (белый цвет)
        cloudPaint = new Paint();
        cloudPaint.setColor(Color.WHITE);
        cloudPaint.setStyle(Paint.Style.FILL);

        // Создаем объект Path для рисования произвольных фигур (крыша)
        roofPath = new Path();

        // --- Инициализация позиций облаков ---
        // Первое облако (начинается слева, немного выше)
        cloud1X = -100; // Начинаем за пределами экрана слева
        cloud1Y = 100;

        // Второе облако (начинается правее, немного ниже)
        cloud2X = getWidth() + 50; // Начинаем за пределами экрана справа
        cloud2Y = 150;

        // --- Настройка движения ---
        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                // Обновляем позиции облаков
                updateCloudPositions();
                // Запрашиваем перерисовку View
                invalidate();
                // Повторяем это через небольшой промежуток времени
                handler.postDelayed(this, 20); // Обновление каждые 20 миллисекунд

            }
        };
    }

    // Метод для обновления позиций облаков
    private void updateCloudPositions() {
        // Двигаем первое облако вправо
        cloud1X += cloudSpeed;
        // Если облако ушло за правый край, возвращаем его на левый
        if (cloud1X > getWidth() + 100) { // +100 чтобы облако полностью ушло за край
            cloud1X = -100; // Начинаем снова слева
        }

        // Двигаем второе облако вправо (можно задать другую скорость или направление)
        cloud2X += cloudSpeed * 0.7f; // Второе облако движется чуть медленнее
        if (cloud2X > getWidth() + 150) {
            cloud2X = -150;
        }
    }
    private void drawCloud(Canvas canvas, float x, float y) {

        float ovalWidth = 80f; // Ширина овала
        float ovalHeight = 60f; // Высота овала

        // Рисуем первый овал
        // drawOval(left, top, right, bottom, paint)
        canvas.drawOval(x, y, x + ovalWidth, y + ovalHeight, cloudPaint);

        // Рисуем второй, перекрывающийся овал (больше и выше)
        canvas.drawOval(
                x + ovalWidth * 0.4f,
                y - ovalHeight * 0.6f,
                x + ovalWidth * 1.8f,
                y + ovalHeight * 0.4f,
                cloudPaint
        );

        // Рисуем третий, меньший овал справа
        canvas.drawOval(
                x + ovalWidth * 1.2f,
                y + ovalHeight * 0.2f,
                x + ovalWidth * 2.2f,
                y + ovalHeight,
                cloudPaint
        );
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);

        // Определяем основные размеры и позиции
        int width = getWidth();
        int height = getHeight();

        // Координаты центра View
        float centerX = width / 2f;
        float houseWidth = 300f; // Ширина дома
        float houseHeight = 250f; // Высота стен


        //небо
        canvas.drawColor(Color.rgb(135, 206, 235)); // Светло-голубое небо

        // Координаты стен
        float wallLeft = centerX - houseWidth / 2;
        float wallTop = (float) height / 2 - houseHeight / 2 + 50; // Сдвиг вниз, чтобы вместить крышу
        float wallRight = centerX + houseWidth / 2;
        float wallBottom = wallTop + houseHeight;

        canvas.drawRect(wallLeft, wallTop, wallRight, wallBottom, housePaint);

        canvas.save();
        // --- 2. Рисуем КРЫШУ (Треугольник с помощью Path) ---
        float roofHeight = 100f;

        // Определяем точки треугольника
        float roofPeakX = centerX;
        float roofPeakY = wallTop - roofHeight; // Верхняя точка

        // Перемещаем Path к первой точке (вершина)
        roofPath.moveTo(roofPeakX, roofPeakY);

        // Рисуем линию к левому нижнему углу крыши
        roofPath.lineTo(wallLeft - 20, wallTop);

        // Рисуем линию к правому нижнему углу крыши
        roofPath.lineTo(wallRight + 20, wallTop);

        // Закрываем контур, проводя линию обратно к вершине
        roofPath.close();

        // Рисуем Path на Canvas
        canvas.drawPath(roofPath, roofPaint);

        // Очищаем Path для будущих использований
        roofPath.reset();

        canvas.restore(); ////

        canvas.save();
        // --- 3. Рисуем КРУГЛЫЕ ОКНА ---

        float windowRadius = 40f; // Радиус окна
        float windowMargin = 70f; // Отступ от краев стен

        // 3.1. Левое окно
        // X-координата центра: Левый край + Отступ + Радиус
        float centerCircleX1 = wallLeft + windowMargin;
        // Y-координата центра: Верхний край + Отступ + Радиус
        float centerCircleY = wallTop + windowMargin;

        // Метод drawCircle принимает: X-центра, Y-центра, Радиус, Paint
        canvas.drawCircle(centerCircleX1, centerCircleY, windowRadius, windowPaint);

        // 3.2. Правое окно
        // X-координата центра: Правый край - Отступ - Радиус
        float centerCircleX2 = wallRight - windowMargin;

        canvas.drawCircle(centerCircleX2, centerCircleY, windowRadius, windowPaint);

        canvas.restore();
        //Рисуем ДВИЖУЩИЕСЯ ОБЛАКА (НОВЫЙ БЛОК) ---
        drawCloud(canvas, cloud1X, cloud1Y);
        drawCloud(canvas, cloud2X, cloud2Y);
    }
    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        // Запускаем анимацию
        handler.post(runnable);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        // Останавливаем анимацию
        handler.removeCallbacks(runnable);
    }
}






